package me.teble.xposed.autodaily.hook.utils

import android.app.Application
import android.text.TextUtils
import de.robv.android.xposed.XposedHelpers
import me.teble.xposed.autodaily.utils.fieldValueAs
import java.lang.reflect.Method


fun isInjector(flag: String): Boolean {
    try {
        if (TextUtils.isEmpty(flag)) return false
        val methodCache: HashMap<String, Method> =
            XposedHelpers::class.java.fieldValueAs("methodCache")
                ?: return false
        val method: Method = Application::class.java.getMethod("onCreate")
        val key = "$flag#${method.name}"
        if (methodCache.containsKey(key)) {
            return true
        }
        methodCache[key] = method
        return false
    } catch (e: Throwable) {
        e.printStackTrace()
    }
    return false
}