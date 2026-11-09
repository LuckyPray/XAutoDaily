package me.teble.xposed.autodaily.shizuku

import android.content.ComponentName
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
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

/**
 * 管理模块应用与守护进程的连接，连接和恢复过程不依赖 Activity 或 ViewModel 存活。
 */
object DaemonConnectionManager {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()
    private val applicationReady = CompletableDeferred<Unit>()
    private var awaiting: CompletableDeferred<IUserService>? = null
    @Volatile
    private var service: IUserService? = null
    private var recovery: Job? = null
    var running by mutableStateOf(false); private set
    var starting by mutableStateOf(false); private set
    var error by mutableStateOf(""); private set
    private val preferences get() = xaApp.getSharedPreferences("task_daemon", 0)
    val paused get() = preferences.getBoolean("paused", false)
    private val args by lazy {
        Shizuku.UserServiceArgs(ComponentName(BuildConfig.APPLICATION_ID, UserService::class.java.name))
            .daemon(true).processNameSuffix("service").debuggable(BuildConfig.DEBUG)
            .version(BuildConfig.VERSION_CODE * 10 + WakeProtocol.VERSION)
    }
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            if (binder == null || !binder.pingBinder()) {
                awaiting?.completeExceptionally(IllegalStateException("守护进程 Binder 无效")); return
            }
            val remote = IUserService.Stub.asInterface(binder)
            runCatching {
                binder.linkToDeath({ if (service?.asBinder() == binder) disconnected() }, 0)
                awaiting?.complete(remote)
            }.onFailure { awaiting?.completeExceptionally(it) }
        }
        override fun onServiceDisconnected(name: ComponentName?) = disconnected()
    }

    fun initialize() {
        applicationReady.complete(Unit)
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
            val deferred = CompletableDeferred<IUserService>()
            awaiting = deferred
            withContext(Dispatchers.Main) { Shizuku.bindUserService(args, connection) }
            val remote = withTimeout(10_000) {
                val candidate = deferred.await()
                check(candidate.protocolVersion == WakeProtocol.VERSION) { "守护进程协议版本不一致，请重新运行" }
                while (!candidate.isRunning) delay(100)
                candidate
            }
            check(!paused && ConfUnit.keepAlive)
            service = remote
            ShizukuApi.initBinder(remote.asBinder())
            running = true
            remote
        } catch (e: Exception) {
            error = e.message ?: e.javaClass.simpleName
            disconnected()
            runCatching { withContext(Dispatchers.Main) { Shizuku.unbindUserService(args, connection, false) } }
            Log.w("XALog", "连接守护进程失败: $error")
            throw e
        } finally { awaiting = null; starting = false }
    }
    suspend fun start() {
        preferences.edit().putBoolean("paused", false).commit()
        ensureConnected()
        service?.reloadConfig()
    }
    suspend fun stop() {
        preferences.edit().putBoolean("paused", true).commit()
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
                if (ConfUnit.keepAlive && !paused) ensureConnected()
            }.onFailure { Log.w("XALog", "更新任务守护配置失败", it) }
        }
    }
    fun testWake(packageName: String) {
        val hostName = when (packageName) {
            PACKAGE_NAME_QQ -> "QQ"
            PACKAGE_NAME_TIM -> "TIM"
            else -> return
        }
        scope.launch {
            val message = runCatching {
                if (!Shizuku.pingBinder()) return@runCatching "请先启动 Shizuku"
                if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                    return@runCatching "请先授权 Shizuku"
                }
                if (!ConfUnit.keepAlive) return@runCatching "请先开启任务守护"
                if (paused) return@runCatching "请先在首页运行任务守护"
                val enabled = if (packageName == PACKAGE_NAME_QQ) ConfUnit.qKeepAlive else ConfUnit.timKeepAlive
                if (!enabled) return@runCatching "请先开启 $hostName 任务守护"
                val remote = ensureConnected()
                val id = UUID.randomUUID().toString()
                remote.wakeHost(packageName, id, 0)
                withTimeout(WakeProtocol.BOOTSTRAP_MS + 5_000) {
                    var result: String? = null
                    while (result == null) {
                        val state = remote.getHostStatus(packageName)
                        if (state.getString("runId") != id) {
                            result = "$hostName 正在唤醒或执行任务，请稍后再试"
                        } else when (state.getString("state")) {
                            "ready" -> result = "$hostName 已唤醒"
                            "error" -> {
                                val detail = state.getString("message").orEmpty()
                                Log.w("XALog", "$packageName 唤醒未完成: $detail")
                                result = if (detail.contains("超时")) "唤醒超时，请打开 $hostName 后重试"
                                else "$hostName 唤醒未完成，请稍后再试"
                            }
                        }
                        if (result == null) delay(300)
                    }
                    result
                }
            }.getOrElse {
                if (it is CancellationException && it !is TimeoutCancellationException) throw it
                Log.w("XALog", "$packageName 唤醒失败", it)
                if (it is TimeoutCancellationException) "唤醒超时，请打开 $hostName 后重试"
                else "暂时无法唤醒 $hostName，请稍后重试"
            }
            withContext(Dispatchers.Main) { Toast.makeText(xaApp, message, Toast.LENGTH_LONG).show() }
        }
    }
}
