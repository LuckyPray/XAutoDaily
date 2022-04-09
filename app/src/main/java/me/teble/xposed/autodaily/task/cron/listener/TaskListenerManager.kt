package me.teble.xposed.autodaily.task.cron.listener

import me.teble.xposed.autodaily.task.cron.TaskExecutor
import me.teble.xposed.autodaily.utils.LogUtil
import java.io.Serializable


/**
 * 监听调度器，统一管理监听
 * @author Looly
 */
class TaskListenerManager : Serializable {
    private val listeners: MutableList<TaskListener> = ArrayList()

    /**
     * 增加监听器
     * @param listener [TaskListener]
     * @return this
     */
    fun addListener(listener: TaskListener): TaskListenerManager {
        synchronized(listeners) { listeners.add(listener) }
        return this
    }

    /**
     * 移除监听器
     * @param listener [TaskListener]
     * @return this
     */
    fun removeListener(listener: TaskListener): TaskListenerManager {
        synchronized(listeners) { listeners.remove(listener) }
        return this
    }

    /**
     * 通知所有监听任务启动器启动
     * @param executor [TaskExecutor]
     */
    fun notifyTaskStart(executor: TaskExecutor) {
        synchronized(listeners) {
            var listener: TaskListener?
            for (taskListener in listeners) {
                listener = taskListener
                listener.onStart(executor)
            }
        }
    }

    /**
     * 通知所有监听任务启动器成功结束
     * @param executor [TaskExecutor]
     */
    fun notifyTaskSucceeded(executor: TaskExecutor) {
        synchronized(listeners) {
            for (listener in listeners) {
                listener.onSucceeded(executor)
            }
        }
    }

    /**
     * 通知所有监听任务启动器结束并失败<br></br>
     * 无监听将打印堆栈到命令行
     * @param executor [TaskExecutor]
     * @param exception 失败原因
     */
    fun notifyTaskFailed(executor: TaskExecutor, exception: Throwable) {
        synchronized(listeners) {
            val size = listeners.size
            if (size > 0) {
                for (listener in listeners) {
                    listener.onFailed(executor, exception)
                }
            } else {
                LogUtil.e(exception, exception.message?: "")
            }
        }
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
