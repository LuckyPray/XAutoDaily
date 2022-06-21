package me.teble.xposed.autodaily.utils

import android.util.Log
import de.robv.android.xposed.XposedBridge
import me.teble.xposed.autodaily.hook.base.ProcUtil
import me.teble.xposed.autodaily.hook.base.hostInit
import me.teble.xposed.autodaily.hook.config.Config
import me.teble.xposed.autodaily.ui.ConfUnit
import java.lang.Integer.min
import java.time.LocalDateTime

object LogUtil {
    private const val tagName = "XALog"
    private const val maxLength = 2000
    private val pid by lazy { ProcUtil.mPid }
    private val logToXposed by lazy { ConfUnit.logToXposed }
    private val toXposed: Boolean get() = Config.isInit && logToXposed

    private fun doLog(
        f: (String, String) -> Unit,
        msg: String?,
        e: Throwable?,
    ) {
        val str = buildString {
            msg?.let { append(it) }
            e?.let { append("\n").append(it.stackTraceToString()) }
        }
        if (str.length > maxLength) {
            var i = 0
            while (i < str.length) {
                val sub = str.substring(i, min(i + maxLength, str.length))
                doLog(f, sub, null)
                i += maxLength
            }
        } else {
            if (hostInit) {
                FileUtil.appendLog("${LocalDateTime.now()} $pid $tagName: $str\n")
            }
            if (toXposed) {
                XposedBridge.log("$tagName : $str")
            } else {
                f(tagName, str)
            }
        }
    }

    @JvmStatic
    fun log(msg: String) {
        i(msg)
    }

    @JvmStatic
    fun d(msg: String) {
        doLog(Log::d, msg, null)
    }

    @JvmStatic
    fun i(msg: String) {
        doLog(Log::i, msg, null)
    }

    @JvmStatic
    fun w(msg: String) {
        doLog(Log::w, msg, null)
    }

    @JvmStatic
    fun e(t: Throwable, msg: String = "") {
        doLog(Log::e, msg, t)
    }

    @JvmStatic
    fun printStackTrace(e: Throwable = RuntimeException("---getStackTrace---")) {
        d(e.stackTraceToString())
    }
}