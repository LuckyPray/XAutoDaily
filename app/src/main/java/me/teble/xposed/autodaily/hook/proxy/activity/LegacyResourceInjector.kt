package me.teble.xposed.autodaily.hook.proxy.activity

import android.content.res.AssetManager
import android.content.res.Resources
import java.util.WeakHashMap

/**
 * 修复旧版资源注入路径，不依赖 Xposed，便于在设备上单独验证资源替换过程。
 */
internal object LegacyResourceInjector {
    private val injectedResources = WeakHashMap<Resources, String>()
    private val addAssetPath by lazy {
        AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java)
            .apply { isAccessible = true }
    }
    private val getAssets by lazy {
        Class.forName("android.content.res.ResourcesImpl").getDeclaredMethod("getAssets")
            .apply { isAccessible = true }
    }

    fun inject(resources: Resources, apkPath: String) {
        require(apkPath.isNotBlank())
        synchronized(injectedResources) { injectedResources[resources] = apkPath }
        addModuleAssets(resources.assets, apkPath)
    }

    /**
     * 在 setImpl 重建主题前，为即将替换的资源实现补充模块资源。
     *
     * @param resources 已登记模块资源注入的资源对象
     * @param implementation 即将替换到资源对象中的实现
     * @return 是否已为该资源对象补充模块资源
     */
    fun beforeSetImpl(resources: Resources, implementation: Any): Boolean {
        val apkPath = synchronized(injectedResources) { injectedResources[resources] } ?: return false
        val assets = getAssets.invoke(implementation) as AssetManager
        addModuleAssets(assets, apkPath)
        return true
    }

    private fun addModuleAssets(assets: AssetManager, apkPath: String) {
        val cookie = addAssetPath.invoke(assets, apkPath) as Int
        check(cookie != 0) { "模块资源 APK 加载失败: $apkPath" }
    }
}
