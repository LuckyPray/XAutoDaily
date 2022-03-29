package me.teble.xposed.autodaily.task.cron

import cn.hutool.core.exceptions.UtilException
import me.teble.xposed.autodaily.task.cron.pattent.CronPattern
import me.teble.xposed.autodaily.task.cron.task.Task


/**
 * 定时任务工具类<br></br>
 * 此工具持有一个全局[Scheduler]，所有定时任务在同一个调度器中执行<br></br>
 * [.setMatchSecond] 方法用于定义是否使用秒匹配模式，如果为true，则定时任务表达式中的第一位为秒，否则为分，默认是分
 *
 * @author xiaoleilu
 */
object CronUtil {
    /**
     * @return 获得Scheduler对象
     */
    val scheduler = Scheduler()

    /**
     * 设置是否支持秒匹配<br></br>
     * 此方法用于定义是否使用秒匹配模式，如果为true，则定时任务表达式中的第一位为秒，否则为分，默认是分<br></br>
     *
     * @param isMatchSecond `true`支持，`false`不支持
     */
    fun setMatchSecond(isMatchSecond: Boolean) {
        scheduler.matchSecond = isMatchSecond
    }

    /**
     * 加入定时任务
     *
     * @param schedulingPattern 定时任务执行时间的crontab表达式
     * @param task 任务
     * @return 定时任务ID
     */
    fun schedule(schedulingPattern: String, task: Task): String {
        return scheduler.schedule(schedulingPattern, task)
    }

    /**
     * 加入定时任务
     *
     * @param id 定时任务ID
     * @param schedulingPattern 定时任务执行时间的crontab表达式
     * @param task 任务
     * @return 定时任务ID
     * @since 3.3.0
     */
    fun schedule(id: String, schedulingPattern: String, task: Task): String {
        scheduler.schedule(id, schedulingPattern, task)
        return id
    }

    /**
     * 加入定时任务
     *
     * @param schedulingPattern 定时任务执行时间的crontab表达式
     * @param task 任务
     * @return 定时任务ID
     */
    fun schedule(schedulingPattern: String, task: Runnable): String {
        return scheduler.schedule(schedulingPattern, task)
    }

    /**
     * 移除任务
     *
     * @param schedulerId 任务ID
     * @return 是否移除成功，`false`表示未找到对应ID的任务
     */
    fun remove(schedulerId: String): Boolean {
        return scheduler.descheduleWithStatus(schedulerId)
    }

    /**
     * 更新Task的执行时间规则
     *
     * @param id Task的ID
     * @param pattern [CronPattern]
     * @since 4.0.10
     */
    fun updatePattern(id: String, pattern: CronPattern) {
        scheduler.updatePattern(id, pattern)
    }

    /**
     * 开始，非守护线程模式
     *
     * @see .start
     */
    fun start() {
        start(false)
    }

    /**
     * 开始
     *
     * @param isDaemon 是否以守护线程方式启动，如果为true，则在调用[.stop]方法后执行的定时任务立即结束，否则等待执行完毕才结束。
     */
    @Synchronized
    fun start(isDaemon: Boolean) {
        if (scheduler.isStarted) {
            throw UtilException("Scheduler has been started, please stop it first!")
        }
        scheduler.start(isDaemon)
    }

    /**
     * 重新启动定时任务<br></br>
     * 此方法会清除动态加载的任务，重新启动后，守护线程与否与之前保持一致
     */
    fun restart() {
        //重新启动
        scheduler.start()
    }

    /**
     * 停止
     */
    fun stop() {
        scheduler.stop(true)
    }
}
