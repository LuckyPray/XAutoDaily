package me.teble.xposed.autodaily.hook

import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookReplace
import com.tencent.qphone.base.remote.ToServiceMsg
import de.robv.android.xposed.XposedBridge
import me.teble.xposed.autodaily.hook.annotation.MethodHook
import me.teble.xposed.autodaily.hook.base.BaseHook
import me.teble.xposed.autodaily.hook.base.ProcUtil

class ToServiceMsgHook : BaseHook() {

    override val isCompatible: Boolean
        get() = ProcUtil.isMain

    override val enabled: Boolean
        get() = true

    @MethodHook("客户端消息拦截")
    private fun toServiceMsgHook() {
        findMethod(ToServiceMsg::class.java) { name == "setTimeout" && parameterTypes[0] == Long::class.java }.hookReplace { param ->
            if ((param.thisObject as ToServiceMsg).timeout != 9999L) {
                XposedBridge.invokeOriginalMethod(
                    param.method,
                    param.thisObject,
                    param.args
                )
            }
        }
    }
}