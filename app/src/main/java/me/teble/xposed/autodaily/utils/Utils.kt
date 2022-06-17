@file:JvmName("ModuleUtils")

package me.teble.xposed.autodaily.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import me.teble.xposed.autodaily.hook.base.moduleRes
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

fun getTextFromAssets(context: Context, fileName: String): String {
    runCatching {
        context.assets.open(fileName).use { stream ->
            val length = stream.available()
            val buffer = ByteArray(length)
            stream.read(buffer)
            return String(buffer, StandardCharsets.UTF_8)
        }
    }.onFailure { LogUtil.e(it) }
    return ""
}

fun getTextFromModuleAssets(fileName: String): String {
    runCatching {
        moduleRes.assets.open(fileName).use { stream ->
            val bis = BufferedInputStream(stream)
            val bos = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var len: Int
            while (bis.read(buffer).also { len = it } != -1) {
                bos.write(buffer, 0, len)
            }
            return String(bos.toByteArray())
        }
    }.onFailure { LogUtil.e(it) }
    return ""
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


fun <T> runRetry(retryNum: Int, block: () -> T): T? {
    for (i in 1..retryNum) {
        runCatching {
            val result = block()
            if (result != null) {
                return result
            }
        }.onFailure {
            LogUtil.e(it)
        }
    }
    return null
}