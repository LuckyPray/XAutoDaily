package me.teble.xposed.autodaily.task.cron

import java.io.Serializable


/**
 * 作业启动管理器
 *
 * @author looly
 */
open class TaskLauncherManager(protected var scheduler: Scheduler) :
    Serializable {
    /** 启动器列表  */
    protected val launchers: MutableList<TaskLauncher> = ArrayList()

    /**
     * 启动 TaskLauncher
     * @param millis 触发事件的毫秒数
     * @return [TaskLauncher]
     */
    fun spawnLauncher(millis: Long): TaskLauncher {
        val launcher = TaskLauncher(scheduler, millis)
        synchronized(launchers) { launchers.add(launcher) }
        //子线程是否为deamon线程取决于父线程，因此此处无需显示调用
        //launcher.setDaemon(this.scheduler.daemon);
//		launcher.start();
        scheduler.threadExecutor!!.execute(launcher)
        return launcher
    }

    /**
     * 启动器启动完毕，启动完毕后从执行器列表中移除
     * @param launcher 启动器 [TaskLauncher]
     */
    fun notifyLauncherCompleted(launcher: TaskLauncher) {
        synchronized(launchers) { launchers.remove(launcher) }
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}