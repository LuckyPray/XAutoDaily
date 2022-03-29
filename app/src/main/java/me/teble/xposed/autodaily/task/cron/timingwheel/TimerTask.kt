package me.teble.xposed.autodaily.task.cron.timingwheel


/**
 * 延迟任务
 *
 * @author eliasyaoyc, looly
 */
open class TimerTask(task: Runnable?, delayMs: Long) {

    /**
     * 延迟时间
     */
    val delayMs: Long = System.currentTimeMillis() + delayMs

    /**
     * 任务
     */
    val task: Runnable? = task

    /**
     * 时间槽
     */
    var timerTaskList: TimerTaskList? = null

    /**
     * 下一个节点
     */
    var next: TimerTask? = null

    /**
     * 上一个节点
     */
    var prev: TimerTask? = null

    /**
     * 任务描述
     */
    var desc: String? = null

    override fun toString(): String {
        return desc!!
    }

}
