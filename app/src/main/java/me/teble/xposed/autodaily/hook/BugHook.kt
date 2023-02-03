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
import me.teble.xposed.autodaily.utils.printAllField
import me.teble.xposed.autodaily.utils.toMap
import mqq.app.MSFServlet
import mqq.app.MainService
import mqq.app.NewIntent

class BugHook : BaseHook() {

    override val isCompatible: Boolean
        get() = ProcUtil.isMain && hostPackageName == PACKAGE_NAME_QQ
    override val enabled: Boolean
        get() = BuildConfig.DEBUG

}