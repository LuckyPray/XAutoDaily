package me.teble.xposed.autodaily.utils

import android.content.Context
import android.os.Build
import me.teble.xposed.autodaily.hook.base.Global
import java.io.IOException
import java.nio.charset.StandardCharsets

fun getAppVersionCode(context: Context, packageName: String): Long {
    return try {
        val pi = context.packageManager.getPackageInfo(packageName, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pi.longVersionCode
        } else {
            pi.versionCode.toLong()
        }
    } catch (e: Exception) {
        LogUtil.e(e)
        -1
    }
}

fun getAppVersionName(context: Context, packageName: String?): String {
    return try {
        val pi = context.packageManager.getPackageInfo(packageName!!, 0)
        pi.versionName
    } catch (e: Exception) {
        e.printStackTrace()
        "unknown"
    }
}

fun getTextFromAssets(context: Context, fileName: String): String {
    try {
        context.assets.open(fileName).use { stream ->
            val length = stream.available()
            val buffer = ByteArray(length)
            stream.read(buffer)
            return String(buffer, StandardCharsets.UTF_8)
        }
    } catch (e: IOException) {
        return ""
    }
}

fun getTextFromModuleAssets(fileName: String): String {
    try {
        Global.moduleClassLoader.getResourceAsStream("assets/$fileName").use { stream ->
            val length = stream.available()
            val buffer = ByteArray(length)
            stream.read(buffer)
            return String(buffer, StandardCharsets.UTF_8)
        }
    } catch (e: Exception) {
        LogUtil.e(e)
    }
    return ""
}