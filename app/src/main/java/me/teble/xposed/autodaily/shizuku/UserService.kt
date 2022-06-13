package me.teble.xposed.autodaily.shizuku

import android.os.Environment
import android.util.Log
import me.teble.xposed.autodaily.IUserService
import me.teble.xposed.autodaily.config.Constants.PACKAGE_NAME_QQ
import me.teble.xposed.autodaily.utils.TimeUtil
import java.io.File
import java.io.RandomAccessFile
import java.util.concurrent.Executors

class UserService: IUserService.Stub() {

    init {
        Executors.newSingleThreadExecutor().execute {
            TimeUtil.init()
            val sdcard = Environment.getExternalStorageDirectory()
            val qqExternalFileDir = File(sdcard, "Android/data/$PACKAGE_NAME_QQ")
            val qqLockFile = File(qqExternalFileDir, ".init_1101_q")
            val daemonLockFile = File(qqExternalFileDir, ".init_1101_d")
            // TODO check install QQ and TIM
            val fileQ = RandomAccessFile(qqLockFile, "rw")
            val fileD = RandomAccessFile(daemonLockFile, "rw")

            while (true) {
                try {
                    Log.d("UserService", "当前时间: " + TimeUtil.cnTimeMillis())
                    Thread.sleep(60_000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun destroy() {
        Log.i("UserService", "destroy")
    }

    override fun exit() {
        Log.i("UserService", "exit")
    }
}