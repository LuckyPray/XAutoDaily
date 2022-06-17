package me.teble.xposed.autodaily.hook

import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import me.teble.xposed.autodaily.activity.common.MainActivity

object ModuleHook {

    fun hookSelf() {
        findMethod(MainActivity::class.java) { name == "isEnabled" }.hookAfter {
            it.result = true
        }
    }


}