package me.teble.xposed.autodaily.hook.function

import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.utils.LogUtil

abstract class BaseFunction(
    val TAG: String
) {
    abstract fun init()

    val isInit by lazy {
        try {
            init()
            LogUtil.i(TAG, "function [$TAG] is initialize success.$this")
            true
        } catch (e: Exception) {
            ToastUtil.send("初始化功能${TAG}失败。")
            LogUtil.e(e, "function [$TAG] is initialize fail. ${e.message}")
            false
        }
    }
}