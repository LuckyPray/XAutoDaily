package me.teble.xposed.autodaily.task.cron


/**
 * 作业启动器<br></br>
 * 负责检查 [TaskTable] 是否有匹配到此时运行的Task<br></br>
 * 检查完毕后启动器结束
 *
 * @author Looly
 */
class TaskLauncher
/**
 * 构造
 *
 * @param scheduler [Scheduler]
 * @param millis    毫秒数
 */(private val scheduler: Scheduler, private val millis: Long) :
    Runnable {
    override fun run() {
        //匹配秒部分由用户定义决定，始终不匹配年
        scheduler.taskTable.executeTaskIfMatch(scheduler, millis)

        //结束通知
        scheduler.taskLauncherManager!!.notifyLauncherCompleted(this)
    }
}
