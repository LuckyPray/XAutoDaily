package me.teble.xposed.autodaily.shizuku

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.IUserService
import me.teble.xposed.autodaily.application.xaApp
import me.teble.xposed.autodaily.config.PACKAGE_NAME_QQ
import me.teble.xposed.autodaily.config.PACKAGE_NAME_TIM
import me.teble.xposed.autodaily.ui.ConfUnit

/**
 * 迁移升级前的模块闹钟，新的唤醒计划和闹钟统一由特权守护进程管理。
 */
object HostAlarmMigration {
    private const val OLD_ACTION = "me.teble.xposed.autodaily.WAKE_HOST"
    private val preferences get() = xaApp.getSharedPreferences("host_schedules", 0)
    fun enabled(pkg: String) = ConfUnit.keepAlive && !DaemonConnectionManager.paused && when (pkg) {
        PACKAGE_NAME_QQ -> ConfUnit.qKeepAlive
        PACKAGE_NAME_TIM -> ConfUnit.timKeepAlive
        else -> false
    }
    fun cancelLegacy() {
        val alarms = xaApp.getSystemService(AlarmManager::class.java)
        WakeProtocol.hosts.forEach { pkg ->
            PendingIntent.getBroadcast(xaApp, 0, Intent(xaApp, HostWakeReceiver::class.java)
                .setAction(OLD_ACTION).setData(Uri.parse("xad-wake://host/$pkg")),
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)?.let {
                alarms.cancel(it); it.cancel()
            }
        }
    }
    fun migrate(remote: IUserService) {
        cancelLegacy()
        if (preferences.getBoolean("daemonMigrated", false)) return
        WakeProtocol.hosts.forEach { pkg ->
            if (preferences.contains("$pkg.target")) remote.publishSchedule(pkg, preferences.getLong("$pkg.target", 0), true)
        }
        check(preferences.edit().putBoolean("daemonMigrated", true).commit())
    }
}

/**
 * 保留旧接收器的组件标识以便升级时取消 PendingIntent，仅负责恢复守护连接。
 */
class HostWakeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        DaemonConnectionManager.scope.launch {
            try { runCatching { DaemonConnectionManager.ensureConnected().reloadConfig() } }
            finally { pending.finish() }
        }
    }
}
