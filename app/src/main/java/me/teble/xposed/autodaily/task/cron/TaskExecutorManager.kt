package me.teble.xposed.autodaily.task.cron

import me.teble.xposed.autodaily.task.cron.task.CronTask
import java.io.Serializable
import java.util.*


/**
 * 作业执行管理器<br></br>
 * 负责管理作业的启动、停止等
 *
 *
 *
 * 此类用于管理正在运行的作业情况，作业启动后加入任务列表，任务结束移除
 *
 *
 * @author Looly
 * @since 3.0.1
 */
class TaskExecutorManager(protected var scheduler: Scheduler) :
    Serializable {
    /**
     * 执行器列表
     */
    private val executors: MutableList<TaskExecutor> = ArrayList()

    /**
     * 获取所有正在执行的任务调度执行器
     *
     * @return 任务执行器列表
     * @since 4.6.7
     */
    fun getExecutors(): List<TaskExecutor> {
        return Collections.unmodifiableList(executors)
    }

    /**
     * 启动 执行器TaskExecutor，即启动作业
     *
     * @param task [CronTask]
     * @return [TaskExecutor]
     */
    fun spawnExecutor(task: CronTask): TaskExecutor {
        val executor = TaskExecutor(scheduler, task)
        synchronized(executors) { executors.add(executor) }
        // 子线程是否为deamon线程取决于父线程，因此此处无需显示调用
        // executor.setDaemon(this.scheduler.daemon);
//		executor.start();
        scheduler.threadExecutor!!.execute(executor)
        return executor
    }

    /**
     * 执行器执行完毕调用此方法，将执行器从执行器列表移除，此方法由[TaskExecutor]对象调用，用于通知管理器自身已完成执行
     *
     * @param executor 执行器 [TaskExecutor]
     * @return this
     */
    fun notifyExecutorCompleted(executor: TaskExecutor): TaskExecutorManager {
        synchronized(executors) { executors.remove(executor) }
        return this
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}