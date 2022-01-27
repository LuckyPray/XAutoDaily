package me.teble.xposed.autodaily.hook.base

import java.net.URL

class XAClassLoader(
    private val qClassLoader: ClassLoader,
    private val mParentClassLoader: ClassLoader
) : ClassLoader() {

    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        if (name == "androidx.lifecycle.ReportFragment" || name.startsWith("androidx.activity")) {
            return try {
                qClassLoader.loadClass(name)
            } catch (e: Exception) {
                mParentClassLoader.loadClass(name)
            }
        }
        return if (switchHostClass(name)) qClassLoader.loadClass(name) else mParentClassLoader.loadClass(name)
    }

    override fun getResource(name: String): URL {
        return mParentClassLoader.getResource(name) ?: qClassLoader.getResource(name)
    }

    companion object {
        val qClassCache: MutableMap<String, Boolean> = HashMap()

        // 代码中使用的qq类包名前缀写进此处
        private val hostPackages = listOf(
            "com.qq.",
            "com.tencent.",
            "mqq.",
            "oicq."
        )

        fun switchHostClass(name: String): Boolean {
//            TODO 缓存实现，策略还没想好
//             TODO 使用前缀树进行加速匹配
            for (pack in hostPackages) {
                if (name.startsWith(pack)) {
                    return true
                }
            }
            return false
        }
    }
}