package me.teble.xposed.autodaily.task.cron

import cn.hutool.core.util.StrUtil
import me.teble.xposed.autodaily.task.cron.pattent.CronPattern
import me.teble.xposed.autodaily.task.cron.task.CronTask
import me.teble.xposed.autodaily.task.cron.task.Task
import java.io.Serializable
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock


/**
 * 定时任务表<br></br>
 * 任务表将ID、表达式、任务一一对应，定时任务执行过程中，会周期性检查定时任务表中的所有任务表达式匹配情况，从而执行其对应的任务<br></br>
 * 任务的添加、移除使用读写锁保证线程安全性
 *
 * @author Looly
 */
class TaskTable @JvmOverloads constructor(initialCapacity: Int = DEFAULT_CAPACITY) :
    Serializable {
    private val lock: ReadWriteLock = ReentrantReadWriteLock()
    val ids: MutableList<String> = ArrayList(initialCapacity)
    private val patterns: MutableList<CronPattern> = ArrayList(initialCapacity)
    private val tasks: MutableList<Task> = ArrayList(initialCapacity)
    private var size = 0

    fun getTaskIds(): List<String> {
        val readLock: Lock = lock.readLock()
        readLock.lock()
        return try {
            Collections.unmodifiableList(ids)
        } finally {
            readLock.unlock()
        }
    }

    /**
     * 新增Task
     *
     * @param id      ID
     * @param pattern [CronPattern]
     * @param task    [Task]
     * @return this
     */
    fun add(id: String, pattern: CronPattern, task: Task): TaskTable {
        val writeLock: Lock = lock.writeLock()
        writeLock.lock()
        try {
            if (ids.contains(id)) {
                throw CronException("Id [{}] has been existed!", id)
            }
            ids.add(id)
            patterns.add(pattern)
            tasks.add(task)
            size++
        } finally {
            writeLock.unlock()
        }
        return this
    }

    /**
     * 获取所有定时任务表达式，返回不可变列表，即列表不可修改
     *
     * @return 定时任务表达式列表
     * @since 4.6.7
     */
    fun getPatterns(): List<CronPattern> {
        val readLock: Lock = lock.readLock()
        readLock.lock()
        return try {
            Collections.unmodifiableList(patterns)
        } finally {
            readLock.unlock()
        }
    }

    /**
     * 获取所有定时任务，返回不可变列表，即列表不可修改
     *
     * @return 定时任务列表
     * @since 4.6.7
     */
    fun getTasks(): List<Task> {
        val readLock: Lock = lock.readLock()
        readLock.lock()
        return try {
            Collections.unmodifiableList(tasks)
        } finally {
            readLock.unlock()
        }
    }

    /**
     * 移除Task
     *
     * @param id Task的ID
     * @return 是否成功移除，`false`表示未找到对应ID的任务
     */
    fun remove(id: String): Boolean {
        val writeLock: Lock = lock.writeLock()
        writeLock.lock()
        try {
            val index = ids.indexOf(id)
            if (index < 0) {
                return false
            }
            tasks.removeAt(index)
            patterns.removeAt(index)
            ids.removeAt(index)
            size--
        } finally {
            writeLock.unlock()
        }
        return true
    }

    /**
     * 更新某个Task的定时规则
     *
     * @param id      Task的ID
     * @param pattern 新的表达式
     * @return 是否更新成功，如果id对应的规则不存在则不更新
     * @since 4.0.10
     */
    fun updatePattern(id: String, pattern: CronPattern): Boolean {
        val writeLock: Lock = lock.writeLock()
        writeLock.lock()
        try {
            val index = ids.indexOf(id)
            if (index > -1) {
                patterns[index] = pattern
                return true
            }
        } finally {
            writeLock.unlock()
        }
        return false
    }

    /**
     * 获得指定位置的[Task]
     *
     * @param index 位置
     * @return [Task]
     * @since 3.1.1
     */
    fun getTask(index: Int): Task {
        val readLock: Lock = lock.readLock()
        readLock.lock()
        return try {
            tasks[index]
        } finally {
            readLock.unlock()
        }
    }

    /**
     * 获得指定id的[Task]
     *
     * @param id ID
     * @return [Task]
     * @since 3.1.1
     */
    fun getTask(id: String): Task? {
        val index = ids.indexOf(id)
        return if (index > -1) {
            getTask(index)
        } else null
    }

    /**
     * 获得指定位置的[CronPattern]
     *
     * @param index 位置
     * @return [CronPattern]
     * @since 3.1.1
     */
    fun getPattern(index: Int): CronPattern {
        val readLock: Lock = lock.readLock()
        readLock.lock()
        return try {
            patterns[index]
        } finally {
            readLock.unlock()
        }
    }

    /**
     * 任务表大小，加入的任务数
     *
     * @return 任务表大小，加入的任务数
     * @since 4.0.2
     */
    fun size(): Int {
        return size
    }

    /**
     * 任务表是否为空
     *
     * @return true为空
     * @since 4.0.2
     */
    val isEmpty: Boolean
        get() = size < 1

    /**
     * 获得指定id的[CronPattern]
     *
     * @param id ID
     * @return [CronPattern]
     * @since 3.1.1
     */
    fun getPattern(id: String): CronPattern? {
        val index = ids.indexOf(id)
        return if (index > -1) {
            getPattern(index)
        } else null
    }

    /**
     * 如果时间匹配则执行相应的Task，带读锁
     *
     * @param scheduler [Scheduler]
     * @param millis 时间毫秒
     */
    fun executeTaskIfMatch(scheduler: Scheduler, millis: Long) {
        val readLock: Lock = lock.readLock()
        readLock.lock()
        try {
            executeTaskIfMatchInternal(scheduler, millis)
        } finally {
            readLock.unlock()
        }
    }

    override fun toString(): String {
        val builder = StrUtil.builder()
        for (i in 0 until size) {
            builder.append(
                StrUtil.format(
                    "[{}] [{}] [{}]\n",
                    ids[i], patterns[i], tasks[i]
                )
            )
        }
        return builder.toString()
    }

    /**
     * 如果时间匹配则执行相应的Task，无锁
     *
     * @param scheduler [Scheduler]
     * @param millis 时间毫秒
     * @since 3.1.1
     */
    fun executeTaskIfMatchInternal(scheduler: Scheduler, millis: Long) {
        for (i in 0 until size) {
            if (patterns[i].match(
                    scheduler.config.timeZone,
                    millis,
                    scheduler.config.matchSecond
                )
            ) {
                scheduler.taskExecutorManager!!.spawnExecutor(
                    CronTask(
                        ids[i], patterns[i],
                        tasks[i]
                    )
                )
            }
        }
    }

    companion object {
        private const val serialVersionUID = 1L
        const val DEFAULT_CAPACITY = 10
    }
}
