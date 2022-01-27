package me.teble.xposed.autodaily.hook.function.impl

import com.tencent.mobileqq.mp.mobileqq_mp
import me.teble.xposed.autodaily.config.QQClasses.Companion.StQWebReq
import me.teble.xposed.autodaily.hook.base.Initiator
import me.teble.xposed.autodaily.hook.function.BaseFunction
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.currentUin
import me.teble.xposed.autodaily.hook.utils.ServletUtil
import me.teble.xposed.autodaily.hook.utils.VersionUtil
import me.teble.xposed.autodaily.hook.utils.WupUtil
import me.teble.xposed.autodaily.utils.new
import mqq.app.Packet

open class PublicAccountManager : BaseFunction(
    TAG = "PublicAccountManager"
) {
    override fun init() {
        Initiator.load(StQWebReq)
            ?: throw RuntimeException("类加载失败 -> ${StQWebReq}")
        Initiator.load("cooperation.qzone.PlatformInfor")
            ?: throw RuntimeException("类加载失败 -> PlatformInfor")
    }

    open fun vipPublicAccountSignIn() {
        val request = mobileqq_mp.SendMenuEventRequest().apply {
            uin.set(2659464438.toInt())
            type.set(1)
            key.set("VIP_PUB_SUBBUTTON_QIANDAO")
            msg_id.set(0L)
            s_type.set(1)
            versionInfo.set(VersionUtil.qqVersionInfo)
            menu_type.set(0)
        }
        val sendData = ServletUtil.encodeReq(-1, request.toByteArray())
        val packet = Packet::class.java.new(currentUin.toString()).apply {
            setSSOCommand("CertifiedAccountSvc.certified_account_write.SendMenuEvent")
            putSendData(WupUtil.encode(sendData))
            setTimeout(9999L)
        }
        val toServiceMsg = packet.toMsg().apply {
            appId = 537064392
            appSeq = 23333333
            serviceName = "com.tencent.mobileqq.msf.service.MsfService"
        }
        QApplicationUtil.send(toServiceMsg)
    }
}