package me.teble.xposed.autodaily.hook

import android.util.Log
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookReplace
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.config.QLog
import me.teble.xposed.autodaily.hook.annotation.MethodHook
import me.teble.xposed.autodaily.hook.base.BaseHook

class QLogHook : BaseHook() {

    override val isCompatible: Boolean
        get() = BuildConfig.DEBUG

    override val enabled: Boolean
        get() = true

    private val qTagFilter = setOf<String>(
//        "PublicAccountManager",
//        "reportsendMenuEventequest",
//        "PublicAccountManager-Click:",
//        "PublicAccountManager-Event:",
//        "PublicAccountManager-Location:",
//        "VisitorsActivity",
//        "MobileQQServiceBase",
//        "FaceInfo",
//        "MsgRespHandler",
//        "CardHandler",
//        "MsgRespHandleReporter",
//        "mqq",
//        "TicketManager",
//        "PskeyManagerImpl",
//        "ChatActivityFacade",
        "AIOMsgService"
    )

    private val ignoreTagFilter = setOf<String>(
        "PlayerCore",
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
                && parameterTypes.contentEquals(
                arrayOf(
                    String::class.java,
                    Int::class.java,
                    String::class.java,
                    Throwable::class.java
                )
            )
        }.hookReplace {
            val tag = it.args[0] as String?
            if (!ignoreTagFilter.contains(tag)
                && qTagFilter.isEmpty() || containsTag(tag)) {
                Log.i("XALog", "QTag: ${it.args[0]}, msg: ${it.args[2]}")
            }
            return@hookReplace Unit
        }
    }

    @MethodHook("QLog Debug 日志打印")
    fun hookDebug() {
        findMethod(QLog) {
            name == "d"
                && parameterTypes.contentEquals(
                arrayOf(
                    String::class.java,
                    Int::class.java,
                    String::class.java,
                    Throwable::class.java
                )
            )
        }.hookReplace {
            val tag = it.args[0] as String?
            if (!ignoreTagFilter.contains(tag)
                && qTagFilter.isEmpty() || containsTag(tag)) {
                Log.i("XALog", "QTag: ${it.args[0]}, msg: ${it.args[2]}")
            }
            return@hookReplace Unit
        }
    }
}