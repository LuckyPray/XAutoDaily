package me.teble.xposed.autodaily.hook.handler

import android.annotation.SuppressLint
import android.content.Context
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.utils.BaseException
import me.teble.xposed.autodaily.utils.LogUtil

class CrashCatchHandler : Thread.UncaughtExceptionHandler {
    companion object {
        private val TAG = "CrashCatchHandler"

        @SuppressLint("StaticFieldLeak")
        private var mContext: Context? = null
        private var mDefaultCaughtExceptionHandler: Thread.UncaughtExceptionHandler? = null
        val instance by lazy { CrashCatchHandler() }
    }

    fun init(context: Context) {
        synchronized(this) {
            if (mContext != null) {
                return
            }
            mContext = context
            mDefaultCaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
            LogUtil.d(mDefaultCaughtExceptionHandler.toString())
            Thread.setDefaultUncaughtExceptionHandler(this)

        }
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        LogUtil.d("接收到异常" + e.stackTraceToString())
        when (e) {
            is BaseException -> ToastUtil.send(mContext!!, e.message!!)
            is RuntimeException -> ToastUtil.send(
                mContext!!, "XAutoDaily已阻止QQ闪退：" +
                (e.message ?: "未知异常信息") + "，堆栈信息:\n" + e.stackTraceToString()
            )
            else -> mDefaultCaughtExceptionHandler?.uncaughtException(t, e) ?: run {
                Thread.sleep(3000)
                ToastUtil.send(mContext!!, e.stackTraceToString())
            }
        }
    }
}