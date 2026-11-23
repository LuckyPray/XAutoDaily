package me.teble.xposed.autodaily.hook.function.impl

import com.tencent.qphone.base.remote.FromServiceMsg
import com.tencent.qphone.base.remote.ToServiceMsg
import me.teble.xposed.autodaily.hook.bridge.MobileQQServiceBridge
import me.teble.xposed.autodaily.hook.function.base.BaseFunction
import me.teble.xposed.autodaily.hook.inject.ServletPool.novelReadServlet
import me.teble.xposed.autodaily.hook.inject.servlets.NovelReadServlet
import me.teble.xposed.autodaily.hook.oidb.cmd.oidb_0x9de7
import me.teble.xposed.autodaily.hook.utils.OidbUtil
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.appInterface
import me.teble.xposed.autodaily.task.exception.TaskTimeoutException
import me.teble.xposed.autodaily.utils.LogUtil

open class NovelReadManager : BaseFunction(
    TAG = "NovelReadManager"
) {

    private lateinit var service: MobileQQServiceBridge

    override fun init() {
        service = MobileQQServiceBridge.resolve(appInterface)
    }

    private fun completeRead(): Int {
        val reqBody = oidb_0x9de7.ReqBody().apply {
            version.set(0)
            type.set(0)
            json.set("{}")
        }
        val toServiceMsg = OidbUtil.createToServiceMsg("MQUpdateSvc_com_qq_vip.web.OidbSvcTrpcJsapiTcp.0x9de7_0").apply {
            putWupBuffer(reqBody.toByteArray())
            appSeq = novelReadServlet.generateSeq()
            timeout = 10_000L
        }
        service.sendReq(toServiceMsg, NovelReadServlet::class.java)
        return toServiceMsg.appSeq
    }

    open fun syncCompleteRead(): Boolean {
        val seq = completeRead()
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < 10_000L) {
            Thread.sleep(120)
            val fromServiceMsg = novelReadServlet.getReceiveAndRemove(seq) ?: continue
            val toServiceMsg = fromServiceMsg.attributes[FromServiceMsg::class.java.simpleName] as ToServiceMsg
            service.decodeResp(toServiceMsg, fromServiceMsg)
            val respBody = oidb_0x9de7.RspBody().apply { mergeFrom(fromServiceMsg.wupBuffer) }
            val ret = respBody.ret.get()
            return ret == 0
        }
        LogUtil.i("去看免费小说: 超时")
        throw TaskTimeoutException("去看免费小说: 超时")
    }
}
