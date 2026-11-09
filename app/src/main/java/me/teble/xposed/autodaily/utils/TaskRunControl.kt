package me.teble.xposed.autodaily.utils

import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit

/**
 * 在请求前后检查取消状态，兼容不能可靠响应线程中断的请求实现。
 */
class TaskRunControl(val id: String, durationMs: Long) {
    private val deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(durationMs)
    @Volatile
    var cancelled: String? = null; private set
    fun cancel(reason: String) { cancelled = reason }
    fun check() {
        if (System.nanoTime() - deadline >= 0) cancel("任务运行超过时限")
        if (Thread.currentThread().isInterrupted) cancel("任务线程被中断")
        cancelled?.let { throw CancellationException(it) }
    }
    fun <T> within(block: () -> T): T {
        current.set(this)
        try { check(); return block() } finally { current.remove() }
    }
    companion object {
        private val current = ThreadLocal<TaskRunControl>()
        fun ensureActive() { current.get()?.check() }
    }
}
