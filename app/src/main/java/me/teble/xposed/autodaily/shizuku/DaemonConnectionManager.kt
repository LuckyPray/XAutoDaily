package me.teble.xposed.autodaily.shizuku

import android.content.ComponentName
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.IUserService
import me.teble.xposed.autodaily.application.xaApp
import me.teble.xposed.autodaily.config.PACKAGE_NAME_QQ
import me.teble.xposed.autodaily.config.PACKAGE_NAME_TIM
import me.teble.xposed.autodaily.ui.ConfUnit
import rikka.shizuku.Shizuku
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

/**
 * 管理模块应用与守护进程的连接，连接和恢复过程不依赖 Activity 或 ViewModel 存活。
 */
object DaemonConnectionManager {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()
    private val applicationReady = CompletableDeferred<Unit>()
    @Volatile
    private var service: IUserService? = null
    private var recovery: Job? = null
    var running by mutableStateOf(false); private set
    var starting by mutableStateOf(false); private set
    var error by mutableStateOf(""); private set
    private val wakeTestResults = mutableStateMapOf<String, WakeTestResult>()
    private var wakeTestPackage by mutableStateOf<String?>(null)
    val wakeTestResult: WakeTestResult?
        get() = wakeTestPackage?.let { wakeTestResults[it] }
    val wakeTestVisible: Boolean
        get() = wakeTestPackage != null
    private val wakeTestJobs = mutableMapOf<String, Job>()
    private val preferences get() = xaApp.getSharedPreferences("task_daemon", 0)
    val paused get() = preferences.getBoolean("paused", false)
    private val installedBuild by lazy { DaemonBuild.read(xaApp) }
    private val args by lazy {
        Shizuku.UserServiceArgs(ComponentName(BuildConfig.APPLICATION_ID, UserService::class.java.name))
            .daemon(true).processNameSuffix("service").debuggable(BuildConfig.DEBUG)
            .version(DaemonBuild.serviceVersion(installedBuild))
    }
    private var connection: ServiceConnection? = null

    /**
     * 每次绑定使用独立回调，旧绑定迟到的连接或断线通知不得覆盖当前守护。
     */
    private fun newConnection(awaiting: CompletableDeferred<IUserService>) = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            if (connection !== this) return
            if (binder == null || !binder.pingBinder()) {
                awaiting.completeExceptionally(IllegalStateException("守护进程 Binder 无效")); return
            }
            val remote = IUserService.Stub.asInterface(binder)
            runCatching {
                binder.linkToDeath({ if (service?.asBinder() == binder) disconnected() }, 0)
                awaiting.complete(remote)
            }.onFailure { awaiting.completeExceptionally(it) }
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            if (connection === this) {
                awaiting.completeExceptionally(IllegalStateException("守护连接已断开"))
                disconnected()
            }
        }
    }

    fun initialize() {
        applicationReady.complete(Unit)
        HostAlarmMigration.cancelLegacy()
        if (ConfUnit.keepAlive && !paused) scope.launch { runCatching { ensureConnected() } }
    }
    suspend fun awaitApplicationReady() = withTimeout(5_000) { applicationReady.await() }
    fun disconnected() {
        service = null; running = false
        ShizukuApi.clearUserServiceBinder()
        recover()
    }
    @Synchronized
    fun recover() {
        if (recovery?.isActive == true) return
        recovery = scope.launch {
            awaitApplicationReady()
            repeat(4) { attempt ->
                delay((attempt + 1) * 1_000L)
                if (!ConfUnit.keepAlive || paused || !Shizuku.pingBinder() ||
                    Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) return@launch
                if (runCatching { ensureConnected() }.isSuccess) return@launch
            }
        }
    }
    suspend fun ensureConnected(): IUserService = mutex.withLock {
        awaitApplicationReady()
        check(ConfUnit.keepAlive && !paused) { "任务守护未启用或已手动停止" }
        check(Shizuku.pingBinder() && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            "Shizuku 未运行或未授权"
        }
        service?.takeIf { it.asBinder().isBinderAlive && running }?.let { return@withLock it }
        starting = true; error = ""
        try {
            ShizukuConfigStore.saveCurrent()
            var verified: IUserService? = null
            for (attempt in 0..1) {
                val deferred = CompletableDeferred<IUserService>()
                val listener = newConnection(deferred)
                connection = listener
                withContext(Dispatchers.Main) { Shizuku.bindUserService(args, listener) }
                val candidate = withTimeout(10_000) { deferred.await() }
                val matches = runCatching {
                    candidate.protocolVersion == WakeProtocol.VERSION && DaemonBuild.matches(candidate.daemonInfo, installedBuild)
                }.getOrDefault(false)
                if (matches) {
                    verified = candidate
                    break
                }
                withContext(Dispatchers.Main) { Shizuku.unbindUserService(args, listener, true) }
                check(attempt == 0) { "守护未加载当前安装版本，请重新启动任务守护" }
                Log.w("XALog", "守护安装版本不一致，已请求重建")
            }
            val remote = checkNotNull(verified)
            withTimeout(10_000) {
                val candidate = remote
                while (!candidate.isRunning) delay(100)
            }
            check(!paused && ConfUnit.keepAlive)
            service = remote
            ShizukuApi.initBinder(remote.asBinder())
            running = true
            val info = remote.daemonInfo
            Log.i("XALog", "模块已连接守护 pid=${info.getInt("daemonPid")} instance=${info.getString("daemonInstance")}")
            HostAlarmMigration.migrate(remote)
            remote
        } catch (e: Exception) {
            error = e.message ?: e.javaClass.simpleName
            disconnected()
            runCatching { withContext(Dispatchers.Main) { Shizuku.unbindUserService(args, connection, false) } }
            Log.w("XALog", "连接守护进程失败: $error")
            throw e
        } finally { starting = false }
    }
    suspend fun start() {
        preferences.edit().putBoolean("paused", false).commit()
        ensureConnected()
        service?.reloadConfig()
    }
    suspend fun stop() {
        preferences.edit().putBoolean("paused", true).commit()
        HostAlarmMigration.cancelLegacy()
        ShizukuConfigStore.saveCurrent()
        mutex.withLock {
            withContext(Dispatchers.Main) { Shizuku.unbindUserService(args, connection, true) }
            disconnected()
        }
    }
    fun configChanged() {
        scope.launch {
            runCatching {
                awaitApplicationReady()
                ShizukuConfigStore.saveCurrent()
                service?.reloadConfig()
                HostAlarmMigration.cancelLegacy()
                if (ConfUnit.keepAlive && !paused) ensureConnected()
            }.onFailure { Log.w("XALog", "更新任务守护配置失败", it) }
        }
    }
    /**
     * 按宿主分别保存和运行唤醒检查，重复点击同一宿主时复用正在执行的检查。
     */
    @Synchronized
    fun testWake(packageName: String) {
        val hostName = when (packageName) {
            PACKAGE_NAME_QQ -> "QQ"
            PACKAGE_NAME_TIM -> "TIM"
            else -> return
        }
        wakeTestPackage = packageName
        if (wakeTestJobs[packageName]?.isActive == true) return
        wakeTestResults[packageName] = WakeTestResult.from(packageName)
        wakeTestJobs[packageName] = scope.launch {
            var remote: IUserService? = null
            var state: Bundle? = null
            val id = UUID.randomUUID().toString()
            suspend fun show(running: Boolean, message: String? = null, failure: String = "") {
                val result = WakeTestResult.from(packageName, state, running,
                    remote?.asBinder()?.isBinderAlive == true, message, failure, id)
                withContext(Dispatchers.Main) { wakeTestResults[packageName] = result }
            }
            try {
                val prerequisite = when {
                    !Shizuku.pingBinder() -> "请先启动 Shizuku"
                    Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED -> "请先授权 Shizuku"
                    !ConfUnit.keepAlive -> "请先开启任务守护"
                    paused -> "请先在首页运行任务守护"
                    !(if (packageName == PACKAGE_NAME_QQ) ConfUnit.qKeepAlive else ConfUnit.timKeepAlive) ->
                        "请先开启 $hostName 任务守护"
                    else -> null
                }
                if (prerequisite != null) {
                    show(false, prerequisite)
                    return@launch
                }
                val connected = ensureConnected()
                remote = connected
                show(true)
                connected.wakeHost(packageName, id, 0)
                withTimeout((WakeProtocol.BOOTSTRAP_MS + 5_000).milliseconds) {
                    while (true) {
                        state = connected.getHostStatus(packageName)
                        if (state?.getString("runId") != id) {
                            show(false, "$hostName 正在唤醒或执行任务，请稍后再试")
                            return@withTimeout
                        }
                        val finished = state?.getString("state") in setOf("ready", "partial", "error")
                        show(!finished)
                        if (finished) return@withTimeout
                        delay(300)
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException && e !is TimeoutCancellationException) throw e
                Log.w("XALog", "$packageName 唤醒检查失败", e)
                state = runCatching { remote?.getHostStatus(packageName) }.getOrNull() ?: state
                show(false, if (e is TimeoutCancellationException && state != null) null
                    else "唤醒检查未完成，请复制诊断信息反馈", "${e.javaClass.simpleName}: ${e.message.orEmpty()}")
            }
        }
    }

    /**
     * 收起结果窗口；后台检查仍会完成并按原有规则释放防冻结保护。
     */
    fun dismissWakeTest() {
        wakeTestPackage = null
    }
}
