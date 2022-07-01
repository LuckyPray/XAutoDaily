package me.teble.xposed.autodaily.hook

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.os.PowerManager
import android.os.PowerManager.PARTIAL_WAKE_LOCK
import cn.hutool.core.thread.ThreadUtil
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import kotlinx.coroutines.*
import me.teble.xposed.autodaily.config.CoreService
import me.teble.xposed.autodaily.hook.annotation.MethodHook
import me.teble.xposed.autodaily.hook.base.BaseHook
import me.teble.xposed.autodaily.hook.base.load
import me.teble.xposed.autodaily.hook.base.moduleLoadSuccess
import me.teble.xposed.autodaily.hook.notification.XANotification
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.task.cron.CronUtil
import me.teble.xposed.autodaily.task.filter.GroupTaskFilterChain
import me.teble.xposed.autodaily.task.model.TaskGroup
import me.teble.xposed.autodaily.task.model.TaskProperties
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.TimeUtil
import me.teble.xposed.autodaily.utils.toMap
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

class CoreServiceHook : BaseHook() {

    override val isCompatible = true

    override val enabled = true

    companion object {

        const val CORE_SERVICE_FLAG = "XAutoDaily:core_service_flag"
        const val CORE_SERVICE_TOAST_FLAG = "XAutoDaily:core_service_toast_flag"

        private val lock = ReentrantLock()
        lateinit var wakeLock: PowerManager.WakeLock
        val wakeLockInit get() = Companion::wakeLock.isInitialized
        val scope = CoroutineScope(Dispatchers.Default)
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
                while (!moduleLoadSuccess) {
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
                executorTask(ConfigUtil.loadSaveConf())
            }
        }

        private suspend fun executorTask(conf: TaskProperties) {
            val executeTime = System.currentTimeMillis()
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
            try {
                XANotification.start()
                wakeLock.acquire(20 * 60 * 1000L)
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
                while (!threadPool.awaitTermination(1, TimeUnit.SECONDS)) {
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
                wakeLock.release()
            }
        }

        private fun startCorn() {
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
                if (ConfigUtil.checkUpdate(false)) {
                    ConfUnit.needUpdate = true
                }
            }
            LogUtil.d("任务调度器存在任务：${scheduler.taskTable.ids}")
        }
    }

    @MethodHook("代理 service hook")
    fun coreServiceHook() {
        val cCoreService by lazy { load(CoreService)!! }
        findMethod(cCoreService) {
            name == "onCreate"
        }.hookAfter {
            LogUtil.d("CoreService onCreate")
            if (wakeLockInit) {
                return@hookAfter
            }
            val service = it.thisObject as Service
            val pm = service.getSystemService(Service.POWER_SERVICE) as PowerManager
            wakeLock = pm.newWakeLock(PARTIAL_WAKE_LOCK, service::class.java.name)
        }
        XposedBridge.hookAllMethods(Service::class.java, "onStartCommand",
        object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                if (param.thisObject::class.java != cCoreService) {
                    return
                }
                val args = param.args
                val service = param.thisObject as Service
                val intent = args[0] as Intent?
                if (intent?.hasExtra(CORE_SERVICE_FLAG) != true) {
                    return
                }
                if (intent.hasExtra(CORE_SERVICE_TOAST_FLAG)) {
                    LogUtil.d("onStartCommand")
                    LogUtil.d(intent.extras.toMap().toString())
                    ToastUtil.send(service, "唤醒测试: true")
                } else {
                    handler.sendEmptyMessage(START_CRON)
                }
                return
            }
        })
    }
}