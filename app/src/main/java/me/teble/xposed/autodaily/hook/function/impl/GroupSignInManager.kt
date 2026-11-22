package me.teble.xposed.autodaily.hook.function.impl

import com.tencent.qphone.base.remote.FromServiceMsg
import com.tencent.qphone.base.remote.ToServiceMsg
import me.teble.xposed.autodaily.hook.base.hostVersionName
import me.teble.xposed.autodaily.hook.bridge.MobileQQServiceBridge
import me.teble.xposed.autodaily.hook.function.base.BaseFunction
import me.teble.xposed.autodaily.hook.inject.ServletPool.troopClockInServlet
import me.teble.xposed.autodaily.hook.inject.servlets.TroopClockInServlet
import me.teble.xposed.autodaily.hook.oidb.cmd.oidb_0xeb7
import me.teble.xposed.autodaily.hook.utils.OidbUtil
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.appInterface
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.currentUin
import me.teble.xposed.autodaily.task.exception.TaskTimeoutException
import me.teble.xposed.autodaily.utils.LogUtil

open class GroupSignInManager : BaseFunction(
    TAG = "GroupSignInManager"
) {

    private lateinit var service: MobileQQServiceBridge

    override fun init() {
        service = MobileQQServiceBridge.resolve(appInterface)
    }

    private fun signIn(groupUin: String): Int {
        val reqBody = oidb_0xeb7.ReqBody().apply {
            signInWriteReq.set(oidb_0xeb7.StSignInWriteReq().apply {
                groupId.set(groupUin)
                uid.set("$currentUin")
                clientVersion.set(hostVersionName)
            })
        }
        val toServiceMsg = OidbUtil.makeOIDBPkg(
            "OidbSvc.0xeb7", 0xeb7, 1, reqBody.toByteArray()
        ).apply {
            appSeq = troopClockInServlet.generateSeq()
            extraData.putString("troopUin", groupUin)
            extraData.putString("memberUin", "$currentUin")
            extraData.putInt("signInScene", 0)
            extraData.putBoolean("isWrite", true)
        }
        service.sendReq(toServiceMsg, TroopClockInServlet::class.java)
        return toServiceMsg.appSeq
    }

    open fun syncSignIn(groupUin: String): Boolean {
        LogUtil.i("group signIn -> $groupUin")
        val seq = signIn(groupUin)
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < 30_000) {
            Thread.sleep(120)
            val fromServiceMsg = troopClockInServlet.getReceiveAndRemove(seq) ?: continue
            val toServiceMsg = fromServiceMsg.attributes[FromServiceMsg::class.java.simpleName] as ToServiceMsg
            service.decodeResp(toServiceMsg, fromServiceMsg)
            val respBody = oidb_0xeb7.RspBody()
            val parseOIDBPkg = OidbUtil.parseOIDBPkg(fromServiceMsg, fromServiceMsg.wupBuffer, respBody)
            respBody.signInWriteRsp.ret.code
            return parseOIDBPkg == 0
        }
        LogUtil.i("执行群签到 $groupUin 超时")
        throw TaskTimeoutException("执行群签到 $groupUin 超时")
    }
}
