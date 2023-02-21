@file:JvmName("ResInjectUtil")

package me.teble.xposed.autodaily.hook.proxy.activity

import android.content.res.Resources
import me.teble.xposed.autodaily.R
import me.teble.xposed.autodaily.hook.base.hostContext
import me.teble.xposed.autodaily.hook.base.modulePath
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.invoke

fun injectRes(res: Resources = hostContext.resources) {
    // FIXME: 去除资源注入成功检测，每次重复注入资源，可以修复一些内置 Hook 框架注入虽然成功但是依然找不到资源 ID 的问题
    //        复现：梦境框架、应用转生、LSPatch，QQ 版本 8.3.9、8.4.1
    //    try {
    //        res.getString(R.string.res_inject_success)
    //        return
    //    } catch (ignored: Resources.NotFoundException) {
    //    }
    LogUtil.log("Module path = $modulePath")
    //-----资源注入部分-----
    val assets = res.assets
    assets.invoke("addAssetPath", modulePath)
    try {
        //尝试读取资源注入值
        LogUtil.log("Resources injection result: ${res.getString(R.string.res_inject_success)}")
    } catch (e: Resources.NotFoundException) {
        //执行失败
        LogUtil.e(e, "Resources injection failed!")
    }
}