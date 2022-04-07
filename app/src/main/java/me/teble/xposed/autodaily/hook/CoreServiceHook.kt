package me.teble.xposed.autodaily.hook

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import cn.hutool.core.thread.ThreadUtil
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import me.teble.xposed.autodaily.config.QQClasses.Companion.CoreService
import me.teble.xposed.autodaily.hook.annotation.MethodHook
import me.teble.xposed.autodaily.hook.base.BaseHook
import me.teble.xposed.autodaily.hook.base.Global
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.task.cron.CronUtil
import me.teble.xposed.autodaily.task.filter.GroupTaskFilterChain
import me.teble.xposed.autodaily.task.model.TaskGroup
import me.teble.xposed.autodaily.task.model.TaskProperties
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.TimeUtil
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

class CoreServiceHook : BaseHook() {

    override val isCompatible = true

    override val enabled = true

    companion object {
        const val TAG = "CoreService"
        private val lock = ReentrantLock()
        private val cronLock = ReentrantLock()
        const val EXEC_TASK = 1
        const val AUTO_EXEC = 2
        private val handlerThread by lazy {
            HandlerThread("CoreServiceHookThread").apply { start() }
        }
        private val runtimeTasks = mutableSetOf<TaskGroup>()

        val handler = object : Handler(handlerThread.looper) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    EXEC_TASK -> runTasks(true)
                    AUTO_EXEC -> runTasks(false)
                }
            }
        }

        fun runTasks(once: Boolean) {
            thread {
                if (lock.isLocked) {
                    if (once) {
                        ToastUtil.send("当前存在任务正在执行，请稍后再试")
                    }
                    return@thread
                }
                while (!Global.isInit()) {
                    LogUtil.d("等待初始化完毕")
                    Thread.sleep(500)
                }
                val globalEnable = ConfUnit.globalEnable
                if (!globalEnable) {
                    if (once) {
                        ToastUtil.send("未启用模块，跳过执行")
                    }
                    return@thread
                }
                if (once) {
                    ToastUtil.send("开始执行签到")
                } else {
                    LogUtil.d("定时执行")
                }
                executorTask(ConfigUtil.loadSaveConf())
            }
        }

        private fun executorTask(conf: TaskProperties) {
            val executeTime = TimeUtil.getCurrentTime()
            val needExecGroups = mutableListOf<TaskGroup>()
            lock.withLock {
                for (group in conf.taskGroups) {
                    for (task in group.tasks) {
                        if (ConfigUtil.checkExecuteTask(task)) {
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
            while (!threadPool.awaitTermination(1, TimeUnit.SECONDS)) {
                if (TimeUtil.getCurrentTime() - executeTime > 20 * 60 * 1000) {
                    // 关闭运行时间超过20分钟的任务
                    threadPool.shutdownNow()
                    needExecGroups.forEach {
                        if (runtimeTasks.contains(it)) {
                            runtimeTasks.remove(it)
                        }
                    }
                }
            }
        }
    }

    @MethodHook("代理 service hook")
    fun coreServiceHook() {
        XposedHelpers.findAndHookMethod(load(CoreService),
            "onCreate", object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val scheduler = CronUtil.scheduler
                    if (scheduler.isStarted) {
                        return
                    }
                    CronUtil.setMatchSecond(true)
                    try {
                        CronUtil.start()
                    } catch (e: Throwable) {
                        LogUtil.e(e, "CronUtil.start error: ")
                        ToastUtil.send("任务调度器启动失败，详情请查看日志")
                    }
                    logd("CoreService 进程已经启动，启动时间 -> ${LocalDateTime.now()}")
                    LogUtil.d("任务调度器启动成功")
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
                        if (ConfigUtil.checkUpdate(false)) {
                            ConfUnit.needUpdate = true
                        }
                    }
                    LogUtil.d("任务调度器存在任务：${scheduler.taskTable.ids}")
                }
            }
        )
    }
}