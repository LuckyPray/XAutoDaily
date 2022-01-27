package me.teble.xposed.autodaily.utils

import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import cn.hutool.core.io.IoUtil
import me.teble.xposed.autodaily.hook.base.Global

class FileUtil {
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