package me.teble.xposed.autodaily.hook.utils

import com.tencent.qphone.base.remote.FromServiceMsg
import me.teble.xposed.autodaily.config.QQClasses
import me.teble.xposed.autodaily.config.QQClasses.Companion.StGetCodeRsp
import me.teble.xposed.autodaily.config.QQClasses.Companion.StGetProfileRsp
import me.teble.xposed.autodaily.config.QQClasses.Companion.StQWebRsp
import me.teble.xposed.autodaily.hook.base.load
import me.teble.xposed.autodaily.task.model.MiniProfile
import me.teble.xposed.autodaily.utils.fieldValue
import me.teble.xposed.autodaily.utils.invoke
import me.teble.xposed.autodaily.utils.invokeAs
import me.teble.xposed.autodaily.utils.new

object ServiceMsgParseUtil : QQClasses {

    fun parseLoginCode(fromServiceMsg: FromServiceMsg): String? {
        val res = load(StQWebRsp)?.new()
        var wupBuffer = fromServiceMsg.fieldValue("wupBuffer") as ByteArray?
        wupBuffer = WupUtil.decode(wupBuffer)
        res?.invoke("mergeFrom", wupBuffer)
        val busiBuff = res?.fieldValue("busiBuff")
        val byteString = busiBuff?.invoke("get")
        val bytes = byteString?.invoke("toByteArray")
        val codeRes = load(StGetCodeRsp)?.new()
        codeRes?.invoke("mergeFrom", bytes)
        val pbCode = codeRes?.fieldValue("code")
        return pbCode?.invokeAs("get")
    }

    fun parseProfile(fromServiceMsg: FromServiceMsg): MiniProfile {
        val res = load(StQWebRsp)?.new()
        var wupBuffer = fromServiceMsg.fieldValue("wupBuffer") as ByteArray?
        wupBuffer = WupUtil.decode(wupBuffer)
        res?.invoke("mergeFrom", wupBuffer)
        val busiBuff = res?.fieldValue("busiBuff")
        val byteString = busiBuff?.invoke("get")
        val bytes = byteString?.invoke("toByteArray")
        val profileRes = load(StGetProfileRsp)?.new()
        profileRes?.invoke("mergeFrom", bytes)
        val userInfo = profileRes?.fieldValue("user")
        val avatar: String = userInfo?.fieldValue("avatar")?.invokeAs("get") ?: ""
        val nick: String = userInfo?.fieldValue("nick")?.invokeAs("get") ?: ""
        return MiniProfile(avatar, nick)
    }
}