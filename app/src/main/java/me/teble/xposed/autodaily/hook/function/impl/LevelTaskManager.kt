package me.teble.xposed.autodaily.hook.function.impl

import com.tencent.mobileqq.pb.MessageMicro
import com.tencent.qphone.base.remote.FromServiceMsg
import com.tencent.qphone.base.remote.ToServiceMsg
import me.teble.xposed.autodaily.hook.bridge.MobileQQServiceBridge
import me.teble.xposed.autodaily.hook.function.base.BaseFunction
import me.teble.xposed.autodaily.hook.inject.ServletPool.levelTaskServlet
import me.teble.xposed.autodaily.hook.inject.servlets.LevelTaskServlet
import me.teble.xposed.autodaily.hook.oidb.cmd.oidb_0x9379
import me.teble.xposed.autodaily.hook.oidb.cmd.oidb_0x9d3c
import me.teble.xposed.autodaily.hook.oidb.cmd.oidb_0x9de7
import me.teble.xposed.autodaily.hook.oidb.cmd.oidb_0x9e99
import me.teble.xposed.autodaily.hook.oidb.cmd.oidb_0x9e9a
import me.teble.xposed.autodaily.hook.utils.OidbUtil
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.appInterface
import me.teble.xposed.autodaily.task.exception.TaskTimeoutException
import me.teble.xposed.autodaily.utils.LogUtil
import tencent.im.oidb.oidb_sso.OIDBSSOPkg

open class LevelTaskManager : BaseFunction(
    TAG = "LevelTaskManager"
) {

    private lateinit var service: MobileQQServiceBridge

    override fun init() {
        service = MobileQQServiceBridge.resolve(appInterface)
    }

    private fun syncRequest(toServiceMsg: ToServiceMsg, taskName: String): FromServiceMsg {
        toServiceMsg.appSeq = levelTaskServlet.generateSeq()
        toServiceMsg.timeout = 10_000L
        service.sendReq(toServiceMsg, LevelTaskServlet::class.java)
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < 10_000L) {
            Thread.sleep(120)
            val fromServiceMsg = levelTaskServlet.getReceiveAndRemove(toServiceMsg.appSeq) ?: continue
            service.decodeResp(toServiceMsg, fromServiceMsg)
            return fromServiceMsg
        }
        LogUtil.i("$taskName: 超时")
        throw TaskTimeoutException("$taskName: 超时")
    }

    private fun syncOidbRequest(
        cmd: Int,
        reqBody: ByteArray,
        respBody: MessageMicro<*>,
        taskName: String,
    ): Boolean {
        val toServiceMsg = OidbUtil.makeOIDBPkg(
            "OidbSvcTrpcTcp.0x${cmd.toString(16)}_0",
            cmd, 0, reqBody
        )
        val fromServiceMsg = syncRequest(toServiceMsg, taskName)
        val envelope = OIDBSSOPkg()
        val result = OidbUtil.parseOIDBPkg(fromServiceMsg, fromServiceMsg.wupBuffer, envelope, respBody)
        return result == 0
    }

    // 2026年07月20日0点起 完成视频任务获得加速时长
    open fun syncCompleteVideo0720(): Boolean {
        return syncOidbRequest(
            0x9379,
            oidb_0x9379.ReqBody().apply {
                taskType.set(1)
            }.toByteArray(),
            oidb_0x9379.RspBody(),
            "完成视频任务获得加速时长"
        )
    }

    // 2026年07月30日0点起 看10秒漫剧
    open fun syncCompleteVideo0730(): Boolean {
        return syncOidbRequest(
            0x9d3c,
            byteArrayOf(),
            oidb_0x9d3c.RspBody(),
            "看10秒漫剧"
        )
    }

    // 2026年09月16日0点起 去看免费小说
    open fun syncCompleteRead0916(): Boolean {
        val toServiceMsg = OidbUtil.createToServiceMsg("MQUpdateSvc_com_qq_vip.web.OidbSvcTrpcJsapiTcp.0x9de7_0").apply {
            putWupBuffer(oidb_0x9de7.ReqBody().apply {
                version.set(0)
                type.set(0)
                json.set("{}")
            }.toByteArray())
        }
        val fromServiceMsg = syncRequest(toServiceMsg, "去看免费小说")
        val respBody = oidb_0x9de7.RspBody().apply { mergeFrom(fromServiceMsg.wupBuffer) }
        return respBody.ret.get() == 0
    }

    // 2026年11月17日12点起 QQ会员连续签到奖励
    open fun syncSignIn1117() {
        val respBody1 = oidb_0x9e99.RspBody()
        syncOidbRequest(0x9e99, oidb_0x9e99.ReqBody().apply {
            scene.set(1)
        }.toByteArray(), respBody1, "QQ会员连续签到奖励")
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
        syncOidbRequest(0x9e9a, oidb_0x9e9a.ReqBody().apply {
            scene.set(1)
            action.set(0)
        }.toByteArray(), respBody2, "QQ会员连续签到奖励")
        when (val result = respBody2.result.get()) {
            0 -> LogUtil.i("QQ会员连续签到奖励: 签到成功")
            1 -> LogUtil.i("QQ会员连续签到奖励: 已签到")
            2 -> throw RuntimeException("QQ会员连续签到奖励: 已断签, 请手动处理")
            else -> throw RuntimeException("QQ会员连续签到奖励: 未知结果-$result")
        }
    }
}
