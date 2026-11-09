package me.teble.xposed.autodaily.utils

import java.util.concurrent.atomic.AtomicLong

/**
 * 仅缓存已确认的计划；发送失败或对端连接变化后，仍需重新同步。
 */
internal class SchedulePublication(private val send: (Long) -> Boolean) {
    private data class Plan(val account: Long, val nextAt: Long, val generation: Long)
    private var acknowledged: Plan? = null
    private val generation = AtomicLong()

    fun invalidate() {
        // 计划发布可能正在等待新的 Binder，此处不能阻塞连接握手。
        generation.incrementAndGet()
    }

    @Synchronized
    fun publish(account: Long, nextAt: Long): Boolean {
        val plan = Plan(account, nextAt, generation.get())
        if (plan == acknowledged) return false
        if (!send(nextAt)) return false
        acknowledged = plan
        return true
    }
}
