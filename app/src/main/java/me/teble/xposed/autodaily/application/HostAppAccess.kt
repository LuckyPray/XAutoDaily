package me.teble.xposed.autodaily.application

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import androidx.core.content.ContextCompat

enum class HostAppListState {
    Checking,
    PermissionRequired,
    Ready,
    Failed,
}

object HostAppAccess {
    const val PERMISSION = "com.android.permission.GET_INSTALLED_APPS"

    fun supportsRuntimePermission(context: Context): Boolean = try {
        val info = context.packageManager.getPermissionInfo(PERMISSION, 0)
        info.packageName == "com.lbe.security.miui" &&
            (info.protectionLevel and PermissionInfo.PROTECTION_MASK_BASE) == PermissionInfo.PROTECTION_DANGEROUS
    } catch (_: PackageManager.NameNotFoundException) {
        false
    }

    fun needsPermission(context: Context): Boolean =
        supportsRuntimePermission(context) &&
            ContextCompat.checkSelfPermission(context, PERMISSION) != PackageManager.PERMISSION_GRANTED
}
