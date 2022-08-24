package me.teble.xposed.autodaily.hook

import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookReplace
import de.robv.android.xposed.XposedBridge
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.hook.annotation.MethodHook
import me.teble.xposed.autodaily.hook.base.BaseHook
import me.teble.xposed.autodaily.hook.base.ProcUtil
import mqq.app.MSFServlet
import mqq.app.MainService
import java.util.concurrent.atomic.AtomicInteger

class MainServiceHook: BaseHook() {

    override val isCompatible: Boolean
        get() = ProcUtil.isMain

    override val enabled: Boolean
        get() = true

    val id: AtomicInteger = AtomicInteger(0)

    override fun init() {
        super.init()
    }

    @MethodHook("receiveMessageFromMSF")
    fun receiveMessageFromMSF() {
        findMethod(MainService::class.java) {
            name == "receiveMessageFromMSF"
        }.hookReplace {
            val msfServlet = it.args[1] as MSFServlet
            if (!msfServlet::class.java.name.startsWith(BuildConfig.APPLICATION_ID)) {
                XposedBridge.invokeOriginalMethod(it.method, it.thisObject, it.args)
            }

            return@hookReplace Unit
        }
    }

    @MethodHook("sendMessageToMSF")
    fun sendMessageToMSF() {
        findMethod(MainService::class.java) {
            name == "sendMessageToMSF"
        }.hookReplace {
            return@hookReplace Unit
        }
    }
}