package me.teble.xposed.autodaily.hook.function.impl

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import me.teble.xposed.autodaily.config.QQClasses
import me.teble.xposed.autodaily.config.QQClasses.Companion.ChatActivityFacade
import me.teble.xposed.autodaily.hook.base.Initiator.load
import me.teble.xposed.autodaily.hook.function.BaseFunction
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.new
import java.lang.reflect.Method

open class SendMessageManager : BaseFunction(
    TAG = "SendMessageManager"
) {
    private lateinit var sendMsgMethod: Method
    private lateinit var cSendMsgParams: Class<*>

    override fun init() {
        val facade = load(ChatActivityFacade)
            ?: throw RuntimeException("类加载失败 -> $ChatActivityFacade")
        for (mi in facade.declaredMethods) {
            if (mi.returnType != LongArray::class.java) continue
            val argt = mi.parameterTypes
            if (argt.size != 6) continue
            if (argt[1] == Context::class.java
                && argt[3] == String::class.java
                && argt[4] == ArrayList::class.java
            ) {
                if (argt[2] == load(QQClasses.SessionInfo)
                    || argt[2] == load(QQClasses.BaseSessionInfo)
                ) {
                    sendMsgMethod = mi
                    sendMsgMethod.isAccessible = true
                    cSendMsgParams = argt[5]
                    LogUtil.d("MessageManager: method -> $sendMsgMethod")
                    return
                }
            }
        }
        throw RuntimeException("没有找到发送消息的方法")
    }

    private fun createSessionInfo(uin: String, isGroup: Boolean): Parcelable {
        val parcel = Parcel.obtain()
        parcel.writeInt(if (isGroup) 1 else 0) // type 当发送给好友为0.否则为1
        parcel.writeString(uin) // uin
        parcel.writeString(if (isGroup) uin else null) //troopUin_b
        parcel.writeString(null) //uin_name_d
        parcel.writeString(null) //phoneNum_e
        parcel.writeInt(3999) //add_friend_source_id_d
        parcel.writeBundle(null)
        parcel.setDataPosition(0)

        val clSessionInfo = load(QQClasses.SessionInfo)!!
        val ret = clSessionInfo.new(parcel)
        parcel.recycle()
        return ret as Parcelable
    }

    open fun sendMessage(uin: String, msg: String, isGroup: Boolean = false): LongArray {
        return sendMessage(
            QApplicationUtil.appInterface,
            QApplicationUtil.application,
            createSessionInfo(uin, isGroup),
            msg
        )
    }

    private fun sendMessage(
        qqAppInterface: Any,
        context: Context,
        sessionInfo: Parcelable,
        msg: String
    ): LongArray {
        return sendMsgMethod.invoke(
            null,
            qqAppInterface,
            context,
            sessionInfo,
            msg,
            ArrayList<Any>(),
            cSendMsgParams.new()
        ) as LongArray
    }
}