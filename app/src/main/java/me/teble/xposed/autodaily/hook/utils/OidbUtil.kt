package me.teble.xposed.autodaily.hook.utils

import com.tencent.mobileqq.pb.ByteStringMicro
import com.tencent.qphone.base.remote.ToServiceMsg
import me.teble.xposed.autodaily.config.QQClasses.Companion.OIDBSSOPkg
import me.teble.xposed.autodaily.hook.base.Global.qqVersionName
import me.teble.xposed.autodaily.hook.base.Initiator.load
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.currentUin
import me.teble.xposed.autodaily.utils.fieldValue
import me.teble.xposed.autodaily.utils.invoke
import me.teble.xposed.autodaily.utils.new

object OidbUtil {

    val Boolean.int
        get() = if (this) 1 else 0

    fun makeOIDBPkg(cmd: String, reqBody: Any, isSign: Boolean): ToServiceMsg {
        val pkg = load(OIDBSSOPkg)?.new()
        pkg?.fieldValue("uint32_command")?.invoke("set", 0xEB7)
        pkg?.fieldValue("uint32_service_type")?.invoke("set", isSign.int)
        pkg?.fieldValue("uint32_result")?.invoke("set", 0)
        pkg?.fieldValue("str_client_version")?.invoke("set", "android $qqVersionName")
        val bytes = ByteStringMicro.copyFrom(reqBody.invoke("toByteArray") as ByteArray?)
        pkg?.fieldValue("bytes_bodybuffer")?.invoke("set", bytes)
        val toServiceMsg = ToServiceMsg("mobileqq.service", "$currentUin", cmd)
        toServiceMsg.putWupBuffer(pkg?.invoke("toByteArray") as ByteArray?)
        toServiceMsg.timeout = 30000L
        return toServiceMsg
    }
}