package me.teble.xposed.autodaily.hook.servlets

import android.content.Intent
import com.tencent.qphone.base.remote.FromServiceMsg
import com.tencent.qphone.base.remote.ToServiceMsg
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.toMap
import mqq.app.MSFServlet
import mqq.app.Packet

class FavoriteServlet : MSFServlet() {

    override fun onReceive(intent: Intent, fromServiceMsg: FromServiceMsg) {
        val toServiceMsg: ToServiceMsg? =
            intent.getParcelableExtra(ToServiceMsg::class.java.simpleName)
        fromServiceMsg.attributes[FromServiceMsg::class.java.simpleName] = toServiceMsg
        LogUtil.d(toServiceMsg.toString())
        LogUtil.d(toServiceMsg?.extraData.toMap().toString())
        LogUtil.d("FavoriteServlet -> onReceive: $fromServiceMsg")
        LogUtil.d(fromServiceMsg.extraData.toMap().toString())
    }

    override fun onSend(intent: Intent, packet: Packet) {
        val toServiceMsg: ToServiceMsg? =
            intent.getParcelableExtra(ToServiceMsg::class.java.simpleName)
        toServiceMsg?.let {
            packet.setSSOCommand(toServiceMsg.serviceCmd)
            packet.putSendData(toServiceMsg.wupBuffer)
            packet.setTimeout(toServiceMsg.timeout)
            @Suppress("UNCHECKED_CAST")
            packet.attributes = toServiceMsg.attributes as HashMap<String, Any>?
            if (!toServiceMsg.isNeedCallback) {
                packet.setNoResponse()
            }
        }
    }
}