package me.teble.xposed.autodaily.utils

import android.util.Log
import de.robv.android.xposed.XposedBridge
import me.teble.xposed.autodaily.BuildConfig

object LogUtil {
    private const val tagName = "XALog"

    private fun tag(tag: String): String {
        return if (BuildConfig.DEBUG) "$tagName/$tag" else tagName
    }

    fun log(msg: String) {
        d(tagName, msg)
    }

    fun d(tag: String, msg: String) {
//        Log.d(tag(tag), msg)
        XposedBridge.log("D/${tag(tag)}: $msg")
    }

    fun i(msg: String) {
        i(tagName, msg)
    }

    fun i(tag: String, msg: String) {
//        Log.i(tag(tag), msg)
        XposedBridge.log("I/${tag(tag)}: $msg")
    }

    fun w(tag: String, msg: String) {
//        Log.w(tag(tag), msg)
        XposedBridge.log("W/${tag(tag)}: $msg")
    }

    fun e(t: Throwable, msg: String = "") {
        XposedBridge.log(t)
//        if (msg.isEmpty()) {
//            Log.e(tagName, t.stackTraceToString())
//        } else {
//            Log.e(tagName, "$msg -> \n${t.stackTraceToString()}")
//        }
    }

    fun e(tag: String, t: Throwable, msg: String = "") {
        XposedBridge.log(t)
//        if (msg.isEmpty()) {
//            Log.e(tag(tag), t.stackTraceToString())
//        } else {
//            Log.e(tag(tag), "$msg -> \n${t.stackTraceToString()}")
//        }
    }

    fun getStackTrace(tag: String, e: Exception = RuntimeException("---getStackTrace---")) {
        d(tag, Log.getStackTraceString(e))
    }
}