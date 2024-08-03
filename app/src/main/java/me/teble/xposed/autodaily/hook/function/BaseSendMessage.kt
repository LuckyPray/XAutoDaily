package me.teble.xposed.autodaily.hook.function

import me.teble.xposed.autodaily.hook.function.base.BaseFunction

abstract class BaseSendMessage(TAG: String): BaseFunction(TAG) {
    abstract fun sendTextMessage(uin: String, msg: String, isGroup: Boolean)
}