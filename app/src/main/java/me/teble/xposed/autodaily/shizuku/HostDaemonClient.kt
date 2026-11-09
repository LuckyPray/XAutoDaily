package me.teble.xposed.autodaily.shizuku

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Build
import android.os.Process
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.teble.xposed.autodaily.IHostCallback
import me.teble.xposed.autodaily.IHostBootstrap
import me.teble.xposed.autodaily.IHostSession
import me.teble.xposed.autodaily.hook.base.hostApp
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.TaskExecutor
import me.teble.xposed.autodaily.utils.TaskSchedulePublisher

/**
 * 运行于已注入模块的宿主主进程，负责连接守护，不依赖模块应用进程中的全局对象。
 */
object HostDaemonClient {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()
    @Volatile
    private var session: IHostSession? = null
    @Volatile
    private var started = false
    @Volatile
    private var runtimeReady = false
    @Volatile
    private var generation = 0
    @Volatile
    private var protectedRunId: String? = null
    private var connectionJob: Job? = null

    val isConnected: Boolean
        get() = currentSession() != null

    private fun currentSession(): IHostSession? = session?.takeIf { it.asBinder().isBinderAlive }

    fun start() {
        if (started) return
        started = true
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.getIntExtra("pid", -1) != Process.myPid() ||
                    intent.getIntExtra("version", -1) != WakeProtocol.VERSION) return
                val offer = IHostBootstrap.Stub.asInterface(intent.extras?.getBinder("bootstrap")) ?: return
                scope.launch {
                    runCatching {
                        mutex.withLock {
                            if (session?.asBinder()?.isBinderAlive == true) return@withLock
                            val epoch = ++generation
                            adopt(offer.connect(WakeProtocol.VERSION, callback(epoch)), epoch)
                        }
                        TaskSchedulePublisher.changed()
                    }.onFailure { LogUtil.w("守护 Binder 交接失败: ${it.message}") }
                }
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
        if (runtimeReady && QApplicationUtil.currentUin > 10000) connected.ready()
        connected
    }

    private fun callback(epoch: Int) = object : IHostCallback.Stub() {
        private fun trusted() = Binder.getCallingUid() in setOf(0, 2000) && epoch == generation
        override fun onWakeReady(wakeId: String, scheduledAt: Long) {
            if (trusted()) LogUtil.i("宿主提前唤醒就绪，等待原有 Cron 检查 at=$scheduledAt wake=$wakeId")
        }
        override fun cancelRun(runId: String, reason: String) {
            if (trusted()) TaskExecutor.cancelRun(runId, reason)
        }
        override fun onSessionClosed(reason: String) {
            if (trusted()) {
                session = null
                cancelProtectedRun(reason)
                reconnect()
            }
        }
    }
    private fun adopt(connected: IHostSession, epoch: Int): IHostSession {
        connected.asBinder().linkToDeath({
            if (epoch == generation) {
                session = null
                cancelProtectedRun("守护进程连接中断")
                reconnect()
            }
        }, 0)
        session = connected
        TaskSchedulePublisher.connectionChanged()
        if (runtimeReady && QApplicationUtil.currentUin > 10000) connected.ready()
        LogUtil.i("任务守护连接成功 pid=${Process.myPid()} ready=$runtimeReady")
        return connected
    }
    suspend fun begin(runId: String): IHostSession? {
        val connected = connect() ?: return null
        check(runtimeReady && QApplicationUtil.currentUin > 10000) { "宿主账号尚未就绪" }
        // 先记录申请中的任务，确保 Binder 在申请过程中断开时也能取消对应任务。
        protectedRunId = runId
        try {
            val result = connected.beginRun(runId)
            check(result.getBoolean("success")) { result.getString("error") ?: "无法申请任务防冻结保护" }
            if (result.getBoolean("disabled")) {
                protectedRunId = null
                return null
            }
            return connected
        } catch (e: Exception) {
            finish(runId, connected)
            throw e
        }
    }
    private fun cancelProtectedRun(reason: String) {
        protectedRunId?.let { TaskExecutor.cancelRun(it, reason) }
    }
    fun finish(runId: String, connected: IHostSession? = session) {
        runCatching { connected?.finishRun(runId) }.onFailure { LogUtil.w("释放任务保护失败，将由守护进程超时清理: ${it.message}") }
        if (protectedRunId == runId) protectedRunId = null
    }
    fun publish(nextAt: Long): Boolean {
        val connected = currentSession() ?: return false
        connected.publishSchedule(nextAt)
        return true
    }
}
