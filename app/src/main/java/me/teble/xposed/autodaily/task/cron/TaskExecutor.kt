package me.teble.xposed.autodaily.task.cron

import me.teble.xposed.autodaily.task.cron.task.CronTask
import me.teble.xposed.autodaily.task.cron.task.Task


/**
 * 作业执行器<br></br>
 * 执行具体的作业，执行完毕销毁<br></br>
 * 作业执行器唯一关联一个作业，负责管理作业的运行的生命周期。
 *
 * @author Looly
 */
class TaskExecutor(private val scheduler: Scheduler, task: CronTask) :
    Runnable {
    private val task: CronTask = task

    /**
     * 获得原始任务对象
     *
     * @return 任务对象
     */
    fun getTask(): Task {
        return task.raw
    }

    /**
     * 获得原始任务对象
     *
     * @return 任务对象
     * @since 5.4.7
     */
    val cronTask: CronTask
        get() = task

    override fun run() {
        try {
            scheduler.listenerManager.notifyTaskStart(this)
            task.execute()
            scheduler.listenerManager.notifyTaskSucceeded(this)
        } catch (e: Exception) {
            scheduler.listenerManager.notifyTaskFailed(this, e)
        } finally {
            scheduler.taskExecutorManager!!.notifyExecutorCompleted(this)
        }
    }

}
