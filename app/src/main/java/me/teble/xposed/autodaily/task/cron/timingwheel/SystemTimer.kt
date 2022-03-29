package me.teble.xposed.autodaily.task.cron.timingwheel

import cn.hutool.core.thread.ThreadUtil
import java.util.concurrent.DelayQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit


/**
 * 系统计时器
 *
 * @author eliasyaoyc, looly
 */
class SystemTimer {
    /**
     * 底层时间轮
     */
    private val timeWheel: TimingWheel

    /**
     * 一个Timer只有一个delayQueue
     */
    private val delayQueue: DelayQueue<TimerTaskList> = DelayQueue()

    /**
     * 执行队列取元素超时时长，单位毫秒，默认100
     */
    private var delayQueueTimeout: Long = 100

    /**
     * 轮询delayQueue获取过期任务线程
     */
    private lateinit var bossThreadPool: ExecutorService

    /**
     * 设置执行队列取元素超时时长，单位毫秒
     * @param delayQueueTimeout 执行队列取元素超时时长，单位毫秒
     * @return this
     */
    fun setDelayQueueTimeout(delayQueueTimeout: Long): SystemTimer {
        this.delayQueueTimeout = delayQueueTimeout
        return this
    }

    /**
     * 启动，异步
     *
     * @return this
     */
    fun start(): SystemTimer {
        bossThreadPool = ThreadUtil.newSingleExecutor()
        bossThreadPool.submit {
            while (true) {
                if (!advanceClock()) {
                    break
                }
            }
        }
        return this
    }

    /**
     * 强制结束
     */
    fun stop() {
        bossThreadPool.shutdown()
    }

    /**
     * 添加任务
     *
     * @param timerTask 任务
     */
    fun addTask(timerTask: TimerTask) {
        //添加失败任务直接执行
        if (!timeWheel.addTask(timerTask)) {
            ThreadUtil.execAsync(timerTask.task)
        }
    }

    /**
     * 指针前进并获取过期任务
     *
     * @return 是否结束
     */
    private fun advanceClock(): Boolean {
        try {
            val timerTaskList = poll()
            //推进时间
            timeWheel.advanceClock(timerTaskList.expire.get())
            //执行过期任务（包含降级操作）
            timerTaskList.flush { timerTask: TimerTask ->
                addTask(
                    timerTask
                )
            }
        } catch (ignore: InterruptedException) {
            return false
        }
        return true
    }

    /**
     * 执行队列取任务列表
     * @return 任务列表
     * @throws InterruptedException 中断异常
     */
    @Throws(InterruptedException::class)
    private fun poll(): TimerTaskList {
        return if (delayQueueTimeout > 0) delayQueue.poll(
            delayQueueTimeout,
            TimeUnit.MILLISECONDS
        ) else delayQueue.poll()!!
    }

    /**
     * 构造
     */
    init {
        timeWheel = TimingWheel(1, 20, delayQueue::offer)
    }
}
