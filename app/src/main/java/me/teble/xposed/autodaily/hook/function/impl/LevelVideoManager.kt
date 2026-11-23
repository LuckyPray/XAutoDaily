package me.teble.xposed.autodaily.hook.function.impl

import com.tencent.qphone.base.remote.FromServiceMsg
import com.tencent.qphone.base.remote.ToServiceMsg
import me.teble.xposed.autodaily.hook.bridge.MobileQQServiceBridge
import me.teble.xposed.autodaily.hook.function.base.BaseFunction
import me.teble.xposed.autodaily.hook.inject.ServletPool.levelVideoServlet
import me.teble.xposed.autodaily.hook.inject.servlets.LevelVideoServlet
import me.teble.xposed.autodaily.hook.oidb.cmd.oidb_0x9379
import me.teble.xposed.autodaily.hook.utils.OidbUtil
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.appInterface
import me.teble.xposed.autodaily.task.exception.TaskTimeoutException
import me.teble.xposed.autodaily.utils.LogUtil

open class LevelVideoManager : BaseFunction(
    TAG = "LevelVideoManager"
) {

    private lateinit var service: MobileQQServiceBridge

    override fun init() {
        service = MobileQQServiceBridge.resolve(appInterface)
    }

    private fun completeVideo(): Int {
        val reqBody = oidb_0x9379.ReqBody().apply {
            taskType.set(1)
        }
        val toServiceMsg = OidbUtil.makeOIDBPkg(
            "OidbSvcTrpcTcp.0x9379_0", 0x9379, 0, reqBody.toByteArray()
        ).apply {
            appSeq = levelVideoServlet.generateSeq()
            timeout = 10_000L
        }
        service.sendReq(toServiceMsg, LevelVideoServlet::class.java)
        return toServiceMsg.appSeq
    }

    open fun syncCompleteVideo(): Boolean {
        val seq = completeVideo()
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < 10_000L) {
            Thread.sleep(120)
            val fromServiceMsg = levelVideoServlet.getReceiveAndRemove(seq) ?: continue
            val toServiceMsg = fromServiceMsg.attributes[FromServiceMsg::class.java.simpleName] as ToServiceMsg
            service.decodeResp(toServiceMsg, fromServiceMsg)
            val respBody = oidb_0x9379.RspBody()
            val result = OidbUtil.parseOIDBPkg(fromServiceMsg, fromServiceMsg.wupBuffer, respBody)
            return result == 0
        }
        LogUtil.i("完成视频任务获得加速时长: 超时")
        throw TaskTimeoutException("完成视频任务获得加速时长: 超时")
    }
}
