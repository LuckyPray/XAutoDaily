package me.teble.xposed.autodaily.hook.utils

import com.tencent.qphone.base.remote.FromServiceMsg
import me.teble.xposed.autodaily.config.QQClasses
import me.teble.xposed.autodaily.config.QQClasses.Companion.StGetCodeRsp
import me.teble.xposed.autodaily.config.QQClasses.Companion.StGetProfileRsp
import me.teble.xposed.autodaily.config.QQClasses.Companion.StQWebRsp
import me.teble.xposed.autodaily.hook.base.Initiator.load
import me.teble.xposed.autodaily.task.module.MiniProfile
import me.teble.xposed.autodaily.utils.*

object ServiceMsgParseUtil : QQClasses {

    fun parseLoginCode(fromServiceMsg: FromServiceMsg): String? {
        val res = load(StQWebRsp)?.new()
        var wupBuffer = fromServiceMsg.fieldValue("wupBuffer") as ByteArray?
        LogUtil.d("ServiceMsgParseUtil", String(wupBuffer!!))
        wupBuffer = WupUtil.decode(wupBuffer)
        res?.invoke("mergeFrom", wupBuffer)
        val busiBuff = res?.fieldValue("busiBuff")
        LogUtil.d("ServiceMsgParseUtil", "busiBuff -> $busiBuff")
        val byteString = busiBuff?.invoke("get")
        LogUtil.d("ServiceMsgParseUtil", "byteString -> $byteString")
        val bytes = byteString?.invoke("toByteArray")
        LogUtil.d("ServiceMsgParseUtil", "bytes -> $bytes")
        val codeRes = load(StGetCodeRsp)?.new()
        LogUtil.d("ServiceMsgParseUtil", "codeRes -> $codeRes")
        codeRes?.invoke("mergeFrom", bytes)
        val pbCode = codeRes?.fieldValue("code")
        LogUtil.d("ServiceMsgParseUtil", "pbCode -> $pbCode")
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