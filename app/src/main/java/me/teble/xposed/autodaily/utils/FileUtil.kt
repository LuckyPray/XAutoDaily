package me.teble.xposed.autodaily.utils

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.os.RemoteException
import android.provider.MediaStore
import me.teble.xposed.autodaily.BuildConfig.*
import me.teble.xposed.autodaily.hook.base.hostContext
import me.teble.xposed.autodaily.hook.config.Config
import me.teble.xposed.autodaily.task.util.parseDateTime
import me.teble.xposed.autodaily.task.util.timestamp
import java.io.*
import java.time.LocalDateTime
import java.util.zip.Deflater
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
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
    private const val logKeepDays = 15

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
                clearLog(logKeepDays)
            }
            return file
        }

    private fun clearLog(keepDays: Int) {
        val currTime = LocalDateTime.now()
        logDir.listFiles()?.forEach { file ->
            if (file.isFile && file.name.startsWith("log_") && file.name.endsWith(".log")) {
                val timeStr = file.name.substring(4, file.name.length - 4)
                parseDateTime(timeStr)?.let {
                    if (currTime.timestamp - it.timestamp > keepDays * 24 * 3600) {
                        file.delete()
                    }
                }
            }
        }
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
                val comment = "XAutoDaily $BUILD_TYPE $VERSION_NAME ($VERSION_CODE)"
                os.setComment(comment)
                os.setLevel(Deflater.BEST_COMPRESSION)
                zipAddDir(os, logDir, logDir)
            }
        } catch (e: Throwable) {
            LogUtil.e(e, "get log")
            throw RemoteException(e.stackTraceToString())
        }
    }

    @JvmStatic
    @Throws(RemoteException::class)
    fun saveLogs(zipFd: ParcelFileDescriptor) {
        try {
            ZipOutputStream(FileOutputStream(zipFd.fileDescriptor)).use { os ->
                val comment = "XAutoDaily $BUILD_TYPE $VERSION_NAME ($VERSION_CODE)"
                os.setComment(comment)
                os.setLevel(Deflater.BEST_COMPRESSION)
                zipAddDir(os, logDir, logDir)
            }
        } catch (e: Throwable) {
            LogUtil.e(e, "get log")
            throw RemoteException(e.message)
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
                LogUtil.e(e, relativePath)
            }
        } else if (file.exists()) {
            try {
                FileInputStream(file).use { fis ->
                    os.putNextEntry(ZipEntry(relativePath))
                    fis.copyTo(os)
                    os.closeEntry()
                }
            } catch (e: IOException) {
                LogUtil.e(e, relativePath)
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
                LogUtil.e(e, relativePath)
            }
        }
        dir.listFiles()?.forEach { zipAddFile(os, it, baseDir) }
    }


    @JvmStatic
    @Suppress("DEPRECATION")
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

    @JvmStatic
    fun backupConfig() {
        val backupOutputStream = exportOutputStream("XAutoDaily_config_${LocalDateTime.now()}.zip")
        val configDir = Config.mmkvDir
        val configFiles = listConfigFiles(configDir)
        ZipOutputStream(backupOutputStream).use { os ->
            val comment = "XAutoDaily $BUILD_TYPE $VERSION_NAME ($VERSION_CODE)"
            os.setComment(comment)
            os.setLevel(Deflater.BEST_COMPRESSION)
            configFiles.forEach {
                zipAddFile(os, it, configDir)
            }
        }
    }

    @JvmStatic
    fun backupConfig(zipFd: ParcelFileDescriptor) {
        val configDir = Config.mmkvDir
        val configFiles = listConfigFiles(configDir)
        ZipOutputStream(FileOutputStream(zipFd.fileDescriptor)).use { os ->
            val comment = "XAutoDaily $BUILD_TYPE $VERSION_NAME ($VERSION_CODE)"
            os.setComment(comment)
            os.setLevel(Deflater.BEST_COMPRESSION)
            configFiles.forEach {
                zipAddFile(os, it, configDir)
            }
        }
    }

    @JvmStatic
    fun restoreBackupConfig(backupFile: File, restoreDir: File) {
        if (!restoreDir.exists()) {
            restoreDir.mkdirs()
        }
        ZipFile(backupFile).use { zip ->
            LogUtil.d("restoreBackupConfig, zip.size: ${zip.size()}")
            val entries = zip.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                LogUtil.log("file name -> ${entry.name}")
                if (entry.isDirectory) {
                    val dir = File(restoreDir, entry.name)
                    if (dir.isFile) {
                        dir.delete()
                    }
                    if (!dir.exists()) {
                        dir.mkdirs()
                    }
                } else {
                    val inputStream = zip.getInputStream(entry)
                    val fileName = entry.name
                    val file = File(restoreDir, fileName)
                    if (file.exists()) {
                        file.delete()
                    }
                    FileOutputStream(file).use { fos ->
                        inputStream.copyTo(fos)
                    }
                }
            }
        }
    }

    private fun listConfigFiles(configDir: File): List<File> {
        val backupFiles = mutableListOf<File>()
        configDir.listFiles()?.forEach {
            val name = it.name
            if (it.isFile && name.endsWith(".crc")) {
                val config = File(it.absolutePath.substringBeforeLast(".crc"))
                if (config.exists()) {
                    backupFiles.add(config)
                    backupFiles.add(it)
                }
            }
        }
        return backupFiles
    }

    @JvmStatic
    fun deleteDir(dir: File) {
        if (dir.isDirectory) {
            dir.listFiles()?.forEach {
                deleteDir(it)
            }
        }
        dir.delete()
    }

    fun saveFile(uri: Uri, file: File) {
        hostContext.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
    }
}