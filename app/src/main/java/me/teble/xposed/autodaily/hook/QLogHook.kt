package me.teble.xposed.autodaily.hook

import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.config.QQClasses.Companion.QLog
import me.teble.xposed.autodaily.hook.annotation.MethodHook
import me.teble.xposed.autodaily.hook.base.BaseHook
import me.teble.xposed.autodaily.utils.LogUtil

class QLogHook : BaseHook() {

    override val isCompatible: Boolean
        get() = BuildConfig.DEBUG

    override val enabled: Boolean
        get() = false

    private val qTagFilter = setOf<String>(
        "PublicAccountManager",
        "reportsendMenuEventequest",
        "PublicAccountManager-Click:",
        "PublicAccountManager-Event:",
        "PublicAccountManager-Location:",
    )

    private fun containsTag(tag: String?): Boolean {
        if (tag in qTagFilter) {
            return true
        }
        for (value in qTagFilter) {
            tag?.let {
                if (tag.contains(value, true)) {
                    return true
                }
            }
        }
        return false
    }

    @MethodHook("QLog 日志等级 hook")
    public fun enableLog() {
        XposedHelpers.findAndHookMethod(
            load(QLog),
            "isColorLevel",
            XC_MethodReplacement.returnConstant(true)
        )
    }

    @MethodHook("QLog Info 日志打印")
    public fun hookInfo() {
        XposedHelpers.findAndHookMethod(load(QLog),
            "i",
            String::class.java, Int::class.java, String::class.java, Throwable::class.java,
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(p0: MethodHookParam): Any {
                    if (qTagFilter.isEmpty() || containsTag(p0.args[0] as String?)) {
                        LogUtil.i("QTag: ${p0.args[0]}, msg: ${p0.args[2]}")
                    }
                    return Unit
                }
            }
        )
    }

    @MethodHook("QLog Debug 日志打印")
    public fun hookDebug() {
        XposedHelpers.findAndHookMethod(load(QLog),
            "d",
            String::class.java, Int::class.java, String::class.java, Throwable::class.java,
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(p0: MethodHookParam): Any {
                    if (qTagFilter.isEmpty() || p0.args[0] in qTagFilter) {
                        LogUtil.d("QTag: ${p0.args[0]}, msg: ${p0.args[2]}")
                    }
                    return Unit
                }
            }
        )
    }
}