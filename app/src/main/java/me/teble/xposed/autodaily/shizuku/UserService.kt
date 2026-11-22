package me.teble.xposed.autodaily.shizuku

import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.os.Process
import android.os.SystemClock
import android.util.AtomicFile
import android.util.Log
import kotlinx.collections.immutable.persistentHashMapOf
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.IHostCallback
import me.teble.xposed.autodaily.IHostBootstrap
import me.teble.xposed.autodaily.IHostSession
import me.teble.xposed.autodaily.IUserService
import me.teble.xposed.autodaily.utils.parse
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.UUID
import kotlin.system.exitProcess

/**
 * 以 Shizuku 授予的身份管理宿主会话、任务保护和预唤醒计划。
 *
 * @param context 模块应用上下文
 */
class UserService(private val context: Context) : IUserService.Stub() {
    private val guard = Any()
    private val worker = Executors.newScheduledThreadPool(2)
    private val moduleUid = context.applicationInfo.uid
    private val userId = moduleUid / 100000
    private val providerWake = ProviderWakeController(userId)
    private val daemonInfo = DaemonBuild.read(context).apply {
        putString("daemonInstance", UUID.randomUUID().toString())
        putInt("daemonUid", Process.myUid())
        putInt("daemonPid", Process.myPid())
        putLong("daemonStartedAt", System.currentTimeMillis())
        putLong("daemonStartedElapsed", SystemClock.elapsedRealtime())
    }
    private val am = AmUnfreezeController()
    private val directory = File("/storage/emulated/$userId/Android/data/${BuildConfig.APPLICATION_ID}/files/conf")
    private val journal = AtomicFile(File(directory, "sticky-leases.json"))
    private lateinit var schedules: DaemonScheduleStore
    private var alarms: DaemonAlarmBackend? = null
    private val armed = mutableMapOf<String, String>()
    private val offeredAt = mutableMapOf<HostProcess, Long>()
    private var lastAlarmAt = 0L
    private val bootId = File("/proc/sys/kernel/random/boot_id").readText().trim()
    private var conf = ShizukuConf(false, persistentHashMapOf())
    private val sessions = mutableMapOf<String, Session>()
    private data class Wake(val id: String, val scheduledAt: Long, val deadline: Long,
        var prepared: Boolean = false, var delivered: Boolean = false, var protectionReady: Boolean = false)
    private val pending = mutableMapOf<String, Wake>()
    private val status = mutableMapOf<String, Bundle>()
    private val handshakes = mutableMapOf<String, HostHandshakeStatus>()
    private val wakeLocks = mutableMapOf<String, PowerManager.WakeLock>()
    @Volatile
    private var initialized = false
    @Volatile
    private var stopping = false
    private val leases = StickyLeaseRegistry(
        clock = SystemClock::elapsedRealtime,
        alive = HostProcess::isAlive,
        unfreeze = { process: HostProcess, sticky ->
            val result = am.unfreeze(process.name, process.pid, sticky)
            Log.i("XALog", "task protection ${process.name}/${process.pid} sticky=$sticky success=${result.success} ${result.error}")
            result.success
        },
        journal = ::writeJournal,
        recovered = readJournal(),
        unavailable = am::isUnavailable,
    )

    init {
        worker.execute {
            synchronized(guard) {
                runCatching {
                    check(Process.myUid() in setOf(0, 2000)) { "守护进程必须以 root/shell 身份运行" }
                    leases.sweep()
                    loadConfig()
                    schedules = DaemonScheduleStore(directory)
                    initialized = true
                    reconcileAlarms()
                    Log.i("XALog", "任务守护进程已就绪 protocol=${WakeProtocol.VERSION} pid=${Process.myPid()} instance=${daemonInfo.getString("daemonInstance")}")
                }.onFailure { Log.e("XALog", "守护进程初始化失败", it) }
            }
        }
        worker.scheduleWithFixedDelay({
            synchronized(guard) {
                runCatching { watch() }.onFailure { Log.e("XALog", "任务保护清理失败，将重试", it) }
                if (initialized) {
                    runCatching { reconcileAlarms() }.onFailure { Log.e("XALog", "登记特权预唤醒失败，将重试", it) }
                    runCatching { offerRunningHosts() }.onFailure { Log.w("XALog", "宿主 Binder 交接失败，将重试", it) }
                }
            }
        }, 5, 5, TimeUnit.SECONDS)
    }

    private fun admin() {
        check(Binder.getCallingUid() == moduleUid) { "仅模块应用可管理守护进程" }
    }
    private fun enabled(pkg: String) = !stopping && conf.enableKeepAlive && conf.alivePackages[pkg] == true
    private fun validId(id: String) { require(id.matches(Regex("[a-zA-Z0-9_-]{1,80}"))) }
    private fun loadConfig() {
        conf = runCatching {
            AtomicFile(File(directory, "conf.json")).openRead().bufferedReader().use { it.readText().parse<ShizukuConf>() }
        }.getOrElse { Log.w("XALog", "读取守护配置失败", it); ShizukuConf(false, persistentHashMapOf()) }
    }

    override fun getProtocolVersion(): Int { admin(); return WakeProtocol.VERSION }
    override fun getDaemonInfo(): Bundle { admin(); return Bundle(daemonInfo) }
    override fun isRunning(): Boolean {
        admin()
        return synchronized(guard) {
            initialized && !stopping && (WakeProtocol.hosts.none(::enabled) || alarms?.alive == true)
        }
    }
    override fun reloadConfig() {
        admin()
        synchronized(guard) {
            loadConfig()
            sessions.values.toList().filter { !enabled(it.pkg) }.forEach { it.close("宿主任务守护已关闭") }
            pending.keys.toList().filter { !enabled(it) }.forEach { finishWake(it, "宿主任务守护已关闭") }
            offeredAt.clear()
            reconcileAlarms()
        }
    }

    override fun openHostSession(pkg: String, uid: Int, pid: Int, callback: IHostCallback): IHostSession {
        admin()
        require(pkg in WakeProtocol.hosts && uid / 100000 == userId)
        check(providerWake.packageInfo(pkg).applicationInfo?.uid == uid) { "宿主 UID 不匹配" }
        val identity = checkNotNull(HostProcess.read(pid)) { "宿主进程不可见" }
        check(identity.name == pkg && identity.uid == uid) { "仅允许宿主主进程连接" }
        synchronized(guard) {
            return createSession(pkg, identity, callback)
        }
    }

    private fun createSession(pkg: String, identity: HostProcess, callback: IHostCallback): Session {
        check(initialized && !stopping) { "守护进程尚未就绪" }
        sessions.remove(pkg)?.close("宿主连接被替换")
        return Session(pkg, identity, callback).also {
            sessions[pkg] = it
            handshakes.getOrPut(pkg) { HostHandshakeStatus() }.connected(it.id)
        }
    }

    private fun offerRunningHosts() {
        offeredAt.keys.removeAll { !it.isAlive() }
        WakeProtocol.hosts.filter { enabled(it) && sessions[it]?.closed != false }.forEach { pkg ->
            val info = runCatching { providerWake.packageInfo(pkg) }.getOrNull() ?: return@forEach
            val uid = info.applicationInfo?.uid ?: return@forEach
            HostProcess.find(pkg, uid).firstOrNull { it.name == pkg }?.let(::offerSession)
        }
    }

    private fun offerSession(identity: HostProcess) {
        val now = SystemClock.elapsedRealtime()
        if (now - (offeredAt[identity] ?: -60_000) < 10_000) return
        offeredAt[identity] = now
        val offerId = UUID.randomUUID().toString()
        val trace = handshakes.getOrPut(identity.name) { HostHandshakeStatus() }
        trace.offer(identity, offerId)
        val offer = object : IHostBootstrap.Stub() {
            override fun connect(version: Int, callback: IHostCallback, hostInfo: Bundle): IHostSession {
                return synchronized(guard) {
                    if (!trace.isCurrent(offerId)) {
                        trace.rejected(offerId, "已有更新的交接请求")
                        error("交接请求已被更新，请等待新的广播")
                    }
                    trace.received(offerId, version, hostInfo)
                    try {
                        check(Binder.getCallingUid() == identity.uid) { "宿主 UID 与交接目标不一致" }
                        check(Binder.getCallingPid() == identity.pid) { "宿主 PID 与交接目标不一致" }
                        check(identity.isAlive()) { "宿主进程身份已变化或无法读取" }
                        check(version == WakeProtocol.VERSION) { "宿主与守护协议不一致，请重启宿主" }
                        check(SystemClock.elapsedRealtime() - now < 30_000) { "Binder 交接已过期" }
                        check(enabled(identity.name)) { "宿主任务守护已关闭" }
                        createSession(identity.name, identity, callback)
                    } catch (e: Exception) {
                        trace.failure(e.message ?: e.javaClass.simpleName)
                        throw e
                    }
                }
            }
        }
        try {
            val result = providerWake.offer(Intent(WakeProtocol.ACTION_SESSION).setPackage(identity.name)
                .addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY or Intent.FLAG_RECEIVER_FOREGROUND)
                .putExtra("pid", identity.pid).putExtra("version", WakeProtocol.VERSION)
                .putExtra("daemonInstance", daemonInfo.getString("daemonInstance"))
                .putExtra("offerId", offerId)
                .putExtras(Bundle().apply { putBinder("bootstrap", offer) }))
            trace.broadcast(result)
            Log.i("XALog", "宿主交接广播 ${identity.name} pid=${identity.pid} result=$result offer=$offerId")
        } catch (e: Exception) {
            trace.failure(e.cause?.message ?: e.message ?: e.javaClass.simpleName)
            Log.w("XALog", "发送宿主交接失败，将重试", e)
        }
    }

    private inner class Session(val pkg: String, val identity: HostProcess, val callback: IHostCallback) : IHostSession.Stub() {
        val id = UUID.randomUUID().toString()
        var ready = false
        @Volatile
        var closed = false
        var run: String? = null
        val death = IBinder.DeathRecipient {
            worker.execute { synchronized(guard) { close("宿主连接断开") } }
        }
        init { callback.asBinder().linkToDeath(death, 0) }
        fun authenticate() {
            check(Binder.getCallingUid() == identity.uid && Binder.getCallingPid() == identity.pid && !closed && identity.isAlive()) {
                "宿主会话已失效"
            }
        }
        override fun ready() {
            authenticate()
            synchronized(guard) {
                if (ready) return
                ready = true
                handshakes[pkg]?.ready(id)
                Log.i("XALog", "宿主握手就绪 $pkg pid=${identity.pid} daemon=${Process.myPid()} session=$id")
                deliver(pkg)
            }
        }
        override fun getSessionInfo(): Bundle {
            authenticate()
            return synchronized(guard) {
                check(sessions[pkg] === this && !closed) { "宿主会话已被替换" }
                Bundle(daemonInfo).apply {
                    putString("sessionId", id)
                    putInt("hostPid", identity.pid)
                    putInt("hostUid", identity.uid)
                    putBoolean("hostReady", ready)
                }
            }
        }
        override fun disconnect(reason: String) {
            check(Binder.getCallingUid() == identity.uid && Binder.getCallingPid() == identity.pid)
            synchronized(guard) { close(reason.take(512)) }
        }
        override fun beginRun(runId: String): Bundle {
            authenticate(); validId(runId)
            return synchronized(guard) {
                if (!enabled(pkg)) return@synchronized Bundle().apply { putBoolean("success", true); putBoolean("disabled", true) }
                if (run != null) return@synchronized Bundle().apply {
                    putBoolean("success", true)
                    putBoolean("disabled", true)
                    putString("protectionError", "上一批任务保护尚未结束")
                }
                runCatching {
                    check(enabled(pkg) && ready && run == null) { "宿主尚未就绪或已有任务执行中" }
                    val protected = protect(pkg) {
                        leases.acquire(runId, HostProcess.find(pkg, identity.uid).also { check(identity in it) },
                            WakeProtocol.MAX_RUN_MS, WakeProtocol.LEASE_TTL_MS)
                    }
                    if (!protected) {
                        consumeWake(pkg)
                        val reason = if (am.isUnavailable) "当前系统不支持任务防冻结" else "任务防冻结未能开启"
                        setStatus(pkg, runId, "unprotected", "$reason，本次任务按普通模式执行")
                        return@synchronized Bundle().apply {
                            putBoolean("success", true)
                            putBoolean("disabled", true)
                            putBoolean("unfreezeUnsupported", am.isUnavailable)
                            putString("protectionError", reason)
                        }
                    }
                    acquireCpu(runId, WakeProtocol.MAX_RUN_MS)
                    run = runId
                    consumeWake(pkg)
                    setStatus(pkg, runId, "running", "任务执行中")
                    Bundle().apply { putBoolean("success", true) }
                }.getOrElse {
                    run = null
                    releaseProtection(runId)
                    consumeWake(pkg)
                    Log.w("XALog", "申请任务保护失败，签到继续按普通模式执行", it)
                    setStatus(pkg, runId, "unprotected", "任务保护不可用，本次任务按普通模式执行")
                    Bundle().apply {
                        putBoolean("success", true)
                        putBoolean("disabled", true)
                        putString("protectionError", it.message ?: "无法申请任务保护")
                    }
                }
            }
        }
        override fun heartbeat(runId: String): Boolean {
            authenticate()
            return synchronized(guard) {
                if (run != runId || !enabled(pkg) || !leases.renew(runId, WakeProtocol.LEASE_TTL_MS)) false
                else runCatching {
                    leases.attach(runId, HostProcess.find(pkg, identity.uid))
                    true
                }.getOrDefault(false)
            }
        }
        override fun finishRun(runId: String) {
            authenticate()
            synchronized(guard) {
                if (run == runId) {
                    releaseProtection(runId)
                    run = null
                    setStatus(pkg, runId, "finished", "任务已结束，已请求解除防冻结")
                } else if (pending[pkg]?.id == runId) finishWake(pkg, "宿主跳过本次任务")
            }
        }
        override fun publishSchedule(nextAt: Long) {
            authenticate()
            synchronized(guard) { saveSchedule(pkg, nextAt, false) }
        }
        fun close(reason: String) {
            if (closed) return
            closed = true
            handshakes[pkg]?.closed(id, reason)
            runCatching { callback.onSessionClosed(reason) }
            val previousRun = run
            try {
                previousRun?.let { leases.release(it) }
            } finally {
                previousRun?.let(::releaseCpu)
                run = null
                callback.asBinder().unlinkToDeath(death, 0)
                if (sessions[pkg] === this) sessions.remove(pkg)
            }
        }
    }

    override fun wakeHost(pkg: String, runId: String, scheduledAt: Long) {
        admin(); require(pkg in WakeProtocol.hosts); validId(runId)
        prepareHost(pkg, runId, scheduledAt)
    }

    private fun prepareHost(pkg: String, runId: String, scheduledAt: Long) {
        require(scheduledAt >= 0)
        val duration = if (scheduledAt == 0L) WakeProtocol.BOOTSTRAP_MS else
            (scheduledAt - System.currentTimeMillis() + WakeProtocol.CRON_GRACE_MS)
                .coerceIn(WakeProtocol.CRON_GRACE_MS, WakeProtocol.MAX_PREPARE_MS)
        synchronized(guard) {
            check(initialized && enabled(pkg)) { "未启用宿主任务守护" }
            if (pending.containsKey(pkg) || sessions[pkg]?.run != null) return
            pending[pkg] = Wake(runId, scheduledAt, SystemClock.elapsedRealtime() + duration)
            setStatus(pkg, runId, "waking", "正在通过 Provider 唤醒并等待宿主就绪")
            try { acquireCpu("wake:$runId", duration) }
            catch (e: Exception) {
                finishWake(pkg, e.message ?: "无法申请唤醒保护")
                throw e
            }
        }
        worker.execute {
            runCatching {
                val uid = checkNotNull(providerWake.packageInfo(pkg).applicationInfo).uid
                val existing = HostProcess.find(pkg, uid)
                var protectionReady = false
                synchronized(guard) {
                    if (pending[pkg]?.id != runId) return@execute
                    if (existing.isNotEmpty()) {
                        protectionReady = protect(pkg) { leases.acquire("wake:$runId", existing, duration, duration) }
                    }
                }
                providerWake.wake(pkg)
                synchronized(guard) {
                    if (pending[pkg]?.id != runId) return@execute
                    val processes = HostProcess.find(pkg, uid)
                    check(processes.any { it.name == pkg }) { "Provider 调用后仍未找到宿主主进程" }
                    val protected = protect(pkg) {
                        if (!protectionReady) leases.acquire("wake:$runId", processes, duration, duration)
                        else leases.attach("wake:$runId", processes)
                    }
                    if (!protected) releaseProtection("wake:$runId")
                    pending[pkg]?.protectionReady = protected
                    pending[pkg]?.prepared = true
                    if (sessions[pkg] == null) {
                        val main = processes.first { it.name == pkg }
                        offeredAt.remove(main)
                        offerSession(main)
                    }
                    deliver(pkg)
                }
            }.onFailure { synchronized(guard) { if (pending[pkg]?.id == runId) finishWake(pkg, it.cause?.message ?: it.message ?: "唤醒失败") } }
        }
    }

    private fun deliver(pkg: String) {
        val request = pending[pkg] ?: return
        val session = sessions[pkg]?.takeIf { it.ready && !it.closed } ?: return
        if (request.delivered || !request.prepared) return
        request.delivered = true
        if (request.scheduledAt > 0) {
            setStatus(pkg, request.id, "waiting", "宿主已提前唤醒，等待原有 Cron 执行")
            session.callback.onWakeReady(request.id, request.scheduledAt)
        } else finishWake(pkg, when {
            am.isUnavailable -> "宿主已唤醒，当前系统不支持任务防冻结"
            !request.protectionReady -> "宿主已唤醒，防冻结未能开启，签到仍可正常执行"
            else -> "Provider 唤醒、宿主握手和防冻结恢复测试成功"
        }, true)
    }

    /**
     * 尝试申请防冻结，失败时继续普通唤醒和签到，未确认的恢复记录仍由租约管理器保留。
     *
     * @return 是否实际申请了防冻结保护
     */
    private fun protect(pkg: String, action: () -> Unit): Boolean {
        if (!am.isUnavailable) {
            try {
                action()
                return true
            } catch (e: Exception) {
                Log.w("XALog", "防冻结未能开启，继续普通唤醒和签到 $pkg", e)
            }
        }
        return false
    }

    /**
     * 原有 Cron 开始任务后结束对应预唤醒，防止普通执行仍被记为等待 Cron 超时。
     */
    private fun consumeWake(pkg: String) {
        val request = pending.remove(pkg) ?: return
        releaseProtection("wake:${request.id}")
    }
    private fun finishWake(pkg: String, message: String, success: Boolean = false) {
        val request = pending.remove(pkg) ?: return
        releaseProtection("wake:${request.id}")
        val restored = !leases.hasPendingRestore()
        setStatus(pkg, request.id, if (success && restored) "ready" else "error",
            if (restored) message else "防冻结恢复未获确认，守护进程会继续重试")
    }
    private fun watch() {
        sessions.values.toList().filter {
            !it.identity.isAlive() || !it.callback.asBinder().isBinderAlive
        }.forEach { it.close("宿主进程或回调已退出") }
        leases.sweep().forEach { id ->
            sessions.values.firstOrNull { it.run == id }?.let { session ->
                session.run = null
                setStatus(session.pkg, id, "unprotected", "任务保护已结束，签到继续按普通模式执行")
            }
            releaseCpu(id)
        }
        pending.filterValues { SystemClock.elapsedRealtime() >= it.deadline }.keys.toList().forEach {
            val message = when {
                pending[it]?.delivered == true -> "等待 Cron 超时，已请求解除防冻结"
                sessions[it]?.closed == false -> "宿主已连接，但未在规定时间内确认账号与模块就绪"
                else -> "宿主会话建立超时，请查看连接交接记录"
            }
            finishWake(it, message)
        }
    }
    private fun setStatus(pkg: String, id: String, state: String, message: String) {
        status[pkg] = Bundle().apply {
            putString("runId", id); putString("state", state); putString("message", message)
        }
        Log.i("XALog", "$pkg $state: $message")
    }
    override fun getHostStatus(pkg: String): Bundle {
        admin(); require(pkg in WakeProtocol.hosts)
        return synchronized(guard) {
            val connected = sessions[pkg]?.takeIf { !it.closed && it.identity.isAlive() && it.callback.asBinder().isBinderAlive }
            (status[pkg]?.let(::Bundle) ?: Bundle()).apply { putBoolean("pendingRestore", leases.hasPendingRestore()) }
                .apply {
                    putAll(daemonInfo)
                    putBoolean("connected", connected != null)
                    putBoolean("hostReady", connected?.ready == true)
                    putString("sessionId", connected?.id)
                    putInt("hostPid", connected?.identity?.pid ?: -1)
                    putBundle("handshake", handshakes[pkg]?.snapshot())
                    putBoolean("unfreezeUnsupported", am.isUnavailable)
                    putInt("alarmUid", alarms?.uid ?: -1); putBoolean("alarmAlive", alarms?.alive == true)
                    putLong("lastAlarmAt", lastAlarmAt)
                    if (::schedules.isInitialized) schedules.plans[pkg]?.let {
                        putLong("target", it.target); putLong("checkAt", it.checkAt)
                        putLong("revision", it.revision); putInt("attempts", it.attempts)
                        putBoolean("armed", armed.containsKey(pkg))
                    }
                }
        }
    }

    override fun publishSchedule(pkg: String, nextAt: Long, onlyIfAbsent: Boolean) {
        admin(); require(pkg in WakeProtocol.hosts)
        synchronized(guard) { saveSchedule(pkg, nextAt, onlyIfAbsent) }
    }

    private fun saveSchedule(pkg: String, nextAt: Long, onlyIfAbsent: Boolean) {
        require(nextAt >= 0)
        check(initialized && !stopping)
        val previous = schedules.plans[pkg]
        if (onlyIfAbsent && previous != null) return
        val updated = WakePlan.replace(previous, nextAt)
        if (updated != previous) {
            schedules.save(pkg, updated)
            Log.i("XALog", "守护已保存宿主计划 $pkg target=$nextAt revision=${updated.revision}")
        }
        reconcileAlarms()
    }

    private fun reconcileAlarms() {
        if (stopping) return
        if (alarms?.alive != true) {
            alarms?.close(); alarms = null; armed.clear()
            // 即使当前没有计划任务，也保留辅助进程的管道，用于守护异常退出后的恢复。
            if (WakeProtocol.hosts.none(::enabled)) return
            alarms = DaemonAlarmBackend(context, userId) { token, release ->
                worker.execute {
                    try { synchronized(guard) { alarmFired(token) } }
                    catch (e: Exception) { Log.e("XALog", "守护预唤醒失败", e) }
                    finally { release() }
                }
            }
        }
        val backend = alarms ?: return
        WakeProtocol.hosts.forEach { pkg ->
            val previous = schedules.plans[pkg]
            val plan = previous?.catchUp(System.currentTimeMillis())
            if (plan != null && plan != previous) schedules.save(pkg, plan)
            val token = plan?.takeIf { enabled(pkg) && it.active }?.let { "$pkg:${it.revision}" }
            if (armed[pkg] == token) return@forEach
            armed.remove(pkg)?.let(backend::remove)
            if (token != null) {
                val at = plan.alarmAt(System.currentTimeMillis())
                backend.set(token, at)
                armed[pkg] = token
                Log.i("XALog", "守护已登记预唤醒 $pkg at=$at target=${plan.checkAt} alarmUid=${backend.uid}")
            }
        }
    }

    private fun alarmFired(token: String) {
        val pkg = armed.entries.firstOrNull { it.value == token }?.key ?: return
        armed.remove(pkg)
        if (!enabled(pkg)) return
        val plan = schedules.plans[pkg] ?: return
        val now = System.currentTimeMillis()
        // 系统时间变化或闹钟投递延迟时，改为准备下一次 Cron 检查。
        val current = plan.catchUp(now)
        if (current != plan || current.checkAt - now > WakeProtocol.PRE_WAKE_MS + 1_000) {
            if (current != plan) schedules.save(pkg, current)
            reconcileAlarms(); return
        }
        // 调用 Provider 前先保存重试计划，避免守护退出后丢失唯一的唤醒机会。
        schedules.save(pkg, plan.consumed())
        lastAlarmAt = now
        Log.i("XALog", "守护预唤醒触发 $pkg target=${plan.checkAt} now=$now")
        prepareHost(pkg, UUID.randomUUID().toString(), plan.checkAt)
        reconcileAlarms()
    }

    private fun acquireCpu(id: String, timeout: Long) {
        val power = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val lock = power.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "XAutoDaily:task:$id")
        lock.setReferenceCounted(false)
        // 清理任务每五秒执行一次，am 命令也有执行超时，需预留恢复 sticky 状态的时间，
        // 避免系统自动释放最后一个 CPU 唤醒锁后，清理任务因休眠而延迟。
        lock.acquire(timeout + 30_000)
        wakeLocks.put(id, lock)?.let { if (it.isHeld) it.release() }
    }
    private fun releaseCpu(id: String) { wakeLocks.remove(id)?.let { if (it.isHeld) it.release() } }

    /**
     * 释放辅助保护，恢复失败时保留租约记录供后续重试，不向签到流程传播异常。
     */
    private fun releaseProtection(id: String) {
        runCatching { leases.release(id) }.onFailure { Log.w("XALog", "恢复任务保护失败，将继续重试 run=$id", it) }
        runCatching { releaseCpu(id) }.onFailure { Log.w("XALog", "释放任务唤醒锁失败 run=$id", it) }
    }

    private fun readJournal(): Set<HostProcess> = runCatching {
        val json = JSONObject(journal.openRead().bufferedReader().use { it.readText() })
        if (json.optString("bootId") != bootId) return emptySet()
        val entries = json.getJSONArray("processes")
        (0 until entries.length()).map { entries.getJSONObject(it) }.mapNotNull {
            val p = HostProcess(it.getInt("pid"), it.getInt("uid"), it.getString("name"), it.getLong("start"))
            p.takeIf { p.uid / 100000 == userId && p.isAlive() }
        }.toSet()
    }.getOrElse { emptySet() }
    private fun writeJournal(processes: Set<HostProcess>) {
        check(directory.isDirectory || directory.mkdirs())
        val array = JSONArray()
        processes.forEach { p -> array.put(JSONObject().put("pid", p.pid).put("uid", p.uid).put("name", p.name).put("start", p.startTicks)) }
        val stream = journal.startWrite()
        try {
            stream.write(JSONObject().put("bootId", bootId).put("processes", array).toString().toByteArray())
            journal.finishWrite(stream)
        } catch (e: Exception) { journal.failWrite(stream); throw e }
    }

    override fun execShell(shell: String): String {
        admin()
        val process = Runtime.getRuntime().exec(shell)
        return process.inputStream.bufferedReader().use { it.readText() }
    }
    override fun unfreezeHostProcess(processName: String?, sticky: Boolean): Bundle {
        admin()
        require(!sticky) { "请通过任务会话申请有期限的防冻结保护" }
        val result = am.unfreeze(processName, false)
        return Bundle().apply { putBoolean("success", result.success); putString("error", result.error); putString("output", result.output) }
    }
    override fun exit() { admin(); destroy() }
    override fun destroy() {
        check(Binder.getCallingUid() in setOf(moduleUid, 0, 2000))
        synchronized(guard) {
            if (stopping) return
            stopping = true; initialized = false
            alarms?.close(); alarms = null; armed.clear()
            sessions.values.toList().forEach { session -> runCatching { session.close("守护进程停止") } }
            runCatching { leases.close() }.onFailure { Log.e("XALog", "退出时恢复防冻结失败，已保留恢复记录", it) }
            wakeLocks.keys.toList().forEach(::releaseCpu)
        }
        worker.shutdownNow()
        exitProcess(0)
    }
}
