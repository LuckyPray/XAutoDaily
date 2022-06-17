package me.teble.xposed.autodaily.hook.function.impl

import android.content.Intent
import me.teble.xposed.autodaily.config.QQClasses.Companion.GetLoginCodeRequest
import me.teble.xposed.autodaily.config.QQClasses.Companion.MiniAppGetLoginCodeServlet
import me.teble.xposed.autodaily.config.QQClasses.Companion.MsfService
import me.teble.xposed.autodaily.hook.FromServiceMsgHook
import me.teble.xposed.autodaily.hook.base.load
import me.teble.xposed.autodaily.hook.function.BaseFunction
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.currentUin
import me.teble.xposed.autodaily.hook.utils.WupUtil
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.invokeAs
import me.teble.xposed.autodaily.utils.new
import mqq.app.Packet

open class MiniLoginManager : BaseFunction(
    TAG = "MiniLoginManager"
) {
    private lateinit var cMiniAppGetLoginCodeServlet: Class<*>
    private lateinit var cGetLoginCodeRequest: Class<*>

    override fun init() {
        cMiniAppGetLoginCodeServlet = load(MiniAppGetLoginCodeServlet)
            ?: throw RuntimeException("类加载失败 -> $MiniAppGetLoginCodeServlet")
        cGetLoginCodeRequest = load(GetLoginCodeRequest)
            ?: throw RuntimeException("类加载失败 -> $GetLoginCodeRequest")
    }

    open fun syncGetLoginCode(miniAppId: String): String? {
        val startTime = System.currentTimeMillis()
        val id = "syncGetLoginCode"
        FromServiceMsgHook.resMap[id] = null
        sendLoginRequest(miniAppId)
        while (System.currentTimeMillis() - startTime < 10_000) {
            Thread.sleep(120)
            val tmp = FromServiceMsgHook.resMap[id] as String?
            tmp?.let {
                FromServiceMsgHook.resMap.remove(id)
                return it
            }
        }
        LogUtil.i("尝试小程序登录，获取js_code超时")
        return null
    }

    private fun sendLoginRequest(miniAppId: String) {
        val packet = Packet::class.java.new("$currentUin")
        val miniAppGetLoginCodeServlet = cMiniAppGetLoginCodeServlet.new()
        val request = cGetLoginCodeRequest.new(miniAppId)
        val traceId: String? = miniAppGetLoginCodeServlet.invokeAs("getTraceId")
        val sendData: ByteArray? = request?.invokeAs("encode", Intent(), -1, traceId)
        packet.let {
            it.setSSOCommand("LightAppSvc.mini_program_auth.GetCode")
            it.putSendData(WupUtil.encode(sendData!!))
            it.setTimeout(9999L)
        }
        val toServiceMsg = packet.toMsg()
        toServiceMsg.let {
            it.appId = 537064392
            it.appSeq = miniAppId.toInt()
            it.serviceName = MsfService
        }
        QApplicationUtil.send(toServiceMsg)
    }
}