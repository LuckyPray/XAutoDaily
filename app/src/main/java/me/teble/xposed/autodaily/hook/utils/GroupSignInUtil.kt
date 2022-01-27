package me.teble.xposed.autodaily.hook.utils

import me.teble.xposed.autodaily.config.QQClasses.Companion.ReqBody
import me.teble.xposed.autodaily.config.QQClasses.Companion.StSignInWriteReq
import me.teble.xposed.autodaily.hook.base.Global.qqVersionName
import me.teble.xposed.autodaily.hook.base.Initiator.load
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.currentUin
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.fieldValue
import me.teble.xposed.autodaily.utils.invoke
import me.teble.xposed.autodaily.utils.new

object GroupSignInUtil {

    private val TAG = "GroupSignInUtil"

    fun signIn(groupUin: String) {
        val reqBody = load(ReqBody)!!.new()
        val writeReq = load(StSignInWriteReq)!!.new()
        writeReq.fieldValue("groupId")?.invoke("set", groupUin)
        writeReq.fieldValue("uid")?.invoke("set", "$currentUin")
        writeReq.fieldValue("clientVersion")?.invoke("set", qqVersionName)
        reqBody.fieldValue("signInWriteReq")?.invoke("set", writeReq)
        val toServiceMsg = OidbUtil.makeOIDBPkg("OidbSvc.0xeb7", reqBody, true)
//        toServiceMsg.appSeq = 2333333
        toServiceMsg.extraData.putString("troopUin", groupUin)
        toServiceMsg.extraData.putString("memberUin", "$currentUin")
        toServiceMsg.extraData.putInt("signInScene", 0)
        toServiceMsg.extraData.putBoolean("isWrite", true)
        LogUtil.d(TAG, toServiceMsg.toString())
        QApplicationUtil.appInterface.sendToService(toServiceMsg)
    }
}