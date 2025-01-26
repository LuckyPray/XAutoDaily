package me.teble.xposed.autodaily.hook.function.impl

import cn.hutool.core.lang.reflect.MethodHandleUtil
import com.github.kyuubiran.ezxhelper.utils.isPublic
import com.github.kyuubiran.ezxhelper.utils.paramCount
import com.tencent.qphone.base.remote.FromServiceMsg
import com.tencent.qphone.base.remote.ToServiceMsg
import me.teble.xposed.autodaily.hook.base.hostVersionName
import me.teble.xposed.autodaily.hook.base.load
import me.teble.xposed.autodaily.hook.function.base.BaseFunction
import me.teble.xposed.autodaily.hook.inject.ServletPool.troopClockInServlet
import me.teble.xposed.autodaily.hook.inject.servlets.TroopClockInServlet
import me.teble.xposed.autodaily.hook.oidb.cmd.oidb_0xeb7
import me.teble.xposed.autodaily.hook.utils.OidbUtil
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.appInterface
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.currentUin
import me.teble.xposed.autodaily.task.exception.TaskTimeoutException
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.getFields
import me.teble.xposed.autodaily.utils.getMethods
import java.lang.reflect.Method

open class GroupSignInManager : BaseFunction(
    TAG = "GroupSignInManager"
) {

    lateinit var mRealHandlerReq: Method
    lateinit var mHandlerResponse: Method
    lateinit var mqqService: Any
    override fun init() {
        val cBaseService = load("com.tencent.mobileqq.service.MobileQQServiceBase")!!
        cBaseService.getMethods(false).forEach {
            if (it.returnType == Void.TYPE && it.isPublic) {
                val paramTypes = it.parameterTypes
                if (paramTypes.size >= 2
                    && paramTypes.first() == ToServiceMsg::class.java
                    && paramTypes.last() == Class::class.java) {
                    mRealHandlerReq = it
                    LogUtil.d(it.toString())
                }
            }
        }
        cBaseService.getMethods(false).forEach {
            if (it.returnType == Void.TYPE && it.isPublic && it.paramCount == 4) {
                val paramTypes = it.parameterTypes
                if (paramTypes[0] == Boolean::class.java
                    && paramTypes[1] == ToServiceMsg::class.java
                    && paramTypes[2] == FromServiceMsg::class.java) {
                    mHandlerResponse = it
                    LogUtil.d(it.toString())
                }
            }
        }
        appInterface.getFields(false).forEach {
            if (cBaseService.isAssignableFrom(it.type)) {
                LogUtil.d(it.name)
                it.isAccessible = true
                mqqService = it.get(appInterface)!!
            }
        }
        if (!this::mRealHandlerReq.isInitialized) {
            throw RuntimeException("初始化失败 -> mRealHandlerReq")
        }
        if (!this::mHandlerResponse.isInitialized) {
            throw RuntimeException("初始化失败 -> mHandlerResponse")
        }
        if (!this::mqqService.isInitialized) {
            throw RuntimeException("初始化失败 -> mqqService")
        }
    }

    private fun sendReq(toServiceMsg: ToServiceMsg) {
        // qq <= 8.9.8
        toServiceMsg.extraData.putBoolean("req_pb_protocol_flag", true)
        // qq >= 8.9.10
        toServiceMsg.attributes["req_pb_protocol_flag"] = true
        if (mRealHandlerReq.parameterTypes.size == 2) {
            MethodHandleUtil.invokeSpecial<Unit>(mqqService, mRealHandlerReq, toServiceMsg, TroopClockInServlet::class.java)
        } else {
            MethodHandleUtil.invokeSpecial<Unit>(mqqService, mRealHandlerReq, toServiceMsg, null, TroopClockInServlet::class.java)
        }
    }

    private fun decodeResponse(toServiceMsg: ToServiceMsg, fromServiceMsg: FromServiceMsg) {
        MethodHandleUtil.invokeSpecial<Unit>(mqqService, mHandlerResponse, fromServiceMsg.isSuccess, toServiceMsg, fromServiceMsg, null)
    }

    private fun signIn(groupUin: String): Int {
        val reqBody = oidb_0xeb7.ReqBody()
        val signWriteReq = oidb_0xeb7.StSignInWriteReq()
        signWriteReq.groupId.set(groupUin)
        signWriteReq.uid.set("$currentUin")
        signWriteReq.clientVersion.set(hostVersionName)
        reqBody.signInWriteReq.set(signWriteReq)

        val toServiceMsg = OidbUtil.makeOIDBPkg(
            "OidbSvc.0xeb7", 0xeb7, 1, reqBody.toByteArray()).apply {
            appSeq = troopClockInServlet.generateSeq()
            extraData.putString("troopUin", groupUin)
            extraData.putString("memberUin", "$currentUin")
            extraData.putInt("signInScene", 0)
            extraData.putBoolean("isWrite", true)
        }
        sendReq(toServiceMsg)
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
            decodeResponse(toServiceMsg, fromServiceMsg)
            val respBody = oidb_0xeb7.RspBody()
            val parseOIDBPkg = OidbUtil.parseOIDBPkg(fromServiceMsg, fromServiceMsg.wupBuffer, respBody)
            respBody.signInWriteRsp.ret.code
            return parseOIDBPkg == 0
        }
        LogUtil.i("执行群签到 $groupUin 超时")
        throw TaskTimeoutException("执行群签到 $groupUin 超时")
    }
}