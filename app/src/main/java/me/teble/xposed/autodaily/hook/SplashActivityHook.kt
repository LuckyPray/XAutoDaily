package me.teble.xposed.autodaily.hook

import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import me.teble.xposed.autodaily.config.QQClasses.Companion.SplashActivity
import me.teble.xposed.autodaily.hook.CoreServiceHook.Companion.AUTO_EXEC
import me.teble.xposed.autodaily.hook.CoreServiceHook.Companion.handler
import me.teble.xposed.autodaily.hook.annotation.MethodHook
import me.teble.xposed.autodaily.hook.base.BaseHook
import me.teble.xposed.autodaily.hook.base.Global
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.task.util.ConfigUtil.loadSaveConf
import me.teble.xposed.autodaily.ui.Cache

class SplashActivityHook : BaseHook() {

    override val isCompatible: Boolean
        get() = Global.hostProcessName == ""

    override val enabled: Boolean
        get() = true

    @MethodHook("SplashActivity Hook")
    private fun splashActivityHook() {
        findMethod(SplashActivity) { name == "doOnCreate" }.hookAfter {
            loadSaveConf()
            handler.sendEmptyMessageDelayed(AUTO_EXEC, 10_000)
            if (Cache.needUpdate) {
                ToastUtil.send("插件版本存在更新")
            }
        }
    }
}