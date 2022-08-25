package me.teble.xposed.autodaily.utils

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.os.PowerManager
import cn.hutool.core.thread.ThreadUtil
import kotlinx.coroutines.*
import me.teble.xposed.autodaily.hook.base.moduleLoadInit
import me.teble.xposed.autodaily.hook.notification.XANotification
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.task.cron.CronUtil
import me.teble.xposed.autodaily.task.filter.GroupTaskFilterChain
import me.teble.xposed.autodaily.task.model.TaskGroup
import me.teble.xposed.autodaily.task.model.TaskProperties
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.ui.errInfo
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

object TaskExecutor {

    const val CORE_SERVICE_FLAG = "XAutoDaily:core_service_flag"
    const val CORE_SERVICE_TOAST_FLAG = "XAutoDaily:core_service_toast_flag"

    private val lock = ReentrantLock()
    lateinit var wakeLock: PowerManager.WakeLock
    private val wakeLockInit get() = TaskExecutor::wakeLock.isInitialized
    private val scope = CoroutineScope(Dispatchers.Default)
    const val EXEC_TASK = 1
    const val AUTO_EXEC = 2
    const val START_CRON = 3
    private val handlerThread by lazy {
        HandlerThread("CoreServiceHookThread").apply { start() }
    }
    private val runtimeTasks = mutableSetOf<TaskGroup>()

    val handler = object : Handler(handlerThread.looper) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                EXEC_TASK -> runTasks(true)
                AUTO_EXEC -> runTasks(false)
                START_CRON -> startCorn()
            }
        }
    }

    fun runTasks(userExec: Boolean) {
        scope.launch(Dispatchers.IO) {
            while (!moduleLoadInit) {
                LogUtil.d("等待初始化完毕")
                delay(500)
            }
            val globalEnable = ConfUnit.globalEnable
            if (!globalEnable) {
                if (userExec) {
                    ToastUtil.send("未启用总开关，跳过执行")
                }
                return@launch
            }
            if (userExec) {
                ToastUtil.send("开始执行签到")
            } else {
                LogUtil.i("定时执行")
            }
            runCatching {
                executorTask(ConfigUtil.loadSaveConf())
            }.onFailure {
                LogUtil.e(it)
            }
        }
    }

    private suspend fun executorTask(conf: TaskProperties) {
        val executeTime = System.currentTimeMillis()
        val needExecGroups = mutableListOf<TaskGroup>()
        lock.withLock {
            for (group in conf.taskGroups) {
                for (task in group.tasks) {
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
        try {
            XANotification.start()
            if (wakeLockInit) {
                // 未知原因，CoreService没有正常hook上
                wakeLock.acquire(20 * 60 * 1000L)
            } else {
                // 补偿启动定时器
                handler.sendEmptyMessage(START_CRON)
            }
            runtimeTasks.addAll(needExecGroups)
            var threadCount = 1
            if (ConfUnit.usedThreadPool) {
                threadCount = 5
            }
            val threadPool = ThreadUtil.newExecutor(threadCount, threadCount)
            for (taskGroup in needExecGroups) {
                threadPool.execute(
                    thread(false) {
                        try {
                            GroupTaskFilterChain.build(taskGroup)
                                .doFilter(mutableMapOf(), mutableListOf(), mutableMapOf())
                        } catch (e: Exception) {
                            LogUtil.e(e)
                        }
                    }
                )
            }
            threadPool.shutdown()
            while (!withContext(Dispatchers.IO) {
                    threadPool.awaitTermination(1, TimeUnit.SECONDS)
                }) {
                if (System.currentTimeMillis() - executeTime > 20 * 60 * 1000) {
                    // 关闭运行时间超过20分钟的任务
                    threadPool.shutdownNow()
                }
            }
        } finally {
            needExecGroups.forEach {
                if (runtimeTasks.contains(it)) {
                    runtimeTasks.remove(it)
                }
            }
            XANotification.setContent("签到执行完毕", onGoing = false, isTask = true)
            withContext(Dispatchers.IO) {
                delay(5000)
            }
            XANotification.stop()
            if (wakeLockInit && wakeLock.isHeld) {
                wakeLock.release()
            }
        }
    }

    fun startCorn() {
        val scheduler = CronUtil.scheduler
        if (scheduler.isStarted) {
            return
        }
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