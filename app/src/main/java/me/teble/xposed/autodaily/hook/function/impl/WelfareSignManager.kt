package me.teble.xposed.autodaily.hook.function.impl

import com.tencent.mobileqq.pb.MessageMicro
import com.tencent.qphone.base.remote.FromServiceMsg
import com.tencent.qphone.base.remote.ToServiceMsg
import me.teble.xposed.autodaily.hook.bridge.MobileQQServiceBridge
import me.teble.xposed.autodaily.hook.function.base.BaseFunction
import me.teble.xposed.autodaily.hook.inject.ServletPool.welfareSignServlet
import me.teble.xposed.autodaily.hook.inject.servlets.WelfareSignServlet
import me.teble.xposed.autodaily.hook.oidb.cmd.oidb_0x9e99
import me.teble.xposed.autodaily.hook.oidb.cmd.oidb_0x9e9a
import me.teble.xposed.autodaily.hook.utils.OidbUtil
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.appInterface
import me.teble.xposed.autodaily.task.exception.TaskTimeoutException
import me.teble.xposed.autodaily.utils.LogUtil

open class WelfareSignManager : BaseFunction(
    TAG = "WelfareSignManager"
) {

    private lateinit var service: MobileQQServiceBridge

    override fun init() {
        service = MobileQQServiceBridge.resolve(appInterface)
    }

    private fun request(command: Int, body: ByteArray): Int {
        val toServiceMsg = OidbUtil.makeOIDBPkg(
            "OidbSvcTrpcTcp.0x${command.toString(16)}_0", command, 0, body
        ).apply {
            appSeq = welfareSignServlet.generateSeq()
            timeout = 10_000L
        }
        service.sendReq(toServiceMsg, WelfareSignServlet::class.java)
        return toServiceMsg.appSeq
    }

    private fun syncRequest(command: Int, body: ByteArray, respBody: MessageMicro<*>): Boolean {
        val seq = request(command, body)
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < 10_000L) {
            Thread.sleep(120)
            val fromServiceMsg = welfareSignServlet.getReceiveAndRemove(seq) ?: continue
            val toServiceMsg = fromServiceMsg.attributes[FromServiceMsg::class.java.simpleName] as ToServiceMsg
            service.decodeResp(toServiceMsg, fromServiceMsg)
            val result = OidbUtil.parseOIDBPkg(fromServiceMsg, fromServiceMsg.wupBuffer, respBody)
            return result == 0
        }
        LogUtil.i("QQ会员连续签到奖励: 超时")
        throw TaskTimeoutException("QQ会员连续签到奖励: 超时")
    }

    open fun syncSignIn() {
        val respBody1 = oidb_0x9e99.RspBody()
        syncRequest(0x9e99, oidb_0x9e99.ReqBody().apply {
            scene.set(1)
        }.toByteArray(), respBody1)
        when (val status = respBody1.status.get()) {
            0 -> Unit
            1 -> {
                LogUtil.i("QQ会员连续签到奖励: 已签到")
                return
            }

            2 -> throw RuntimeException("QQ会员连续签到奖励: 已断签, 请手动处理")
            else -> throw RuntimeException("QQ会员连续签到奖励: 未知状态-$status")
        }

        val respBody2 = oidb_0x9e9a.RspBody()
        syncRequest(0x9e9a, oidb_0x9e9a.ReqBody().apply {
            scene.set(1)
            action.set(0)
        }.toByteArray(), respBody2)
        when (val result = respBody2.result.get()) {
            0 -> LogUtil.i("QQ会员连续签到奖励: 签到成功")
            1 -> LogUtil.i("QQ会员连续签到奖励: 已签到")
            2 -> throw RuntimeException("QQ会员连续签到奖励: 已断签, 请手动处理")
            else -> throw RuntimeException("QQ会员连续签到奖励: 未知结果-$result")
        }
    }
}
