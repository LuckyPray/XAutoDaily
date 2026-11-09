package me.teble.xposed.autodaily.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil
import me.teble.xposed.autodaily.shizuku.HostDaemonClient
import me.teble.xposed.autodaily.shizuku.WakeProtocol
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.ui.enable
import me.teble.xposed.autodaily.ui.errInfo
import me.teble.xposed.autodaily.ui.nextShouldExecTime
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 提供轻量的计划变更通知；配置设置方法也会在不含宿主类的模块应用进程中调用。
 */
object TaskScheduleEvents {
    @Volatile
    var listener: (() -> Unit)? = null
    fun changed() { listener?.invoke() }
}

object TaskSchedulePublisher {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val publication = SchedulePublication(HostDaemonClient::publish)
    private var job: Job? = null
    @Volatile
    private var installed = false
    @Volatile
    private var calculating = false
    @Volatile
    private var dirty = false
    @Volatile
    private var retryNotBefore = 0L
    private var lastPublishSucceeded = false
    fun install() { installed = true; TaskScheduleEvents.listener = ::changed }
    @Synchronized
    fun changed() {
        if (!installed || !HostDaemonClient.isConnected) return
        if (calculating || TaskExecutor.isRunning) { dirty = true; return }
        job?.cancel()
        job = scope.launch {
            delay(750)
            synchronized(this@TaskSchedulePublisher) {
                if (!isActive) return@synchronized
                if (TaskExecutor.isRunning) dirty = true else publish()
            }
        }
    }
    fun connectionChanged() {
        publication.invalidate()
    }
    @Synchronized
    fun afterRun() {
        // 最终计划快照已在释放任务保护前发送，此处仅处理后续变更或发布失败。
        if (dirty || !lastPublishSucceeded) changed()
    }
    @Synchronized
    fun publishBeforeRelease() {
        if (!installed) return
        job?.cancel()
        job = null
        retryNotBefore = System.currentTimeMillis() + WakeProtocol.RETRY_MS
        publish()
    }
    @Synchronized
    private fun publish() {
        if (!HostDaemonClient.isConnected) {
            lastPublishSucceeded = false
            return
        }
        calculating = true
        dirty = false
        lastPublishSucceeded = false
        try {
            val account = QApplicationUtil.currentUin
            if (!ConfUnit.globalEnable || account <= 10000) {
                if (publication.publish(account, 0)) LogUtil.i("已取消宿主预唤醒计划")
                lastPublishSucceeded = HostDaemonClient.isConnected
                return
            }
            val now = System.currentTimeMillis()
            val taskNow = TimeUtil.getCNDate().time
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
            val next = ConfigUtil.loadSaveConf().taskGroups.flatMap { it.tasks }.filter { it.enable }
                .mapNotNull { task ->
                    runCatching {
                        val due = ConfigUtil.checkExecuteTask(task)
                        // 按任务引擎的时间计算执行间隔，再转换为 RTC 闹钟使用的设备时间。
                        if (task.errInfo.count >= 3) now + 24 * 60 * 60_000L
                        else if (due) maxOf(now + 1_000, retryNotBefore)
                        else task.nextShouldExecTime?.let { now + (format.parse(it)!!.time - taskNow).coerceAtLeast(1_000) }
                    }.onFailure { LogUtil.e(it, "计算任务 ${task.id} 下次唤醒失败") }.getOrNull()
                }.minOrNull() ?: 0
            val cronCheck = if (next == 0L) 0 else WakeProtocol.nextCronCheck(next, now)
            if (publication.publish(account, cronCheck)) {
                LogUtil.i("发布宿主下次 Cron 检查时间: $cronCheck")
            }
            lastPublishSucceeded = HostDaemonClient.isConnected
        } catch (e: Exception) { LogUtil.e(e, "发布任务时间失败") }
        finally {
            calculating = false
            if (dirty) { dirty = false; changed() }
        }
    }
}
