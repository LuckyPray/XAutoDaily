package me.teble.xposed.autodaily.hook.function.base

import me.teble.xposed.autodaily.hook.notification.XANotification
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.utils.LogUtil

abstract class BaseFunction(
    val TAG: String
) {
    abstract fun init()

    val isInit by lazy {
        try {
            init()
            LogUtil.i("function [$TAG] is initialize success.$this")
            true
        } catch (e: Exception) {
            ToastUtil.send("初始化功能${TAG}失败。")
            XANotification.notify("初始化功能${TAG}失败，可能是由于版本过高/低，请提取日志前往tg/github反馈")
            LogUtil.e(e, "function [$TAG] is initialize fail. ${e.message}")
            false
        }
    }
}