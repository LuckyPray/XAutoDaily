package me.teble.xposed.autodaily.hook.base

import me.teble.xposed.autodaily.hook.base.Global.qqVersionCode
import me.teble.xposed.autodaily.hook.config.Config
import me.teble.xposed.autodaily.hook.config.Config.classCache
import me.teble.xposed.autodaily.utils.LogUtil
import java.util.*

object Initiator {

    private lateinit var hostClassLoader: ClassLoader
    private val mClassCache: MutableMap<String, Class<*>?> = HashMap()
    private val confusedClass by lazy {
        return@lazy Config.confuseInfo.keys.map { getSimpleName(it) }.toSet()
    }

    fun init(hostClassLoader: ClassLoader) {
        this.hostClassLoader = hostClassLoader
    }

    fun getSimpleName(className: String): String {
        var name = className
        if (name.endsWith(';') || name.contains('/')) {
            var flag = 0
            if (name.startsWith("L")) {
                flag = flag or (1 shl 1)
            }
            if (name.endsWith(";")) {
                flag = flag or 1
            }
            if (flag > 0) {
                name = name.substring(flag shr 1, name.length - (flag and 1))
            }
            name = name.replace('/', '.')
        }
        return name
    }

    @JvmOverloads
    fun load(className: String, classLoader: ClassLoader = hostClassLoader): Class<*>? {
        // 获取正常类名
        val name = getSimpleName(className)
        if (classLoader == hostClassLoader && mClassCache.containsKey(className)) {
            return mClassCache[name]
        }
        if (confusedClass.contains(name)) {
            // 使用正常类名获取混淆类名
            val realClassName = classCache.getString("${name}#${qqVersionCode}")
            return realClassName?.let {
                val cls = classLoader.loadClass(getSimpleName(realClassName))
                mClassCache[name] = cls
                cls
            } ?: let {
                mClassCache[name] = null
                null
            }
        }
        return try {
            val clazz = classLoader.loadClass(name)
            mClassCache[name] = clazz
            clazz
        } catch (e: Exception) {
            LogUtil.log("没有找到类：$name")
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> loadAs(
        className: String,
        classLoader: ClassLoader = hostClassLoader
    ): Class<T> = load(className, classLoader) as Class<T>
}