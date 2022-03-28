package me.teble.xposed.autodaily.utils

import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.os.RemoteException
import android.provider.MediaStore
import android.util.Log
import me.teble.xposed.autodaily.BuildConfig.*
import me.teble.xposed.autodaily.hook.base.Global.hostContext
import java.io.*
import java.time.LocalDateTime
import java.util.zip.Deflater
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import cn.hutool.core.io.FileUtil as HFileUtil


object FileUtil {

    private const val TAG = "XALog"

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

    @JvmStatic
    @Throws(RemoteException::class)
    fun saveLogs() {
        try {
            val exportStream = exportOutputStream("XAutoDaily_${LocalDateTime.now()}.zip")
            ZipOutputStream(exportStream).use { os ->
                val comment: String = "XAutoDaily $BUILD_TYPE $VERSION_NAME ($VERSION_CODE)"
                os.setComment(comment)
                os.setLevel(Deflater.BEST_COMPRESSION)
                zipAddDir(os, logDir, logDir)
            }
        } catch (e: Throwable) {
            Log.w(TAG, "get log", e)
            throw RemoteException(Log.getStackTraceString(e))
        }
    }

    @JvmStatic
    fun zipAddFile(os: ZipOutputStream, file: File, baseDir: File) {
        val relativePath = file.toString().substring(baseDir.toString().length + 1)
        if (file.isDirectory) {
            try {
                os.putNextEntry(ZipEntry(relativePath))
                os.closeEntry()
            } catch (e: IOException) {
                Log.w(TAG, relativePath, e)
            }
        } else if (file.exists()) {
            try {
                FileInputStream(file).use { fis ->
                    os.putNextEntry(ZipEntry(relativePath))
                    fis.copyTo(os)
                    os.closeEntry()
                }
            } catch (e: IOException) {
                Log.w(TAG, relativePath, e)
            }
        }
    }

    @JvmStatic
    fun zipAddDir(os: ZipOutputStream, dir: File, baseDir: File) {
        if (!dir.exists()) return
        if (baseDir != dir) {
            val relativePath = dir.toString().substring(baseDir.toString().length + 1)
            try {
                os.putNextEntry(ZipEntry(relativePath))
                os.closeEntry()
            } catch (e: IOException) {
                Log.w(TAG, relativePath, e)
            }
        }
        dir.listFiles()?.forEach { zipAddFile(os, it, baseDir) }
    }

    @JvmStatic
    fun exportOutputStream(fileName: String): OutputStream {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val dir = File(Environment.getExternalStorageDirectory(), "Download")
            val file = File(dir, fileName)
            return FileOutputStream(file)
        } else {
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            values.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOWNLOADS
            )
            val contentResolver = hostContext.contentResolver
            val uri = contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                values
            )
            uri?.let {
                val stream = contentResolver.openOutputStream(uri)
                return stream ?: throw IOException("Failed to open output stream")
            }
            throw IOException("Failed to open output stream")
        }
    }
}