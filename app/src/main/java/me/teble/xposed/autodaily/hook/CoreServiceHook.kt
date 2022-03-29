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
import me.teble.xposed.autodaily.hook.config.Config.accountConfig
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.task.cron.CronUtil
import me.teble.xposed.autodaily.task.filter.GroupTaskFilterChain
import me.teble.xposed.autodaily.task.model.TaskGroup
import me.teble.xposed.autodaily.task.model.TaskProperties
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.task.util.Const.GLOBAL_ENABLE
import me.teble.xposed.autodaily.ui.Cache
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
                    return@thread
                }
                lock.withLock {
                    while (!Global.isInit()) {
                        LogUtil.d("等待初始化完毕")
                        Thread.sleep(500)
                    }
                    val globalEnable = accountConfig.getBoolean(GLOBAL_ENABLE, false)
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
        }

        private fun executorTask(conf: TaskProperties) {
            val needExecGroups = mutableListOf<TaskGroup>()
            for (group in conf.taskGroups) {
                for (task in group.tasks) {
                    if (ConfigUtil.checkExecuteTask(task)) {
                        needExecGroups.add(group)
                        break
                    }
                }
            }
            if (needExecGroups.isEmpty()) {
                return
            }
            // TODO 是否启用多线程
            val threadPool = ThreadUtil.newExecutor(5, 5)
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
            threadPool.awaitTermination(10, TimeUnit.SECONDS)
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
                        "0 */1 * * * *"
                    ) {
                        LogUtil.d("执行定时task")
                        handler.sendEmptyMessage(AUTO_EXEC)
                    }
                    CronUtil.schedule(
                        "check_update_task",
                        "0 0 9/3 * * *"
                    ) {
                        if (ConfigUtil.checkUpdate(false)) {
                            Cache.needUpdate = true
                        }
                    }
                    CronUtil.schedule(
                        "time_correction",
                        "0 0 0/3 * * *"
                    ) {
                        TimeUtil.init()
                    }
                    LogUtil.d("任务调度器存在任务：${scheduler.taskTable.ids}")
                }
            }
        )
    }
}