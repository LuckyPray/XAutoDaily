package me.teble.xposed.autodaily.hook

import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookReplace
import de.robv.android.xposed.XposedBridge
import me.teble.xposed.autodaily.hook.annotation.MethodHook
import me.teble.xposed.autodaily.hook.base.BaseHook
import me.teble.xposed.autodaily.hook.base.ProcUtil
import me.teble.xposed.autodaily.hook.base.load
import me.teble.xposed.autodaily.utils.new

class BugHook: BaseHook() {

    override val isCompatible: Boolean
        get() = ProcUtil.isMain
    override val enabled: Boolean
        get() = true


    @MethodHook("fuck No value for gdt_report_list")
    fun fixJsonException() {
        val emptyArr = load("org.json.JSONArray")!!.new()
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