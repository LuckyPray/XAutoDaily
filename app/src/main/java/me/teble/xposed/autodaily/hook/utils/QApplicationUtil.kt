package me.teble.xposed.autodaily.hook.utils

import com.tencent.common.app.AppInterface
import com.tencent.common.app.BaseApplicationImpl
import com.tencent.mobileqq.app.QQAppInterface
import com.tencent.qphone.base.remote.ToServiceMsg
import me.teble.xposed.autodaily.config.MsfServiceSdk
import me.teble.xposed.autodaily.hook.base.ProcUtil
import me.teble.xposed.autodaily.hook.base.hostApp
import me.teble.xposed.autodaily.hook.base.load
import me.teble.xposed.autodaily.utils.field
import me.teble.xposed.autodaily.utils.invoke
import mqq.app.AppRuntime
import java.lang.reflect.Field

object QApplicationUtil {

    val application = hostApp as BaseApplicationImpl
    private val fAppRuntime: Field by lazy { application.field(AppRuntime::class.java)!! }
    val appRuntime: AppRuntime get() = application.waitAppRuntime(null)

    val appInterface: AppInterface = if (ProcUtil.isMain) {
        // only main process has QQAppInterface, other process is PluginRuntime or maybe something else
        appRuntime as QQAppInterface
    } else {
        error("appInterface is not available in ${ProcUtil.procName}}")
    }

    val currentUin: Long inline get() = appRuntime.longAccountUin
    private val msf: Any get() = load(MsfServiceSdk)!!.invoke("get")!!

    fun send(toServiceMsg: ToServiceMsg) {
        msf.invoke("sendMsg", toServiceMsg)
    }
}