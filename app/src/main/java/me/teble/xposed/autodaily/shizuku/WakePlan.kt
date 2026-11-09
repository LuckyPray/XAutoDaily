package me.teble.xposed.autodaily.shizuku

/**
 * 保存宿主发布的目标时间；checkAt 在漏过检查点或进行有限次数重试时推进。
 */
internal data class WakePlan(val target: Long, val checkAt: Long, val revision: Long, val attempts: Int = 0) {
    val active get() = target > 0 && attempts < MAX_ATTEMPTS
    fun alarmAt(now: Long) = maxOf(checkAt - WakeProtocol.PRE_WAKE_MS, now + 1_000)
    fun catchUp(now: Long): WakePlan = if (active && checkAt < now - 1_000)
        copy(checkAt = WakeProtocol.nextCronCheck(now, now), revision = revision + 1) else this
    fun consumed() = copy(checkAt = WakeProtocol.nextCronCheck(checkAt + WakeProtocol.CRON_CHECK_MS, checkAt),
        revision = revision + 1, attempts = attempts + 1)

    companion object {
        const val MAX_ATTEMPTS = 4
        fun replace(previous: WakePlan?, target: Long): WakePlan {
            require(target >= 0)
            return if (previous?.target == target) previous
            else WakePlan(target, target, (previous?.revision ?: 0) + 1)
        }
    }
}
