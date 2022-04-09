package me.teble.xposed.autodaily.task.cron.task

import me.teble.xposed.autodaily.task.cron.pattent.CronPattern


/**
 * 定时作业，此类除了定义了作业，也定义了作业的执行周期以及ID。
 *
 * @author looly
 * @since 5.4.7
 */
class CronTask(
    val id: String, pattern: CronPattern, task: Task
) : Task {
    private var pattern: CronPattern
    private val task: Task
    override fun execute() {
        task.execute()
    }

    /**
     * 获取表达式
     *
     * @return 表达式
     */
    fun getPattern(): CronPattern {
        return pattern
    }

    /**
     * 设置新的定时表达式
     * @param pattern 表达式
     * @return this
     */
    fun setPattern(pattern: CronPattern): CronTask {
        this.pattern = pattern
        return this
    }

    /**
     * 获取原始作业
     *
     * @return 作业
     */
    val raw: Task
        get() = task

    /**
     * 构造
     * @param id ID
     * @param pattern 表达式
     * @param task 作业
     */
    init {
        this.pattern = pattern
        this.task = task
    }
}