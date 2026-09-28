package me.teble.xposed.autodaily.task.cron.timingwheel

import me.teble.xposed.autodaily.utils.TimeUtil
import java.util.concurrent.Delayed
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Consumer
import kotlin.math.max


/**
 * 任务队列，任务双向链表
 *
 * @author siran.yao，looly
 */
class TimerTaskList : Delayed {
    /**
     * 过期时间
     */
    val expire: AtomicLong = AtomicLong(-1L)

    /**
     * 根节点
     */
    private val root: TimerTask = TimerTask(null, -1L)

    /**
     * 设置过期时间
     *
     * @param expire 过期时间，单位毫秒
     * @return 是否设置成功
     */
    fun setExpiration(expire: Long): Boolean {
        return this.expire.getAndSet(expire) != expire
    }

    /**
     * 获取过期时间
     * @return 过期时间
     */
    fun getExpire(): Long {
        return expire.get()
    }

    /**
     * 新增任务，将任务加入到双向链表的头部
     *
     * @param timerTask 延迟任务
     */
    fun addTask(timerTask: TimerTask) {
        synchronized(this) {
            if (timerTask.timerTaskList == null) {
                timerTask.timerTaskList = this
                val tail = root.prev
                timerTask.next = root
                timerTask.prev = tail
                tail!!.next = timerTask
                root.prev = timerTask
            }
        }
    }

    /**
     * 移除任务
     *
     * @param timerTask 任务
     */
    private fun removeTask(timerTask: TimerTask?) {
        synchronized(this) {
            if (this == timerTask!!.timerTaskList) {
                timerTask.next!!.prev = timerTask.prev
                timerTask.prev!!.next = timerTask.next
                timerTask.timerTaskList = null
                timerTask.next = null
                timerTask.prev = null
            }
        }
    }

    /**
     * 重新分配，即将列表中的任务全部处理
     *
     * @param flush 任务处理函数
     */
    @Synchronized
    fun flush(flush: Consumer<TimerTask>) {
        var timerTask = root.next
        while (timerTask != root) {
            removeTask(timerTask)
            flush.accept(timerTask!!)
            timerTask = root.next
        }
        expire.set(-1L)
    }

    override fun getDelay(unit: TimeUnit): Long {
        return max(
            0,
            unit.convert(expire.get() - TimeUtil.cnTimeMillis(), TimeUnit.MILLISECONDS)
        )
    }

    override operator fun compareTo(other: Delayed): Int {
        return if (other is TimerTaskList) {
            expire.get().compareTo(other.expire.get())
        } else 0
    }

    /**
     * 构造
     */
    init {
        root.prev = root
        root.next = root
    }
}
