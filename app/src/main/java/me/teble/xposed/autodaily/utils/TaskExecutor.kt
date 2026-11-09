package me.teble.xposed.autodaily.utils

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import me.teble.xposed.autodaily.IHostSession
import me.teble.xposed.autodaily.hook.base.moduleLoadInit
import me.teble.xposed.autodaily.hook.notification.XANotification
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.shizuku.HostDaemonClient
import me.teble.xposed.autodaily.shizuku.WakeProtocol
import me.teble.xposed.autodaily.task.cron.CronUtil
import me.teble.xposed.autodaily.task.filter.GroupTaskFilterChain
import me.teble.xposed.autodaily.task.model.TaskGroup
import me.teble.xposed.autodaily.task.model.TaskProperties
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.task.util.formatDate
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.ui.TaskErrorInfo
import me.teble.xposed.autodaily.ui.errInfo
import me.teble.xposed.autodaily.ui.retryCount
import java.time.LocalDateTime
import java.util.TimeZone
import java.util.UUID
import java.util.concurrent.CompletableFuture.runAsync
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

object TaskExecutor {

    const val CORE_SERVICE_FLAG = "XAutoDaily:core_service_flag"
    const val CORE_SERVICE_TOAST_FLAG = "XAutoDaily:core_service_toast_flag"

    private val lock = ReentrantLock()
    private val scope = CoroutineScope(Dispatchers.Default)
    const val EXEC_TASK = 1
    const val AUTO_EXEC = 2
    const val START_CRON = 3
    private val handlerThread by lazy {
        HandlerThread("CoreServiceHookThread").apply { start() }
    }
    private val runtimeTasks = mutableSetOf<TaskGroup>()
    private val running = AtomicBoolean(false)
    val isRunning get() = running.get()
    @Volatile
    private var activeControl: TaskRunControl? = null
    @Volatile
    private var activePool: ExecutorService? = null

    val handler = object : Handler(handlerThread.looper) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                EXEC_TASK -> runTasks(true)
                AUTO_EXEC -> runTasks(false)
                START_CRON -> startCorn()
            }
        }
    }

    fun cancelRun(runId: String, reason: String) {
        if (activeControl?.id == runId) cancelActiveRun(reason)
    }

    fun cancelActiveRun(reason: String) {
        activeControl?.cancel(reason)
        activePool?.shutdownNow()
    }

    fun runTasks(userExec: Boolean) {
        scope.launch(Dispatchers.IO) {
            if (!running.compareAndSet(false, true)) {
                if (userExec) ToastUtil.send("已有任务正在执行")
                return@launch
            }
            val runId = UUID.randomUUID().toString()
            val control = TaskRunControl(runId, WakeProtocol.MAX_RUN_MS)
            activeControl = control
            var session: IHostSession? = null
            var heartbeat: Job? = null
            try {
                withTimeout(WakeProtocol.BOOTSTRAP_MS) { while (!moduleLoadInit) delay(200) }
                if (!ConfUnit.globalEnable) {
                    if (userExec) ToastUtil.send("未启用总开关，跳过执行")
                    return@launch
                }
                executorTask(ConfigUtil.loadSaveConf(), control) {
                    session = HostDaemonClient.begin(runId)
                    val connected = session
                    if (connected != null) heartbeat = scope.launch(Dispatchers.IO) {
                        while (isActive) {
                            delay(WakeProtocol.HEARTBEAT_MS)
                            if (!runCatching { connected.heartbeat(runId) }.getOrDefault(false)) {
                                cancelRun(runId, "任务防冻结保护续期失败")
                                break
                            }
                        }
                    }
                    if (userExec) ToastUtil.send("开始执行签到") else LogUtil.i("定时执行 run=$runId")
                }
            } catch (e: Exception) {
                control.cancel(e.message ?: "任务失败")
                LogUtil.e(e, "本次任务已停止")
                if (userExec) ToastUtil.send("任务停止: ${e.message}", true)
            } finally {
                heartbeat?.cancel()
                if (session != null) HostDaemonClient.finish(runId, session)
                val remaining = activePool
                activeControl = null
                if (remaining != null && !remaining.isTerminated) {
                    // 等待无法中断的旧工作线程真正退出后，才允许下一批任务开始。
                    scope.launch(Dispatchers.IO) {
                        while (!remaining.awaitTermination(1, TimeUnit.SECONDS)) delay(100)
                        activePool = null; running.set(false)
                    }
                } else {
                    activePool = null; running.set(false)
                }
            }
        }
    }

    private suspend fun executorTask(conf: TaskProperties, control: TaskRunControl, protect: suspend () -> Unit) {
        val needExecGroups = mutableListOf<TaskGroup>()
        val currDateStr = TimeUtil.getCNDate().formatDate()
        lock.withLock {
            for (group in conf.taskGroups) {
                for (task in group.tasks) {
                    val taskException = task.errInfo
                    // 重置历史异常数据
                    if (taskException.dateStr.isNotEmpty() && taskException.dateStr != currDateStr) {
                        if (task.retryCount != 0) {
                            LogUtil.d("重置任务${task.id}历史异常数据")
                            task.retryCount = 0
                            task.errInfo = TaskErrorInfo()
                        }
                    }
                    if (ConfigUtil.checkExecuteTask(task) && task.errInfo.count < 3) {
                        if (!runtimeTasks.contains(group)) {
                            needExecGroups.add(group)
                        }
                        break
                    }
                }
            }
        }
        if (needExecGroups.isEmpty()) {
            return
        }
        protect()
        val threadPool = Executors.newFixedThreadPool(if (ConfUnit.usedThreadPool) 5 else 1)
        activePool = threadPool
        try {
            control.check()
            XANotification.start()
            lock.withLock { runtimeTasks.addAll(needExecGroups) }
            for (taskGroup in needExecGroups) {
                threadPool.execute {
                    try {
                        control.within {
                            GroupTaskFilterChain.build(taskGroup)
                                .doFilter(mutableMapOf(), mutableListOf(), mutableMapOf())
                        }
                    } catch (e: Exception) {
                        LogUtil.e(e)
                    }
                }
            }
            threadPool.shutdown()
            while (!threadPool.awaitTermination(1, TimeUnit.SECONDS)) control.check()
            control.check()
        } finally {
            control.cancel(control.cancelled ?: "本批任务已结束")
            threadPool.shutdownNow()
            threadPool.awaitTermination(5, TimeUnit.SECONDS)
            lock.withLock { runtimeTasks.removeAll(needExecGroups.toSet()) }
            XANotification.setContent("任务执行已结束", onGoing = false, isTask = true)
            XANotification.stop()
        }
    }

    fun startCorn() {
        val scheduler = CronUtil.scheduler
        if (scheduler.isStarted) {
            return
        }
        runAsync {
            TimeUtil.init()
            CronUtil.setMatchSecond(true)
            scheduler.setTimeZone(TimeZone.getTimeZone("GMT+8"))
            try {
                CronUtil.start()
            } catch (e: Throwable) {
                LogUtil.e(e, "CronUtil.start error: ")
                ToastUtil.send("任务调度器启动失败，详情请查看日志")
            }
            LogUtil.i("CoreService 进程已经启动，启动时间 -> ${LocalDateTime.now()}")
            LogUtil.i("任务调度器启动成功")
            CronUtil.schedule(
                "task_timer",
                "0 */10 * * * *"
            ) {
                handler.sendEmptyMessage(AUTO_EXEC)
            }
            CronUtil.schedule(
                "check_update_task",
                "0 0 9/3 * * *"
            ) {
                ConfigUtil.checkUpdate(false)
            }
            LogUtil.d("任务调度器存在任务：${scheduler.taskTable.ids}")
        }
    }
}
