package me.teble.xposed.autodaily.hook

import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.config.PACKAGE_NAME_QQ
import me.teble.xposed.autodaily.hook.base.BaseHook
import me.teble.xposed.autodaily.hook.base.ProcUtil
import me.teble.xposed.autodaily.hook.base.hostPackageName

class BugHook : BaseHook() {

    override val isCompatible: Boolean
        get() = ProcUtil.isMain && hostPackageName == PACKAGE_NAME_QQ
    override val enabled: Boolean
        get() = BuildConfig.DEBUG

}