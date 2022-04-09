package me.teble.xposed.autodaily.task.cron


import cn.hutool.core.thread.ExecutorBuilder
import cn.hutool.core.thread.ThreadFactoryBuilder
import cn.hutool.core.util.IdUtil
import me.teble.xposed.autodaily.task.cron.listener.TaskListener
import me.teble.xposed.autodaily.task.cron.listener.TaskListenerManager
import me.teble.xposed.autodaily.task.cron.pattent.CronPattern
import me.teble.xposed.autodaily.task.cron.task.RunnableTask
import me.teble.xposed.autodaily.task.cron.task.Task
import java.io.Serializable
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock


/**
 * 任务调度器<br></br>
 *
 * 调度器启动流程：<br></br>
 *
 * <pre>
 * 启动Timer =》 启动TaskLauncher =》 启动TaskExecutor
</pre> *
 *
 * 调度器关闭流程:<br></br>
 *
 * <pre>
 * 关闭Timer =》 关闭所有运行中的TaskLauncher =》 关闭所有运行中的TaskExecutor
</pre> *
 *
 * 其中：
 *
 * <pre>
 * **TaskLauncher**：定时器每分钟调用一次（如果[Scheduler.isMatchSecond]为`true`每秒调用一次），
 * 负责检查**TaskTable**是否有匹配到此时间运行的Task
</pre> *
 *
 * <pre>
 * **TaskExecutor**：TaskLauncher匹配成功后，触发TaskExecutor执行具体的作业，执行完毕销毁
</pre> *
 *
 * @author Looly
 */
open class Scheduler : Serializable {
    private val lock: Lock = ReentrantLock()

    /** 定时任务配置  */
    var config = CronConfig()
    /**
     * @return 是否已经启动
     */
    /** 是否已经启动  */
    var isStarted = false
        private set
    /**
     * 是否为守护线程
     *
     * @return 是否为守护线程
     */
    /** 是否为守护线程  */
    var isDaemon = false
        protected set

    /** 定时器  */
    private var timer: CronTimer? = null
    /**
     * 获取定时任务表，注意此方法返回非复制对象，对返回对象的修改将影响已有定时任务
     *
     * @return 定时任务表{@link TaskTable}
     * @since 4.6.7
     */
    /** 定时任务表  */
    var taskTable = TaskTable()
        protected set

    /** 启动器管理器  */
    var taskLauncherManager: TaskLauncherManager? = null

    /** 执行器管理器  */
    var taskExecutorManager: TaskExecutorManager? = null

    /** 监听管理器列表  */
    var listenerManager: TaskListenerManager = TaskListenerManager()

    /** 线程池，用于执行TaskLauncher和TaskExecutor  */
    var threadExecutor: ExecutorService? = null
    // --------------------------------------------------------- Getters and Setters start
    /**
     * 设置时区
     *
     * @param timeZone 时区
     * @return this
     */
    fun setTimeZone(timeZone: TimeZone): Scheduler {
        config.timeZone = timeZone
        return this
    }

    /**
     * 获得时区，默认为 [TimeZone.getDefault]
     *
     * @return 时区
     */
    val timeZone: TimeZone
        get() = config.timeZone

    /**
     * 设置是否为守护线程<br></br>
     * 如果为true，则在调用[.stop]方法后执行的定时任务立即结束，否则等待执行完毕才结束。默认非守护线程<br></br>
     * 如果用户调用[.setThreadExecutor]自定义线程池则此参数无效
     *
     * @param on `true`为守护线程，否则非守护线程
     * @return this
     * @throws CronException 定时任务已经启动抛出此异常
     */
    @Throws(CronException::class)
    fun setDaemon(on: Boolean): Scheduler {
        lock.lock()
        try {
            checkStarted()
            isDaemon = on
        } finally {
            lock.unlock()
        }
        return this
    }

    /**
     * 设置自定义线程池<br></br>
     * 自定义线程池时须考虑方法执行的线程是否为守护线程
     *
     * @param threadExecutor 自定义线程池
     * @return this
     * @throws CronException 定时任务已经启动抛出此异常
     * @since 5.7.10
     */
    @Throws(CronException::class)
    fun setThreadExecutor(threadExecutor: ExecutorService): Scheduler {
        lock.lock()
        try {
            checkStarted()
            this.threadExecutor = threadExecutor
        } finally {
            lock.unlock()
        }
        return this
    }

    var matchSecond: Boolean
        get() = config.matchSecond
        set(value) {
            config.matchSecond = value
        }

    /**
     * 增加监听器
     *
     * @param listener [TaskListener]
     * @return this
     */
    fun addListener(listener: TaskListener): Scheduler {
        listenerManager.addListener(listener)
        return this
    }

    /**
     * 移除监听器
     *
     * @param listener [TaskListener]
     * @return this
     */
    fun removeListener(listener: TaskListener): Scheduler {
        listenerManager.removeListener(listener)
        return this
    }
    // --------------------------------------------------------- Getters and Setters end
    // -------------------------------------------------------------------- shcedule start


    /**
     * 新增Task，使用随机UUID
     *
     * @param pattern [CronPattern]对应的String表达式
     * @param task [Runnable]
     * @return ID
     */
    fun schedule(pattern: String, task: Runnable): String {
        return schedule(pattern, RunnableTask(task))
    }

    /**
     * 新增Task，使用随机UUID
     *
     * @param pattern [CronPattern]对应的String表达式
     * @param task [Task]
     * @return ID
     */
    fun schedule(pattern: String, task: Task): String {
        val id = IdUtil.fastUUID()
        schedule(id, pattern, task)
        return id
    }

    /**
     * 新增Task，如果任务ID已经存在，抛出异常
     *
     * @param id ID，为每一个Task定义一个ID
     * @param pattern [CronPattern]对应的String表达式
     * @param task [Runnable]
     * @return this
     */
    fun schedule(id: String, pattern: String, task: Runnable): Scheduler {
        return schedule(id, CronPattern(pattern), RunnableTask(task))
    }

    /**
     * 新增Task，如果任务ID已经存在，抛出异常
     *
     * @param id ID，为每一个Task定义一个ID
     * @param pattern [CronPattern]对应的String表达式
     * @param task [Task]
     * @return this
     */
    fun schedule(id: String, pattern: String, task: Task): Scheduler {
        return schedule(id, CronPattern(pattern), task)
    }

    /**
     * 新增Task，如果任务ID已经存在，抛出异常
     *
     * @param id ID，为每一个Task定义一个ID
     * @param pattern [CronPattern]
     * @param task [Task]
     * @return this
     */
    fun schedule(id: String, pattern: CronPattern, task: Task): Scheduler {
        taskTable.add(id, pattern, task)
        return this
    }

    /**
     * 移除Task
     *
     * @param id Task的ID
     * @return this
     */
    fun deschedule(id: String): Scheduler {
        descheduleWithStatus(id)
        return this
    }

    /**
     * 移除Task，并返回是否移除成功
     *
     * @param id Task的ID
     * @return 是否移除成功，`false`表示未找到对应ID的任务
     * @since 5.7.17
     */
    fun descheduleWithStatus(id: String): Boolean {
        return taskTable.remove(id)
    }

    /**
     * 更新Task执行的时间规则
     *
     * @param id Task的ID
     * @param pattern [CronPattern]
     * @return this
     * @since 4.0.10
     */
    fun updatePattern(id: String, pattern: CronPattern): Scheduler {
        taskTable.updatePattern(id, pattern)
        return this
    }

    /**
     * 获得指定id的[CronPattern]
     *
     * @param id ID
     * @return [CronPattern]
     * @since 3.1.1
     */
    fun getPattern(id: String): CronPattern? {
        return taskTable.getPattern(id)
    }

    /**
     * 获得指定id的[Task]
     *
     * @param id ID
     * @return [Task]
     * @since 3.1.1
     */
    fun getTask(id: String): Task? {
        return taskTable.getTask(id)
    }

    /**
     * 是否无任务
     *
     * @return true表示无任务
     * @since 4.0.2
     */
    fun isEmpty(): Boolean {
        return taskTable.isEmpty
    }

    /**
     * 当前任务数
     *
     * @return 当前任务数
     * @since 4.0.2
     */
    fun size(): Int {
        return taskTable.size()
    }

    /**
     * 清空任务表
     * @return this
     * @since 4.1.17
     */
    fun clear(): Scheduler {
        taskTable = TaskTable()
        return this
    }
    // -------------------------------------------------------------------- shcedule end
    /**
     * 启动
     *
     * @param isDaemon 是否以守护线程方式启动，如果为true，则在调用[.stop]方法后执行的定时任务立即结束，否则等待执行完毕才结束。
     * @return this
     */
    fun start(isDaemon: Boolean): Scheduler {
        this.isDaemon = isDaemon
        return start()
    }

    /**
     * 启动
     *
     * @return this
     */
    fun start(): Scheduler {
        lock.lock()
        try {
            checkStarted()
            if (null == threadExecutor) {
                // 无界线程池，确保每一个需要执行的线程都可以及时运行，同时复用已有线程避免线程重复创建
                threadExecutor = ExecutorBuilder.create().useSynchronousQueue().setThreadFactory( //
                    ThreadFactoryBuilder.create().setNamePrefix("hutool-cron-").setDaemon(isDaemon)
                        .build() //
                ).build()
            }
            taskLauncherManager = TaskLauncherManager(this)
            taskExecutorManager = TaskExecutorManager(this)

            // Start CronTimer
            timer = CronTimer(this)
            timer!!.isDaemon = isDaemon
            timer!!.start()
            isStarted = true
        } finally {
            lock.unlock()
        }
        return this
    }
    /**
     * 停止定时任务<br></br>
     * 此方法调用后会将定时器进程立即结束，如果为守护线程模式，则正在执行的作业也会自动结束，否则作业线程将在执行完成后结束。
     *
     * @param clearTasks 是否清除所有任务
     * @return this
     * @since 4.1.17
     */
    /**
     * 停止定时任务<br></br>
     * 此方法调用后会将定时器进程立即结束，如果为守护线程模式，则正在执行的作业也会自动结束，否则作业线程将在执行完成后结束。<br></br>
     * 此方法并不会清除任务表中的任务，请调用[.clear] 方法清空任务或者使用[.stop]方法可选是否清空
     *
     * @return this
     */
    @JvmOverloads
    fun stop(clearTasks: Boolean = false): Scheduler {
        lock.lock()
        try {
            check(isStarted) { "Scheduler not started !" }

            // 停止CronTimer
            timer!!.stopTimer()
            timer = null

            //停止线程池
            threadExecutor?.shutdown()
            threadExecutor = null

            //可选是否清空任务表
            if (clearTasks) {
                clear()
            }

            // 修改标志
            isStarted = false
        } finally {
            lock.unlock()
        }
        return this
    }

    /**
     * 检查定时任务是否已经启动
     *
     * @throws CronException 已经启动则抛出此异常
     */
    @Throws(CronException::class)
    private fun checkStarted() {
        if (isStarted) {
            throw CronException("Scheduler already started!")
        }
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
