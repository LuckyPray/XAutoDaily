package me.teble.xposed.autodaily.hook.proxy.activity

import android.content.res.Resources
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import me.teble.xposed.autodaily.utils.LogUtil

internal object LegacyResourceHook {
    private var attempted = false

    @Synchronized
    fun install() {
        if (attempted) return
        attempted = true
        runCatching {
            val setImpl = Resources::class.java.declaredMethods.first {
                it.name == "setImpl" && it.parameterTypes.size == 1 &&
                    it.parameterTypes[0].name == "android.content.res.ResourcesImpl"
            }
            setImpl.hookBefore { param ->
                val resources = param.thisObject as Resources
                val implementation = param.args[0] ?: return@hookBefore
                runCatching { LegacyResourceInjector.beforeSetImpl(resources, implementation) }
                    .onFailure { LogUtil.e(it, "恢复替换后的模块资源失败") }
            }
            LogUtil.i("已启用旧版资源替换修复")
        }.onFailure { LogUtil.e(it, "安装旧版资源替换修复失败，当前仅保留直接注入") }
    }
}
