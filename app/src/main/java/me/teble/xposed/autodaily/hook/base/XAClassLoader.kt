package me.teble.xposed.autodaily.hook.base

import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.field
import me.teble.xposed.autodaily.utils.fieldValue
import java.net.URL

class XAClassLoader(
    private val qClassLoader: ClassLoader,
    private val mParentClassLoader: ClassLoader
) : ClassLoader() {

    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        return if (switchHostClass(name)) {
            qClassLoader.loadClass(name)
        } else {
            mParentClassLoader.loadClass(name)
        }
    }

    override fun getResource(name: String): URL? {
        return mParentClassLoader.getResource(name) ?: qClassLoader.getResource(name)
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
            "mqq.",
            "oicq."
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

fun injectClassLoader(hostClassLoader: ClassLoader) {
    val fParent = ClassLoader::class.java.field("parent")!!
    val mClassloader = moduleClassLoader
    val parentClassloader = mClassloader.fieldValue("parent", true) as ClassLoader
    runCatching {
        if (XAClassLoader::class.java != parentClassloader.javaClass) {
            LogUtil.d("replace parent classloader")
            fParent.set(mClassloader, XAClassLoader(hostClassLoader, parentClassloader))
        }
    }.onFailure { LogUtil.e(it) }
}