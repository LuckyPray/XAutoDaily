package me.teble.xposed.autodaily.shizuku

/**
 * 使用单调时钟管理防冻结租约，并在调用 am 前写入恢复记录，处理命令结果不明确的情况。
 */
internal class StickyLeaseRegistry<P>(
    private val clock: () -> Long,
    private val alive: (P) -> Boolean,
    private val unfreeze: (P, Boolean) -> Boolean,
    private val journal: (Set<P>) -> Unit,
    recovered: Set<P> = emptySet(),
) {
    private data class Lease<P>(val processes: MutableSet<P>, val deadline: Long, var expires: Long)
    private val leases = mutableMapOf<String, Lease<P>>()
    private val protected = recovered.toMutableSet()

    @Synchronized
    fun acquire(id: String, processes: Set<P>, duration: Long, ttl: Long) {
        require(id.isNotBlank() && processes.isNotEmpty() && duration > 0 && ttl > 0)
        check(id !in leases) { "任务保护已存在" }
        val now = clock()
        leases[id] = Lease(mutableSetOf(), now + duration, now + minOf(duration, ttl))
        try {
            attach(id, processes)
        } catch (e: Exception) {
            release(id)
            throw e
        }
    }

    @Synchronized
    fun attach(id: String, processes: Set<P>) {
        val lease = checkNotNull(leases[id])
        for (process in processes) {
            check(alive(process)) { "宿主进程已退出" }
            if (leases.values.none { process in it.processes }) {
                journal(protected + process)
                protected.add(process)
                check(unfreeze(process, true)) { "ROM 未确认 am unfreeze --sticky，任务未开始" }
            }
            lease.processes.add(process)
        }
    }

    @Synchronized
    fun renew(id: String, ttl: Long): Boolean {
        val lease = leases[id] ?: return false
        val now = clock()
        if (now >= lease.expires || now >= lease.deadline) return false
        lease.expires = minOf(lease.deadline, now + ttl)
        return true
    }

    @Synchronized
    fun release(id: String) {
        leases.remove(id)
        restoreUnused()
    }

    @Synchronized
    fun sweep(): List<String> {
        val now = clock()
        val expired = leases.filterValues { now >= it.expires || it.processes.any { p -> !alive(p) } }.keys.toList()
        expired.forEach { leases.remove(it) }
        restoreUnused()
        return expired
    }

    @Synchronized
    fun close() {
        leases.clear()
        restoreUnused()
    }

    @Synchronized
    fun hasPendingRestore(): Boolean = protected.any { p -> leases.values.none { p in it.processes } }

    private fun restoreUnused() {
        val owned = leases.values.flatMap { it.processes }.toSet()
        // 恢复失败的进程继续保留在记录中，由定期清理任务或重启后的守护进程重试。
        val removed = protected.filter { it !in owned && (!alive(it) || unfreeze(it, false)) }
        if (removed.isNotEmpty()) {
            val remaining = protected - removed.toSet()
            journal(remaining)
            protected.removeAll(removed.toSet())
        }
    }
}
