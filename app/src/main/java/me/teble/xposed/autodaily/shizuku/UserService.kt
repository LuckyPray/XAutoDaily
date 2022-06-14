package me.teble.xposed.autodaily.shizuku

import android.os.Environment
import android.util.Log
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.IUserService
import me.teble.xposed.autodaily.config.QQClasses.Companion.DataMigrationService
import me.teble.xposed.autodaily.hook.CoreServiceHook.Companion.CORE_SERVICE_FLAG
import me.teble.xposed.autodaily.utils.parse
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.concurrent.Executors
import kotlin.system.exitProcess

class UserService : IUserService.Stub() {

    private val sdcardPath by lazy { Environment.getExternalStorageDirectory() }

    init {
        Executors.newSingleThreadExecutor().execute {
            val conf = loadConf()
            if (!conf.enableKeepAlive) {
                Log.d("XALog", "未启用保活，守护进程退出")
                exit()
                return@execute
            }
            Log.d("XALog", "保活进程正在执行")
            val enablePackage = mutableListOf<String>()
            conf.alivePackages.forEach { (packageName, enable) ->
                if (enable) {
                    enablePackage.add(packageName)
                }
            }
            while (true) {
                enablePackage.forEach { packageName ->
                    runCatching {
                        if (!isAlive(packageName)) {
                            Log.d("XALog", "package: $packageName is died, try start")
                            startService(packageName, DataMigrationService,
                                arrayOf(
                                    "-e", CORE_SERVICE_FLAG, "$"
                                ))
                        }
                    }.onFailure {
                        Log.e("XALog", Log.getStackTraceString(it))
                    }
                }
                Thread.sleep(10_000)
            }
        }
    }

    private fun isAlive(packageName: String): Boolean {
        return execShell("pidof $packageName").isNotEmpty()
    }

    private fun startService(packageName: String, className: String, args: Array<String>) {
        val arg = args.joinToString(" ")
        val shellStr = "am startservice -n $packageName/$className $arg"
        execShell(shellStr)
    }

    private fun execShell(shell: String): String {
        val process = Runtime.getRuntime().exec(shell)
        val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
        return bufferedReader.readText()
    }

    private fun loadConf(): ShizukuConf {
        val confDir = File(sdcardPath, "Android/data/${BuildConfig.APPLICATION_ID}/files/conf")
        if (confDir.isFile) {
            confDir.delete()
        }
        if (!confDir.exists()) {
            confDir.mkdirs()
        }
        val confFile = File(confDir, "conf.json")
        runCatching {
            return confFile.readText().parse()
        }
        return ShizukuConf(false, HashMap())
    }

    fun retry(retryNum: Int, delayTime: Long = 21, block: () -> Boolean): Boolean {
        for (i in 1..retryNum) {
            runCatching {
                return block()
            }
            Thread.sleep(delayTime)
        }
        return false
    }

    override fun destroy() {
        Log.i("XALog", "user service destroy")
        exitProcess(0)
    }

    override fun exit() {
        destroy()
    }

    override fun isRunning(): Boolean {
        return true
    }
}