package me.teble.xposed.autodaily.hook.utils

import com.tencent.common.app.AppInterface
import com.tencent.common.app.BaseApplicationImpl
import com.tencent.qphone.base.remote.ToServiceMsg
import me.teble.xposed.autodaily.config.QQClasses.Companion.MsfServiceSdk
import me.teble.xposed.autodaily.hook.ModuleInitException
import me.teble.xposed.autodaily.hook.base.Initiator.load
import me.teble.xposed.autodaily.utils.field
import me.teble.xposed.autodaily.utils.fieldValueAs
import me.teble.xposed.autodaily.utils.invoke
import mqq.app.AppRuntime
import java.lang.reflect.Field

object QApplicationUtil {
    private const val TAG = "QApplicationUtils"

    val application: BaseApplicationImpl =
        BaseApplicationImpl::class.java.fieldValueAs(BaseApplicationImpl::class.java)
            ?: throw ModuleInitException("get BaseApplicationImpl null")
    private val fAppRuntime: Field = application.field(AppRuntime::class.java)!!
    val appRuntime: AppRuntime
        get() = fAppRuntime.get(application) as AppRuntime
    val appInterface: AppInterface = appRuntime as AppInterface
    val currentUin: Long
        inline get() = appRuntime.longAccountUin
    private val msf: Any?
        get() = load(MsfServiceSdk)?.invoke("get")

    val versionName = VersionUtil.qqVersionName

    fun send(toServiceMsg: ToServiceMsg) {
        msf?.invoke("sendMsg", toServiceMsg)
    }
}