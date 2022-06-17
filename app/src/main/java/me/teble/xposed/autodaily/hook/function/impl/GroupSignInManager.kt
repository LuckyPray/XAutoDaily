package me.teble.xposed.autodaily.hook.function.impl

import me.teble.xposed.autodaily.config.QQClasses.Companion.TroopClockInHandler
import me.teble.xposed.autodaily.hook.base.load
import me.teble.xposed.autodaily.hook.function.BaseFunction
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.appInterface
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.currentUin
import me.teble.xposed.autodaily.utils.new
import java.lang.reflect.Method

open class GroupSignInManager: BaseFunction(
    TAG = "GroupSignInManager"
) {
    private lateinit var cTroopClockInHandle: Class<*>
    private lateinit var method: Method

    override fun init() {
        cTroopClockInHandle = load(TroopClockInHandler)
            ?: throw RuntimeException("类加载失败 -> $TroopClockInHandler")
        for (m in cTroopClockInHandle.declaredMethods) {
            if (m.returnType != Void.TYPE) continue
            val argt = m.parameterTypes
            if (argt.size != 4) continue
            if (argt[0] == String::class.java && argt[1] == String::class.java
                && argt[2] == Int::class.java && argt[3] == Boolean::class.java) {
                method = m
                method.isAccessible = true
                return
            }
        }
        throw RuntimeException("没有找到签到方法")
    }

    open fun signIn(groupUin: String) {
        method.invoke(cTroopClockInHandle.new(appInterface), groupUin, "$currentUin", 0, true)
    }
}