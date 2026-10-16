package me.teble.xposed.autodaily.hook.base

import android.util.Log
import java.net.URL

class XAClassLoader(
    private val mParentClassLoader: ClassLoader
) : ClassLoader() {

    // Tinker 可能替换宿主 ClassLoader，每次委托时读取当前值，不能保存注入时的快照。
    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        return if (switchHostClass(name)) {
            hostClassLoader.loadClass(name)
        } else {
            mParentClassLoader.loadClass(name)
        }
    }

    override fun getResource(name: String): URL? {
        return mParentClassLoader.getResource(name) ?: hostClassLoader.getResource(name)
    }

    companion object {
        val qClassCache: MutableMap<String, Boolean> = HashMap()

        // 代码中使用的qq类包名前缀写进此处
        private val hostPackages = listOf(
            "com.qq.",
            "com.tencent.common.app.",
            "com.tencent.mobileqq.",
            "com.tencent.qphone.base.",
            "com.tencent.widget.",
            "com.tencent.qqnt.",
            "mqq.",
            "oicq.",
            "tencent.im",
        )

        fun switchHostClass(name: String): Boolean {
            // TODO 缓存实现，策略还没想好
            // TODO 使用前缀树进行加速匹配
            for (pack in hostPackages) {
                if (name.startsWith(pack)) {
                    return true
                }
            }
            return false
        }
    }
}

fun injectClassLoader() {
    // 注入完成之前不能调用 LogUtil 或其他可能间接引用宿主类的工具。
    try {
        val fParent = ClassLoader::class.java.getDeclaredField("parent")
        fParent.isAccessible = true
        val mClassloader = moduleClassLoader
        val parentClassloader = fParent.get(mClassloader) as ClassLoader
        if (XAClassLoader::class.java != parentClassloader.javaClass) {
            fParent.set(mClassloader, XAClassLoader(parentClassloader))
            Log.d("XALog", "replace parent classloader")
        }
        isInjectClassLoader = true
    } catch (e: Throwable) {
        Log.e("XALog", "Failed to inject host class loader", e)
        throw e
    }
}

var isInjectClassLoader = false
