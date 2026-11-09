package me.teble.xposed.autodaily.shizuku

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.os.Process
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.IHostCallback

/**
 * 提供守护连接的启动兜底及恢复入口，调用者身份由 Binder 提供，不采信传入的 UID 和 PID。
 */
class HostBridgeProvider : ContentProvider() {
    override fun onCreate() = true
    override fun call(method: String, arg: String?, extras: Bundle?): Bundle {
        val pkg = requireNotNull(arg)
        val uid = Binder.getCallingUid()
        val pid = Binder.getCallingPid()
        require(pkg in WakeProtocol.hosts && uid / 100000 == Process.myUid() / 100000)
        check(context!!.packageManager.getPackagesForUid(uid)?.contains(pkg) == true) { "不允许的宿主调用者" }
        val token = Binder.clearCallingIdentity()
        try {
            // Provider 可能早于 Application.onCreate 中的 MMKV 初始化完成对外发布。
            runBlocking { DaemonConnectionManager.awaitApplicationReady() }
            if (method == "schedule") {
                runBlocking { DaemonConnectionManager.ensureConnected().publishSchedule(pkg, extras?.getLong("nextAt") ?: 0, false) }
                return Bundle.EMPTY
            }
            require(method == "connect")
            if (!HostAlarmMigration.enabled(pkg)) return Bundle().apply { putBoolean("enabled", false) }
            check(extras?.getInt("version") == WakeProtocol.VERSION) { "宿主协议已更新，请重启宿主" }
            val callback = IHostCallback.Stub.asInterface(requireNotNull(extras?.getBinder("callback")))
            return runBlocking {
                val remote = DaemonConnectionManager.ensureConnected()
                val session = remote.openHostSession(pkg, uid, pid, callback)
                Bundle().apply {
                    putBoolean("enabled", true)
                    putInt("version", WakeProtocol.VERSION)
                    putBinder("session", session.asBinder())
                }
            }
        } finally { Binder.restoreCallingIdentity(token) }
    }
    override fun getType(uri: Uri): String? {
        if (uri.path != WakeProtocol.RECOVER_PATH) return null
        // getType 可能由 system_server 转发；此入口不接受调用者传入的参数或命令。
        if (Binder.getCallingUid() !in setOf(0, 1000, 2000)) return null
        DaemonConnectionManager.scope.launch {
            runCatching { DaemonConnectionManager.ensureConnected() }
        }
        return "vnd.android.cursor.item/vnd.xautodaily.recover"
    }
    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
}
