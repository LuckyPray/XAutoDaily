package me.teble.xposed.autodaily.shizuku

import android.content.Context
import android.os.Bundle
import me.teble.xposed.autodaily.BuildConfig

/**
 * 保存进程启动时的模块安装信息，识别同版本号下重新安装的修复包。
 */
internal object DaemonBuild {
    fun read(context: Context): Bundle {
        val info = context.packageManager.getPackageInfo(BuildConfig.APPLICATION_ID, 0)
        return Bundle().apply {
            putInt("daemonVersionCode", BuildConfig.VERSION_CODE)
            putString("daemonVersionName", BuildConfig.VERSION_NAME)
            putString("daemonApkPath", context.applicationInfo.sourceDir)
            putLong("daemonUpdatedAt", info.lastUpdateTime)
            putInt("daemonProtocol", WakeProtocol.VERSION)
        }
    }

    /**
     * 为 Shizuku 提供安装版本号，连接后仍核对完整信息，避免哈希碰撞误用旧守护。
     */
    fun serviceVersion(info: Bundle): Int = (31 * info.getLong("daemonUpdatedAt").hashCode() +
        info.getString("daemonApkPath").orEmpty().hashCode() + WakeProtocol.VERSION) and Int.MAX_VALUE

    fun matches(actual: Bundle, expected: Bundle): Boolean =
        actual.getInt("daemonProtocol") == expected.getInt("daemonProtocol") &&
            actual.getInt("daemonVersionCode") == expected.getInt("daemonVersionCode") &&
            actual.getString("daemonVersionName") == expected.getString("daemonVersionName") &&
            actual.getLong("daemonUpdatedAt") == expected.getLong("daemonUpdatedAt") &&
            actual.getString("daemonApkPath") == expected.getString("daemonApkPath")
}
