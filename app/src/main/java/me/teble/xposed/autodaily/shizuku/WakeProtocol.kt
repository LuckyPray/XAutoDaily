package me.teble.xposed.autodaily.shizuku

import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.config.PACKAGE_NAME_QQ
import me.teble.xposed.autodaily.config.PACKAGE_NAME_TIM

object WakeProtocol {
    const val VERSION = 4
    const val AUTHORITY = BuildConfig.APPLICATION_ID + ".hostbridge"
    const val WAKE_PATH = "/xautodaily/wake"
    const val ACTION_SESSION = BuildConfig.APPLICATION_ID + ".HOST_SESSION"
    const val MAX_RUN_MS = 20 * 60_000L
    const val LEASE_TTL_MS = 90_000L
    const val HEARTBEAT_MS = 20_000L
    const val BOOTSTRAP_MS = 60_000L
    const val CRON_GRACE_MS = 90_000L
    const val MAX_PREPARE_MS = 3 * 60_000L
    val hosts = setOf(PACKAGE_NAME_QQ, PACKAGE_NAME_TIM)
}
