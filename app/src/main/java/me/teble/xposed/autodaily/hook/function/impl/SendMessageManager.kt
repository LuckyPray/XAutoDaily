package me.teble.xposed.autodaily.hook.function.impl

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import me.teble.xposed.autodaily.config.ChatActivityFacade
import me.teble.xposed.autodaily.config.SessionInfo
import me.teble.xposed.autodaily.hook.base.load
import me.teble.xposed.autodaily.hook.function.BaseSendMessage
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.new
import java.lang.reflect.Method

open class SendMessageManager : BaseSendMessage(
    TAG = "SendMessageManager"
) {
    private lateinit var sendMsgMethod: Method
    private lateinit var cSendMsgParams: Class<*>
    private lateinit var cSessionInfo: Class<*>

    override fun init() {
        val facade = load(ChatActivityFacade)
            ?: throw RuntimeException("类加载失败 -> $ChatActivityFacade")
        cSessionInfo = load(SessionInfo)
            ?: throw RuntimeException("类加载失败 -> $SessionInfo")
        for (mi in facade.declaredMethods) {
            if (mi.returnType != LongArray::class.java) continue
            val argt = mi.parameterTypes
            if (argt.size != 6) continue
            if (argt[1] == Context::class.java
                && argt[3] == String::class.java
                && argt[4] == ArrayList::class.java
            ) {
                // argt[2] is BaseSessionInfo or SessionInfo
                sendMsgMethod = mi
                sendMsgMethod.isAccessible = true
                cSendMsgParams = argt[5]
                LogUtil.d("MessageManager: method -> $sendMsgMethod")
                return
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

        val ret = cSessionInfo.new(parcel)
        parcel.recycle()
        return ret as Parcelable
    }

    override fun sendTextMessage(uin: String, msg: String, isGroup: Boolean) {
        sendMessage(
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