package me.teble.xposed.autodaily.hook

import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookReplace
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
    fun enableLog() {
        findMethod(QLog) { name == "isColorLevel" }.hookReplace { true }
    }

    @MethodHook("QLog Info 日志打印")
    fun hookInfo() {
        findMethod(QLog) {
            name == "i"
                && parameterTypes.contentEquals(arrayOf(String::class.java, Int::class.java, String::class.java, Throwable::class.java))
        }.hookReplace {
            if (qTagFilter.isEmpty() || containsTag(it.args[0] as String?)) {
                LogUtil.i("QTag: ${it.args[0]}, msg: ${it.args[2]}")
            }
            return@hookReplace Unit
        }
    }

    @MethodHook("QLog Debug 日志打印")
    fun hookDebug() {
        findMethod(QLog) {
            name == "d"
                && parameterTypes.contentEquals(arrayOf(String::class.java, Int::class.java, String::class.java, Throwable::class.java))
        }.hookReplace {
            if (qTagFilter.isEmpty() || containsTag(it.args[0] as String?)) {
                LogUtil.i("QTag: ${it.args[0]}, msg: ${it.args[2]}")
            }
            return@hookReplace Unit
        }
    }
}