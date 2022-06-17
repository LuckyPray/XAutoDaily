package me.teble.xposed.autodaily.hook.proxy.activity

import android.content.res.Resources
import me.teble.xposed.autodaily.R
import me.teble.xposed.autodaily.hook.base.hostContext
import me.teble.xposed.autodaily.hook.base.modulePath
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.invoke

object ResInjectUtil {

    fun injectRes(res: Resources = hostContext.resources) {
        try {
            res.getString(R.string.res_inject_success)
            return
        } catch (ignored: Resources.NotFoundException) {
        }
        LogUtil.log("Module path = $modulePath")
        //-----资源注入部分-----
        val assets = res.assets
        assets.invoke(
            "addAssetPath",
            modulePath
        )
        try {
            //尝试读取资源注入值
            LogUtil.log("Resources injection result: ${res.getString(R.string.res_inject_success)}")
        } catch (e: Resources.NotFoundException) {
            //执行失败
            LogUtil.e(e, "Resources injection failed!")
        }
    }
}