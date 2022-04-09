package me.teble.xposed.autodaily.task.cron.listener

import me.teble.xposed.autodaily.task.cron.TaskExecutor


/**
 * 定时任务监听接口<br>
 * 通过实现此接口，实现对定时任务的各个环节做监听
 * @author Looly
 *
 */
interface TaskListener {
    /**
     * 定时任务启动时触发
     * @param executor [TaskExecutor]
     */
    fun onStart(executor: TaskExecutor)

    /**
     * 任务成功结束时触发
     *
     * @param executor [TaskExecutor]
     */
    fun onSucceeded(executor: TaskExecutor)

    /**
     * 任务启动失败时触发
     *
     * @param executor [TaskExecutor]
     * @param exception 异常
     */
    fun onFailed(executor: TaskExecutor, exception: Throwable)
}