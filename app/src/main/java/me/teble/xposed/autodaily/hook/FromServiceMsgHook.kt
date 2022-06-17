package me.teble.xposed.autodaily.hook

import com.github.kyuubiran.ezxhelper.utils.findAllMethods
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.tencent.qphone.base.remote.FromServiceMsg
import com.tencent.qphone.base.remote.ToServiceMsg
import me.teble.xposed.autodaily.hook.annotation.MethodHook
import me.teble.xposed.autodaily.hook.base.BaseHook
import me.teble.xposed.autodaily.hook.base.ProcUtil
import me.teble.xposed.autodaily.hook.utils.ServiceMsgParseUtil
import me.teble.xposed.autodaily.task.model.VoterInfo
import me.teble.xposed.autodaily.utils.fieldValue
import mqq.app.MainService

class FromServiceMsgHook : BaseHook() {

    override val isCompatible: Boolean
        get() = ProcUtil.isMain

    override val enabled: Boolean
        get() = true

    companion object {
        val resMap = HashMap<String, Any?>(5)
    }

    @MethodHook("服务消息拦截")
    private fun fromServiceMsgHook() {
        findAllMethods(MainService::class.java) { name == "receiveMessageFromMSF" }.hookAfter { param ->
            val toServiceMsg = param.args[0] as? ToServiceMsg ?: return@hookAfter
            if (toServiceMsg.timeout != 9999L) return@hookAfter

            val fromServiceMsg = param.args[1] as FromServiceMsg
            when (fromServiceMsg.serviceCmd) {
                "CertifiedAccountSvc.certified_account_write.SendMenuEvent" -> {
//                            ToastUtil.send("QQ会员公众号签到成功")
//                            logd("QQ会员公众号: 签到成功")
                }
                "LightAppSvc.mini_program_auth.GetCode" -> {
                    val code = ServiceMsgParseUtil.parseLoginCode(fromServiceMsg)
                    val id = "syncGetLoginCode"
                    if (resMap.containsKey(id)) {
                        resMap[id] = code
                    }
                }
                // 点赞
//                        "VisitorSvc.ReqFavorite" -> {
//                            if (app)
//                        }
                // 群签到
//                        "OidbSvc.0xeb7" -> {
//                            logd(fromServiceMsg.toString())
//                            logd(fromServiceMsg.extraData.getExtras().toString())
//                        }
                "LightAppSvc.mini_user_info.GetProfile" -> {
                    val res = ServiceMsgParseUtil.parseProfile(fromServiceMsg)
                    val id = "syncGetProfile"
                    if (resMap.containsKey(id)) {
                        resMap[id] = res
                    }
                }
                "VisitorSvc.ReqGetVoterList" -> {
                    val v = fromServiceMsg.getAttribute("result")
                    val list = v?.fieldValue("vVoterInfos") as List<*>?
                    val res = mutableListOf<VoterInfo>().apply {
                        list?.forEach {
                            val lTime = it?.fieldValue("lTime") as Int
                            val lEctID = it.fieldValue("lEctID") as Long
                            val bTodayVotedCnt = it.fieldValue("bTodayVotedCnt") as Short
                            val bVoteCnt = it.fieldValue("bVoteCnt") as Short
                            val bAvailableCnt = it.fieldValue("bAvailableCnt") as Short
                            add(
                                VoterInfo(
                                    lEctID, lTime, bVoteCnt, bTodayVotedCnt, bAvailableCnt
                                )
                            )
                        }
                    }
                    val id = "syncGetVoterList"
                    if (resMap.containsKey(id)) {
                        resMap[id] = res
                    }
                }
            }
        }
    }
}