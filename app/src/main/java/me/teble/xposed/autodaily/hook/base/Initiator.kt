@file:JvmName("Initiator")
package me.teble.xposed.autodaily.hook.base

import me.teble.xposed.autodaily.hook.config.Config
import me.teble.xposed.autodaily.hook.config.Config.classCache
import me.teble.xposed.autodaily.utils.LogUtil

private val mClassCache: MutableMap<String, Class<*>?> = HashMap()
private val confusedClass by lazy {
    Config.confuseInfo.keys.map { getSimpleName(it) }.toSet()
}

fun getSimpleName(className: String): String {
    var name = className
    if (name.startsWith('L') && name.endsWith(';') || name.contains('/')) {
        var flag = 0
        if (name.startsWith('L')) {
            flag = flag or (1 shl 1)
        }
        if (name.endsWith(';')) {
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
        val realClassName = classCache.getString("${name}#${hostVersionCode}")
        LogUtil.d("混淆缓存：${name}#${hostVersionCode} -> $realClassName")
        realClassName?.let {
            val clazz = classLoader.loadClass(getSimpleName(realClassName))
            mClassCache[name] = clazz
            return clazz
        }
        return null
    }
    runCatching {
        val clazz = classLoader.loadClass(name)
        mClassCache[name] = clazz
        return clazz
    }.onFailure { LogUtil.log("没有找到类：$name") }
    return null
}

@JvmOverloads
@Suppress("UNCHECKED_CAST")
fun <T> loadAs(
    className: String,
    classLoader: ClassLoader = hostClassLoader
): Class<T> = load(className, classLoader) as Class<T>