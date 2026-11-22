package me.teble.xposed.autodaily.shizuku

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Process
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.teble.xposed.autodaily.IHostCallback
import me.teble.xposed.autodaily.IHostBootstrap
import me.teble.xposed.autodaily.IHostSession
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.hook.base.hostApp
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.TaskSchedulePublisher
import java.util.concurrent.atomic.AtomicInteger

/**
 * 运行于已注入模块的宿主主进程，负责连接守护，不依赖模块应用进程中的全局对象。
 */
object HostDaemonClient {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()
    private val protectionMutex = Mutex()
    private val generations = AtomicInteger()
    @Volatile
    private var session: IHostSession? = null
    private var sessionInfo: Bundle? = null
    private var incompatibleSession: Pair<String, Int>? = null
    @Volatile
    private var started = false
    @Volatile
    private var runtimeReady = false
    @Volatile
    private var generation = 0
    private var connectionJob: Job? = null

    val isConnected: Boolean
        get() = currentSession() != null

    private fun currentSession(): IHostSession? = session?.takeIf { it.asBinder().isBinderAlive }

    fun start() {
        if (started) return
        started = true
        hostApp.registerActivityLifecycleCallbacks(DaemonVersionNotice)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                acceptOffer(intent)
            }
        }
        // 通过 DUMP 权限限制会话交接广播的发送方，阻止未获特权授权的普通应用冒充守护。
        val filter = IntentFilter(WakeProtocol.ACTION_SESSION)
        runCatching {
            if (Build.VERSION.SDK_INT >= 33) hostApp.registerReceiver(receiver, filter, "android.permission.DUMP", null, Context.RECEIVER_EXPORTED)
            else hostApp.registerReceiver(receiver, filter, "android.permission.DUMP", null)
        }.onFailure { LogUtil.w("注册任务守护接收器失败，普通签到仍可使用: ${it.message}") }
        TaskSchedulePublisher.install()
        reconnect()
    }

    /**
     * 响应守护实际发出的交接请求，不以旧 Binder 仍存活为由忽略重连。
     * 新连接验证成功后才替换本地会话，迟到的旧回调不能清除新连接。
     */
    private fun acceptOffer(intent: Intent): Job = scope.launch {
        try {
            check(intent.getIntExtra("pid", -1) == Process.myPid()) { "交接目标 PID 不匹配" }
            mutex.withLock {
                val protocol = intent.getIntExtra("version", -1)
                if (protocol != WakeProtocol.VERSION) {
                    if (!isConnected) DaemonVersionNotice.protocolMismatch(protocol)
                    return@launch
                }
                val instance = checkNotNull(intent.getStringExtra("daemonInstance"))
                incompatibleSession?.let { (rejectedInstance, actualProtocol) ->
                    if (instance == rejectedInstance) {
                        if (!isConnected) DaemonVersionNotice.protocolMismatch(actualProtocol)
                        return@launch
                    }
                }
                val offer = checkNotNull(IHostBootstrap.Stub.asInterface(intent.extras?.getBinder("bootstrap")))
                LogUtil.i("收到守护交接 pid=${Process.myPid()} instance=$instance offer=${intent.getStringExtra("offerId")}")
                val previous = session
                val epoch = generations.incrementAndGet()
                val hostInfo = Bundle().apply {
                    putInt("hostVersionCode", BuildConfig.VERSION_CODE)
                    putString("hostVersionName", BuildConfig.VERSION_NAME)
                }
                val candidate = offer.connect(WakeProtocol.VERSION, callback(epoch), hostInfo)
                try {
                    if (!adopt(candidate, epoch, instance)) {
                        if (candidate.asBinder() != previous?.asBinder()) disconnect(candidate, "守护协议不匹配")
                        return@launch
                    }
                } catch (e: Exception) {
                    if (candidate.asBinder() != previous?.asBinder()) disconnect(candidate, "宿主未接受会话: ${e.message}")
                    throw e
                }
                if (previous != null && previous.asBinder() != candidate.asBinder()) {
                    disconnect(previous, "宿主已切换到新的守护会话")
                }
            }
            TaskSchedulePublisher.changed()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            LogUtil.w("守护 Binder 交接失败: ${e.message}")
        }
    }
    fun markReady() {
        runtimeReady = true
        if (started) reconnect()
    }
    @Synchronized
    fun reconnect() {
        if (!started || !isConnected || connectionJob?.isActive == true) return
        connectionJob = scope.launch {
            repeat(3) { attempt ->
                try {
                    connect() ?: return@launch
                    TaskSchedulePublisher.changed()
                    return@launch
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    LogUtil.w("宿主连接任务守护失败: ${e.message}")
                    delay((attempt + 1) * 3_000L)
                }
            }
        }
    }
    /**
     * 使用守护主动交接的会话；尚未连接时按普通模式执行，不依赖模块 Provider。
     *
     * @return 当前可用的守护会话，未连接时返回 null
     */
    private suspend fun connect(): IHostSession? = mutex.withLock {
        val connected = currentSession() ?: return@withLock null
        try {
            connected.sessionInfo
            if (runtimeReady && QApplicationUtil.currentUin > 10000) connected.ready()
            connected
        } catch (e: Exception) {
            if (session === connected) session = null
            disconnect(connected, "宿主检测到会话已失效")
            throw e
        }
    }

    private fun callback(epoch: Int) = object : IHostCallback.Stub() {
        private fun trusted() = Binder.getCallingUid() in setOf(0, 2000) && epoch == generation
        override fun onWakeReady(wakeId: String, scheduledAt: Long) {
            if (trusted()) LogUtil.i("宿主提前唤醒就绪，等待原有 Cron 检查 at=$scheduledAt wake=$wakeId")
        }
        override fun cancelRun(runId: String, reason: String) {
            if (trusted()) LogUtil.w("任务保护已失效，签到继续按普通模式执行 run=$runId: $reason")
        }
        override fun onSessionClosed(reason: String) {
            val allowed = Binder.getCallingUid() in setOf(0, 2000)
            if (allowed) scope.launch {
                mutex.withLock {
                    if (epoch == generation) {
                        session = null
                        LogUtil.w("任务守护已断开，普通签到不受影响: $reason")
                    }
                }
                reconnect()
            }
        }
    }
    private fun adopt(connected: IHostSession, epoch: Int, instance: String): Boolean {
        val info = connected.sessionInfo
        check(info.getString("daemonInstance") == instance) { "交接来源与实际守护实例不一致" }
        check(info.getInt("hostPid") == Process.myPid() && info.getInt("hostUid") == Process.myUid()) { "会话宿主身份不匹配" }
        val protocol = info.getInt("daemonProtocol")
        if (protocol != WakeProtocol.VERSION) {
            incompatibleSession = instance to protocol
            if (!isConnected) DaemonVersionNotice.protocolMismatch(protocol)
            return false
        }
        val previousInfo = sessionInfo
        check(currentSession() == null || previousInfo?.getString("daemonInstance") == instance ||
            (previousInfo?.getLong("daemonStartedElapsed") ?: 0) <= info.getLong("daemonStartedElapsed")) {
            "当前已连接更新的守护实例，忽略迟到的旧交接"
        }
        if (runtimeReady && QApplicationUtil.currentUin > 10000) connected.ready()
        val confirmed = connected.sessionInfo
        connected.asBinder().linkToDeath({
            scope.launch {
                mutex.withLock {
                    if (epoch == generation && session?.asBinder() == connected.asBinder()) {
                        session = null
                        LogUtil.w("守护进程连接中断，普通签到不受影响")
                    }
                }
                reconnect()
            }
        }, 0)
        generation = epoch
        session = connected
        sessionInfo = confirmed
        incompatibleSession = null
        DaemonVersionNotice.connected(confirmed)
        TaskSchedulePublisher.connectionChanged()
        LogUtil.i("任务守护连接成功 pid=${Process.myPid()} ready=${confirmed.getBoolean("hostReady")} " +
            "daemonPid=${confirmed.getInt("daemonPid")} instance=$instance session=${confirmed.getString("sessionId")}")
        return true
    }

    private fun disconnect(connected: IHostSession, reason: String) {
        scope.launch {
            runCatching { connected.disconnect(reason) }
                .onFailure { LogUtil.w("关闭旧守护会话失败，将由守护超时清理: ${it.message}") }
        }
    }
    /**
     * 独立申请和续期任务保护，失败或守护响应缓慢时不阻塞签到线程。
     * 任务结束后取消返回的作业；即使申请结果迟到，也会释放对应租约。
     *
     * @param runId 本批签到的唯一标识
     * @return 负责申请、续期及释放保护的后台作业
     */
    fun protect(runId: String): Job = scope.launch {
        if (!protectionMutex.tryLock()) {
            LogUtil.w("上一次任务保护尚未清理完成，本次签到按普通模式执行")
            return@launch
        }
        var connected: IHostSession? = null
        try {
            val available = connect() ?: return@launch
            coroutineContext.ensureActive()
            check(runtimeReady && QApplicationUtil.currentUin > 10000) { "宿主账号尚未就绪" }
            connected = available
            val result = available.beginRun(runId)
            coroutineContext.ensureActive()
            check(result.getBoolean("success")) { result.getString("error") ?: "无法申请任务防冻结保护" }
            if (result.getBoolean("disabled")) {
                result.getString("protectionError")?.let { LogUtil.w("未启用任务保护，签到继续按普通模式执行: $it") }
                return@launch
            }
            while (isActive) {
                delay(WakeProtocol.HEARTBEAT_MS)
                if (!available.heartbeat(runId)) {
                    LogUtil.w("任务保护续期失败，签到继续按普通模式执行 run=$runId")
                    break
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            LogUtil.w("任务保护不可用，签到继续按普通模式执行 run=$runId: ${e.message}")
        } finally {
            try {
                connected?.let { finish(runId, it) }
            } finally {
                protectionMutex.unlock()
            }
        }
    }
    private fun finish(runId: String, connected: IHostSession) {
        runCatching { connected.finishRun(runId) }.onFailure { LogUtil.w("释放任务保护失败，将由守护进程超时清理: ${it.message}") }
    }
    fun publish(nextAt: Long): Boolean {
        val connected = currentSession() ?: return false
        connected.publishSchedule(nextAt)
        return true
    }
}
