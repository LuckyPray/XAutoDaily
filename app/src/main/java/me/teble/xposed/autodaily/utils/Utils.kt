@file:JvmName("ModuleUtils")

package me.teble.xposed.autodaily.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import me.teble.xposed.autodaily.hook.MainHook
import me.teble.xposed.autodaily.hook.base.moduleClassLoader
import me.teble.xposed.autodaily.hook.base.modulePath
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

@Suppress("DEPRECATION")
fun getAppVersionCode(context: Context, packageName: String): Long {
    runCatching {
        val pi = context.packageManager.getPackageInfo(packageName, 0)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pi.longVersionCode
        } else {
            pi.versionCode.toLong()
        }
    }.onFailure { LogUtil.e(it) }
    return -1
}

fun getAppVersionName(context: Context, packageName: String): String {
    runCatching {
        val pi = context.packageManager.getPackageInfo(packageName, 0)
        return pi.versionName
    }.onFailure { LogUtil.e(it) }
    return "unknown"
}

fun getAppName(context: Context, packageName: String): String {
    runCatching {
        val pm = context.packageManager
        val pi = pm.getApplicationInfo(packageName, 0)
        return pm.getApplicationLabel(pi).toString()
    }.onFailure { LogUtil.e(it) }
    return "unknown"
}

fun getAppIcon(context: Context, packageName: String): Bitmap? {
    runCatching {
        val bd = context.packageManager.getApplicationIcon(packageName) as BitmapDrawable
        return bd.bitmap
    }.onFailure { LogUtil.e(it) }
    return null
}

fun getTextFromAssets(classLoader: ClassLoader, fileName: String): String {
    runCatching {
        classLoader.getResourceAsStream("assets/$fileName").use { stream ->
            val bis = BufferedInputStream(stream)
            val bos = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var len: Int
            while (bis.read(buffer).also { len = it } != -1) {
                bos.write(buffer, 0, len)
            }
            return String(bos.toByteArray(), StandardCharsets.UTF_8)
        }
    }.onFailure { LogUtil.e(it) }
    return ""
}

fun getTextFromModuleAssets(fileName: String): String {
    return getTextFromAssets(moduleClassLoader, fileName)
}


fun Context.openUrl(url: String) {
    val contentUrl = Uri.parse(url)
    val intent = Intent()
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.action = Intent.ACTION_VIEW
    intent.data = contentUrl
    runCatching {
        this.startActivity(intent)
    }.onFailure {
        LogUtil.e(it)
        throw it
    }
}


fun <T> runRetry(retryNum: Int, sleepMs: Long = 0, block: () -> T?): T? {
    for (i in 1..retryNum) {
        runCatching {
            return block()
        }.onFailure {
            LogUtil.e(it)
        }
        if (sleepMs > 0) Thread.sleep(sleepMs)
    }
    return null
}

fun Int.dp2px(ctx: Context): Int {
    return (this * ctx.getDensity() + 0.5f).toInt()
}

fun Context.getDensity(): Float {
    return this.resources.displayMetrics.density
}

fun getArtApexVersion(context: Context): Long {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        return Build.VERSION.SDK_INT.toLong()
    }

    try {
        val packageManager = context.packageManager
        val info = packageManager.getPackageInfo("com.google.android.art", PackageManager.MATCH_APEX)
        return info.longVersionCode
    } catch (e: Throwable) {
        LogUtil.e(e, "err: ")
        return -1
    }
}

fun getModulePath(): String {
    runCatching {
        return MainHook::class.java.classLoader!!
            .invoke("findResource", "AndroidManifest.xml")!!
            .invoke("getPath") as String
    }
    return modulePath
}