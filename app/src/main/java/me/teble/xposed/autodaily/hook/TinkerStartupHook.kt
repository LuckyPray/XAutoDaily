package me.teble.xposed.autodaily.hook

import android.content.Context
import android.util.Log
import dalvik.system.BaseDexClassLoader
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.teble.xposed.autodaily.utils.LogUtil
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

internal object TinkerStartupHook {

    private const val TAG = "XALog"
    private const val QFIX_APPLICATION_IMPL_PROXY =
        "com.tencent.common.app.QFixApplicationImplProxy"

    private val installed = AtomicBoolean(false)

    fun initializeBeforeAppCreate(
        initialClassLoader: ClassLoader,
        hostPackageName: String,
        onRuntimeClassLoaderReady: (ClassLoader) -> Unit,
    ) {
        if (!installed.compareAndSet(false, true)) {
            return
        }

        val completed = AtomicBoolean(false)
        val callingThread = AtomicReference<Thread?>()
        val capturedClassLoader = AtomicReference<ClassLoader?>()
        val attachHook = AtomicReference<XC_MethodHook.Unhook?>()
        val constructorHooks = AtomicReference<Set<XC_MethodHook.Unhook>?>()

        fun cleanup() {
            attachHook.getAndSet(null)?.unhook()
            constructorHooks.getAndSet(null)?.forEach { it.unhook() }
        }

        fun complete(runtimeClassLoader: ClassLoader) {
            if (!completed.compareAndSet(false, true)) {
                return
            }
            callingThread.set(null)
            cleanup()
            LogUtil.i("Using host runtime class loader: $runtimeClassLoader")
            try {
                onRuntimeClassLoaderReady(runtimeClassLoader)
            } catch (e: Throwable) {
                if (runtimeClassLoader === initialClassLoader) {
                    throw e
                }
                LogUtil.e(e, "Runtime class loader initialization failed, trying initial loader")
                onRuntimeClassLoaderReady(initialClassLoader)
            }
        }

        try {
            val proxyClass = initialClassLoader.loadClass(QFIX_APPLICATION_IMPL_PROXY)
            val attachBaseContext =
                proxyClass.getDeclaredMethod("attachBaseContext", Context::class.java)

            attachHook.set(XposedBridge.hookMethod(attachBaseContext, object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    callingThread.set(Thread.currentThread())
                }

                override fun afterHookedMethod(param: MethodHookParam) {
                    complete(capturedClassLoader.get() ?: initialClassLoader)
                }
            }))

            constructorHooks.set(
                XposedBridge.hookAllConstructors(
                    BaseDexClassLoader::class.java,
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            if (callingThread.get() !== Thread.currentThread()) {
                                return
                            }

                            val classLoader = param.thisObject as ClassLoader
                            if (!isTinkerClassLoader(classLoader, hostPackageName)) {
                                return
                            }

                            if (capturedClassLoader.compareAndSet(null, classLoader)) {
                                LogUtil.i("Captured Tinker class loader: $classLoader")
                            }
                        }
                    },
                ),
            )
        } catch (e: ClassNotFoundException) {
            complete(initialClassLoader)
        } catch (e: NoSuchMethodException) {
            complete(initialClassLoader)
        } catch (e: Throwable) {
            LogUtil.e(e, "Failed to install Tinker startup hook, using initial class loader")
            complete(initialClassLoader)
        }
    }

    private fun isTinkerClassLoader(classLoader: ClassLoader, hostPackageName: String): Boolean {
        val description = classLoader.toString()
        if (description.contains("me.teble.xposed.autodaily")) {
            return false
        }
        val className = classLoader.javaClass.name
        return className.contains("TinkerClassLoader") ||
            description.contains("/tinker/") ||
            className == "dalvik.system.DelegateLastClassLoader" &&
            description.contains(hostPackageName)
    }
}
