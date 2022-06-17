package me.teble.xposed.autodaily.hook.base

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import me.teble.xposed.autodaily.utils.runRetry

object ProcUtil {
    const val UNKNOW = 0
    const val MAIN = 1
    const val MSF = 1 shl 1
    const val PEAK = 1 shl 2
    const val TOOL = 1 shl 3
    const val QZONE = 1 shl 4
    const val VIDEO = 1 shl 5
    const val MINI = 1 shl 6
    const val PLUGIN = 1 shl 7
    const val QQFAV = 1 shl 8
    const val TROOP = 1 shl 9
    const val UNITY = 1 shl 10

    const val OTHOE = 1 shl 31
    const val ANY = (1 shl 32) - 1

    val mPid: Int by lazy { Process.myPid() }
    val procName: String by lazy {
        val activityManager = hostApp.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return@lazy runRetry(3) {
            activityManager.runningAppProcesses.forEach {
                if (it.pid == mPid) {
                    return@runRetry it.processName
                }
            }
            return@runRetry null
        } ?: "unknown"
    }
    val procType: Int by lazy {
        val parts = procName.split(":")
        if (parts.size == 1) {
            if (parts.last() == "unknown") {
                return@lazy UNKNOW
            } else return@lazy MAIN
        }
        val tail = parts.last()
        return@lazy when {
            tail == "MSF" -> MSF
            tail == "peak" -> PEAK
            tail == "tool" -> TOOL
            tail.startsWith("qzone") -> QZONE
            tail == "video" -> VIDEO
            tail.startsWith("mini") -> MINI
            tail.startsWith("plugin") -> PLUGIN
            tail.startsWith("troop") -> TROOP
            tail.startsWith("unity") -> UNITY
            tail.startsWith("qqfav") -> QQFAV
            else -> OTHOE
        }
    }

    fun inProcess(flag: Int): Boolean = (procType and flag) != 0

    val isMain: Boolean = inProcess(MAIN)
    val isMSF: Boolean = inProcess(MSF)
    val isPEAK: Boolean = inProcess(PEAK)
    val isTOOL: Boolean = inProcess(TOOL)
    val isQZONE: Boolean = inProcess(QZONE)
    val isVIDEO: Boolean = inProcess(VIDEO)
    val isMINI: Boolean = inProcess(MINI)
    val isPLUGIN: Boolean = inProcess(PLUGIN)
    val isQQFAV: Boolean = inProcess(QQFAV)
    val isTROOP: Boolean = inProcess(TROOP)
    val isUNITY: Boolean = inProcess(UNITY)
    val isAny: Boolean = true
}