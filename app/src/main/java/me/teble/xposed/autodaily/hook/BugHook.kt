package me.teble.xposed.autodaily.hook

import android.os.Build
import androidx.annotation.RequiresApi
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.hookReplace
import com.tencent.qphone.base.remote.ToServiceMsg
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.config.PACKAGE_NAME_QQ
import me.teble.xposed.autodaily.hook.annotation.MethodHook
import me.teble.xposed.autodaily.hook.base.BaseHook
import me.teble.xposed.autodaily.hook.base.ProcUtil
import me.teble.xposed.autodaily.hook.base.hostPackageName
import me.teble.xposed.autodaily.hook.base.load
import me.teble.xposed.autodaily.hook.inject.servlets.FavoriteServlet
import me.teble.xposed.autodaily.hook.inject.servlets.TroopClockInServlet
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.new
import me.teble.xposed.autodaily.utils.toMap
import mqq.app.MSFServlet
import mqq.app.MainService
import mqq.app.NewIntent

class BugHook : BaseHook() {

    override val isCompatible: Boolean
        get() = ProcUtil.isMain && hostPackageName == PACKAGE_NAME_QQ
    override val enabled: Boolean
        get() = BuildConfig.DEBUG


    @MethodHook("fuck No value for gdt_report_list")
    fun fixJsonException() {
        val emptyArr = load("org.json.JSONArray")!!.new()
        runCatching {
            findMethod("com.tencent.mobileqq.qwallet.config.impl.QWalletConfigServiceImpl") {
                name == "getArray"
            }.hookReplace {
                if (it.args.size == 2 && it.args[0] == "common") {
                    val arr = it.args[1] as Array<*>
                    if (arr.size == 2 && arr[0] == "pub_ad_control" && arr[1] == "gdt_report_list") {
                        return@hookReplace emptyArr
                    }
                }
                XposedBridge.invokeOriginalMethod(it.method, it.thisObject, it.args)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @MethodHook("测试")
    fun test() {
        kotlin.runCatching {
            findMethod(MainService::class.java) {
                name == "sendMessageToMSF"
            }.hookAfter {
                val toServiceMsg = it.args[0] as? ToServiceMsg ?: return@hookAfter
                val servlet = it.args[1] as? MSFServlet ?: return@hookAfter
                if (toServiceMsg.serviceCmd.startsWith("OidbSvc.0xeb7")) {
                    LogUtil.d("sendMessageToMSF: " + servlet::class.java.name)
                    LogUtil.d(toServiceMsg.appSeq.toString())
                    LogUtil.d(toServiceMsg.serviceCmd)
                    LogUtil.d(toServiceMsg.toString())
                    LogUtil.d("buf size: ${toServiceMsg.wupBuffer.size}")
                    LogUtil.d(toServiceMsg.extraData.toMap().toString())
                    LogUtil.printStackTrace()
                }
            }
        }.onFailure { LogUtil.e(it) }

        kotlin.runCatching {
            findMethod("mqq.app.AppRuntime") {
                name == "startServlet"
            }.hookAfter {
                val newIntent = it.args[0] as NewIntent
                if (newIntent.component?.className == "com.tencent.mobileqq.x.a" ||
                    newIntent.component?.className == FavoriteServlet::class.java.name ||
                    newIntent.component?.className == TroopClockInServlet::class.java.name) {
                    val toServiceMsg = newIntent.getParcelableExtra<ToServiceMsg>("ToServiceMsg")
                    toServiceMsg?.let {
                        if (it.serviceCmd.startsWith("OidbSvc.0xeb7")) {
                            LogUtil.d("startServlet: " + it.serviceCmd)
                            LogUtil.d(it.toString())
                            LogUtil.d(it.wupBuffer.size.toString())
                            LogUtil.d(it.extraData.toMap().toString())
                            LogUtil.printStackTrace()
                        }
                    }
                }
            }
        }

        val cls = load("Lcom/tencent/mobileqq/troop/clockin/handler/TroopClockInHandler;")!!
        XposedBridge.hookAllConstructors(cls, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                LogUtil.d("TroopClockInHandler -> init")
                LogUtil.printStackTrace()
            }
        })
//
//        findMethod(load("Lcom/tencent/mobileqq/troop/d/b/a;")!!) {
//            name == "W4"
//        }.hookAfter {
//            LogUtil.d("onResp: " + it.args.last().toString())
//            val toServiceMsg = it.args[0] as ToServiceMsg
//            val req = oidb_0xeb7.ReqBody().mergeFrom(toServiceMsg.wupBuffer)
//            LogUtil.d("""
//                ${req.signInStatusReq}
//                ${req.signInStatusReq.uid.get()}
//                ${req.signInStatusReq.groupId.get()}
//                ${req.signInStatusReq.scene.get()}
//                ${req.signInStatusReq.clientVersion.get()}
//                -------------
//                ${req.signInWriteReq}
//                ${req.signInWriteReq.uid.get()}
//                ${req.signInWriteReq.groupId.get()}
//                ${req.signInWriteReq.clientVersion.get()}
//            """.trimIndent())
//            LogUtil.printStackTrace()
//        }

//        findMethod(load("Lcom/tencent/mobileqq/troop/d/b/a;")!!) {
//            name == "sendPbReq"
//        }.hookAfter {
//        }

        XposedBridge.hookAllMethods(load("Lcom/tencent/mobileqq/troop/handler/c;"),
        "sendPbReq", object: XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val toServiceMsg = param.args[0] as ToServiceMsg
                if (toServiceMsg.serviceCmd == "OidbSvc.0xeb7") {
                    LogUtil.d("cmd: ${toServiceMsg.serviceCmd}, sendPbReq: " + toServiceMsg.wupBuffer.size)
                    LogUtil.printStackTrace()
                }
            }
        })
    }
}