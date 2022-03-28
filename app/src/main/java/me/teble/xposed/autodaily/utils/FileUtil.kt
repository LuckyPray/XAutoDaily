package me.teble.xposed.autodaily.utils

import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import cn.hutool.core.io.IoUtil
import me.teble.xposed.autodaily.hook.base.Global
import me.teble.xposed.autodaily.hook.base.Global.hostContext
import java.io.File
import java.time.LocalDateTime
import cn.hutool.core.io.FileUtil as HFileUtil

object FileUtil {

    private val logDir by lazy {
        val dir = File(hostContext.filesDir, "xa_log")
        if (dir.isFile) {
            dir.delete()
        }
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return@lazy dir
    }

    private const val maxSingleLogSize = 4 * 1024 * 1024

    private val logFile: File
        get() {
            val file by lazy {
                val file = File(logDir, "log.txt")
                if (!file.exists()) {
                    file.createNewFile()
                }
                return@lazy file
            }
            if (file.length() > maxSingleLogSize) {
                file.renameTo(File(logDir, "log_${LocalDateTime.now()}.log"))
                file.createNewFile()
            }
            return file
        }

    @JvmStatic
    fun appendLog(log: String) {
        HFileUtil.appendUtf8String(log, logFile)
    }

    fun writeFile(fileName: String, content: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {

        } else {
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, "xa.log")
            values.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOWNLOADS
            )
            val contentResolver = Global.hostContext.contentResolver
            val uri = contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                values
            )
            uri?.let {
                val stream = contentResolver.openOutputStream(uri)
                IoUtil.write(stream, true, "123".toByteArray())
            }
        }
    }
}