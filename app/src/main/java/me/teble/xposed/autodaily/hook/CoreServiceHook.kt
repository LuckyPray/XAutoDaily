package me.teble.xposed.autodaily.hook

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import cn.hutool.core.exceptions.UtilException
import cn.hutool.core.thread.ThreadUtil
import cn.hutool.cron.CronUtil
import cn.hutool.cron.task.Task
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import me.teble.xposed.autodaily.config.QQClasses.Companion.CoreService
import me.teble.xposed.autodaily.hook.annotation.MethodHook
import me.teble.xposed.autodaily.hook.base.BaseHook
import me.teble.xposed.autodaily.hook.base.Global
import me.teble.xposed.autodaily.hook.config.Config.accountConfig
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.task.filter.GroupTaskFilterChain
import me.teble.xposed.autodaily.task.model.TaskProperties
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.task.util.Const.CHANGE_SIGN_BUTTON
import me.teble.xposed.autodaily.task.util.Const.GLOBAL_ENABLE
import me.teble.xposed.autodaily.task.util.format
import me.teble.xposed.autodaily.task.util.millisecond
import me.teble.xposed.autodaily.ui.Cache
import me.teble.xposed.autodaily.utils.LogUtil
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

class CoreServiceHook : BaseHook() {

    override val isCompatible = true

    override val enabled = true

    companion object {
        const val TAG = "CoreService"
        private val lock = ReentrantLock()
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
                if (lock.isLocked) return@thread
                lock.withLock {
                    while (!Global.isInit()) {
                        LogUtil.d("CoreServiceHook", "等待初始化完毕")
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
                    }
                    val changeSignButton = accountConfig.getBoolean(CHANGE_SIGN_BUTTON, false)
                    val mostRecentExecTime = ConfigUtil.getMostRecentExecTime()
                    LogUtil.d(
                        TAG,
                        "isChange=$changeSignButton, isOnce=$once, mostRecentExecTime=${
                            Date(mostRecentExecTime).format()
                        }"
                    )
                    if (changeSignButton || once || LocalDateTime.now().millisecond > mostRecentExecTime) {
                        executorTask(ConfigUtil.loadSaveConf())
                    }
                    if (changeSignButton) {
                        accountConfig.putBoolean(CHANGE_SIGN_BUTTON, false)
                    }
                }
            }
        }

        private fun executorTask(conf: TaskProperties) {
            val threadPool = ThreadUtil.newExecutor(5, 5)
            for (taskGroup in conf.taskGroups) {
                threadPool.execute(
                    thread(false) {
                        try {
                            GroupTaskFilterChain.build(taskGroup)
                                .doFilter(mutableMapOf(), mutableListOf(), mutableMapOf())
                        } catch (e: Exception) {
                            LogUtil.e(taskGroup.id, e)
                        }
                    }
                )
            }
        }
    }

    @MethodHook("代理 service hook")
    fun coreServiceHook() {

        XposedHelpers.findAndHookMethod(load(CoreService),
            "onCreate", object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    try {
                        LogUtil.d(TAG, "调用 ---------------> ${param.method.name}")
                        CronUtil.setMatchSecond(true)
                        CronUtil.start()
                        logd("CoreService 进程已经启动，启动时间 -> ${LocalDateTime.now()}")
                        CronUtil.schedule("0 */10 * * * *", Task {
                            handler.sendEmptyMessage(AUTO_EXEC)
                        })
                        CronUtil.schedule("0 0 9/3 * * *", Task {
                            if (ConfigUtil.checkUpdate(false)) {
                                Cache.needUpdate = true
                            }
                        })
                    } catch (ignore: UtilException) {
                        // Scheduler has been started, please stop it first!
                    }
                }
            })
    }
}