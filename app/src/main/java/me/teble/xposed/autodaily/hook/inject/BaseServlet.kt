package me.teble.xposed.autodaily.hook.inject

import android.content.Intent
import android.os.Build
import com.tencent.qphone.base.remote.FromServiceMsg
import com.tencent.qphone.base.remote.ToServiceMsg
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.toMap
import mqq.app.MSFServlet
import mqq.app.Packet
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

abstract class BaseServlet: MSFServlet() {

    protected val seqReceiveMap = ConcurrentHashMap<Int, FromServiceMsg>()
    protected val seqFactory = AtomicInteger(0)

    fun generateSeq(): Int {
        return seqFactory.addAndGet(1)
    }

    fun getReceiveAndRemove(seq: Int): FromServiceMsg? {
        if (seqReceiveMap.containsKey(seq)) {
            val fromServiceMsg = seqReceiveMap[seq]
            seqReceiveMap.remove(seq)
            return fromServiceMsg
        }
        return null
    }

    override fun onReceive(intent: Intent, fromServiceMsg: FromServiceMsg) {
        val toServiceMsg: ToServiceMsg =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(
                    ToServiceMsg::class.java.simpleName,
                    ToServiceMsg::class.java
                )
            } else {
                intent.getParcelableExtra(ToServiceMsg::class.java.simpleName)
            }!!
        fromServiceMsg.attributes[FromServiceMsg::class.java.simpleName] = toServiceMsg
        LogUtil.d(toServiceMsg.toString())
        LogUtil.d(toServiceMsg.extraData.toMap().toString())
        LogUtil.d("${this::class.java.simpleName} -> onReceive: $fromServiceMsg")
        LogUtil.d(fromServiceMsg.extraData.toMap().toString())
        seqReceiveMap[toServiceMsg.appSeq] = fromServiceMsg
    }

    override fun onSend(intent: Intent, packet: Packet) {
        val toServiceMsg: ToServiceMsg? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(
                    ToServiceMsg::class.java.simpleName,
                    ToServiceMsg::class.java
                )
            } else {
                intent.getParcelableExtra(ToServiceMsg::class.java.simpleName)
            }
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