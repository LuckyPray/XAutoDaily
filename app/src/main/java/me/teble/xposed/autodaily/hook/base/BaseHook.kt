package me.teble.xposed.autodaily.hook.base

import me.teble.xposed.autodaily.hook.annotation.MethodHook
import me.teble.xposed.autodaily.hook.base.Global.hostPackageName
import me.teble.xposed.autodaily.hook.base.Global.hostProcessName
import me.teble.xposed.autodaily.utils.LogUtil

abstract class BaseHook : IBaseHook {
    protected val TAG: String = javaClass.simpleName

    override fun init() {
        val init by lazy {
            if (!this.isCompatible || !enabled) {
                return@lazy
            }
            val packageName = hostPackageName
            logd("满足执行条件，正在加载hook")
            logd("current process name: ${hostProcessName.ifEmpty { "main" }}")
            invokeHookClassMethod(this, packageName, hostProcessName)
        }
        return init
    }

    private fun invokeHookClassMethod(impl: BaseHook, packageName: String, processName: String) {
        val methods = impl.javaClass.declaredMethods
        for (method in methods) {
            if (!method.isAnnotationPresent(MethodHook::class.java)) {
                continue
            }
            val methodHook = method.getAnnotation(MethodHook::class.java)!!
            try {
                method.isAccessible = true
                method.invoke(impl)
                logd("加载 [${methodHook.desc}] 功能完成！")
            } catch (e: Exception) {
                LogUtil.e(e, "加载 [${methodHook.desc}] 功能失败: ")
            }
        }
    }

    private val settingValue: Any?
        get() = null

    protected fun load(className: String): Class<*>? {
        return Initiator.load(className)
    }

    protected fun logd(msg: String) {
        LogUtil.d(msg)
    }

    protected fun logi(msg: String) {
        LogUtil.i(msg)
    }
}