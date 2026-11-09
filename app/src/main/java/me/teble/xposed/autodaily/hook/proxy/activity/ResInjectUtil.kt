@file:JvmName("ResInjectUtil")

package me.teble.xposed.autodaily.hook.proxy.activity

import android.content.res.Resources
import android.content.res.loader.ResourcesLoader
import android.content.res.loader.ResourcesProvider
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.annotation.RequiresApi
import me.teble.xposed.autodaily.R
import me.teble.xposed.autodaily.hook.base.hostContext
import me.teble.xposed.autodaily.hook.base.modulePath
import me.teble.xposed.autodaily.utils.LogUtil
import java.io.File

fun injectRes(res: Resources = hostContext.resources) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        try {
            // ResourcesManager 的资源键会保留加载器，资源实现替换后仍能继续加载模块资源。
            // 自由窗口移动或缩放可能直接替换 ResourcesImpl，而不通知 Activity。
            ModuleResourceLoader.attach(res)
            res.getString(R.string.res_inject_success)
            return
        } catch (e: Exception) {
            // 部分内置 Hook 框架提供的 Resources 不受 ResourcesManager 管理。
            LogUtil.e(e, "ResourcesLoader 注入失败，尝试兼容方式")
        }
    }

    // 仅登记明确注入过模块资源的 Resources，在后续 setImpl 重建主题前补充模块资源，
    // 同时覆盖窗口移动时未通知 Activity 的资源替换情况。
    LegacyResourceHook.install()
    LegacyResourceInjector.inject(res, modulePath)
    try {
        // 尝试读取模块资源，验证注入结果。
        LogUtil.log("Resources injection result: ${res.getString(R.string.res_inject_success)}")
    } catch (e: Resources.NotFoundException) {
        // 记录模块资源读取失败的原因。
        LogUtil.e(e, "Resources injection failed!")
    }
}

@RequiresApi(Build.VERSION_CODES.R)
private object ModuleResourceLoader {
    /**
     * 每个宿主进程复用一个加载器，ResourcesProvider 持有复制后的文件描述符。
     * 保留该提供者，可在宿主存活期间更新模块 APK 后继续访问原有资源。
     */
    private val loader by lazy {
        val provider = ParcelFileDescriptor.open(File(modulePath), ParcelFileDescriptor.MODE_READ_ONLY)
            .use { ResourcesProvider.loadFromApk(it) }
        ResourcesLoader().apply { addProvider(provider) }
    }

    fun attach(resources: Resources) {
        // 同一加载器的 addLoaders 调用具有幂等性，无需为每次回调重新创建加载器。
        resources.addLoaders(loader)
    }
}
