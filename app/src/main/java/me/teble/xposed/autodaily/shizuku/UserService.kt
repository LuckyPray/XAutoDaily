package me.teble.xposed.autodaily.shizuku

import android.os.Environment
import android.util.Log
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.IUserService
import me.teble.xposed.autodaily.config.QQClasses.Companion.KernelService
import me.teble.xposed.autodaily.hook.CoreServiceHook.Companion.CORE_SERVICE_FLAG
import me.teble.xposed.autodaily.hook.CoreServiceHook.Companion.CORE_SERVICE_TOAST_FLAG
import me.teble.xposed.autodaily.utils.parse
import java.io.File
import java.io.RandomAccessFile
import java.util.concurrent.Executors
import kotlin.system.exitProcess

class UserService : IUserService.Stub() {

    private val sdcardPath by lazy { Environment.getExternalStorageDirectory() }

    /**
     * package -> pair<hostLockFile, daemonLockFile>
     */
    private val daemonFileMap = HashMap<String, Pair<RandomAccessFile, RandomAccessFile>>()

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
                    lockFileInit(packageName)
                    val daemonLockFile = daemonFileMap[packageName]!!.second
                    daemonLockFile.channel.lock()
                    Log.d("XALog", "package: $packageName daemonLockFile lock success!")
                }
            }
            while (true) {
                runCatching {
                    enablePackage.forEach {
                        val hostLockFile = daemonFileMap[it]!!.first
                        val lock = hostLockFile.channel.tryLock()
                        if (lock != null) {
                            Log.d("XALog", "package: $it is died, try start")
                            startService(it, KernelService,
                                arrayOf(
                                    "-e", CORE_SERVICE_FLAG, "$",
                                    "-e", CORE_SERVICE_TOAST_FLAG, "$"
                                ))
                        }
                        lock.release()
                    }
                }
                Thread.sleep(60_000)
            }
        }
    }

    private fun startService(packageName: String, className: String, args: Array<String>) {
        val runtime = Runtime.getRuntime()
        val arg = args.joinToString(" ")
        val shellStr = "am startservice -n $packageName/$className $arg"
        runtime.exec(shellStr)
    }

    private fun lockFileInit(packageName: String) {
        val dir = File(sdcardPath, "Android/data/$packageName/files/xa_daemon")
        if (dir.isFile) {
            dir.delete()
        }
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val hostLockFile = File(dir, ".init_host")
        if (!hostLockFile.exists()) hostLockFile.createNewFile()
        val daemonLockFile = File(dir, ".init_service")
        if (!daemonLockFile.exists()) hostLockFile.createNewFile()
        val fileQ = RandomAccessFile(hostLockFile, "rw")
        val fileD = RandomAccessFile(daemonLockFile, "rw")
        daemonFileMap[packageName] = Pair(fileQ, fileD)
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