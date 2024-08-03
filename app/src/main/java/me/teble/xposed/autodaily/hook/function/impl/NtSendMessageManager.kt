package me.teble.xposed.autodaily.hook.function.impl

import com.tencent.mobileqq.qroute.QRoute
import com.tencent.qqnt.kernel.nativeinterface.IOperateCallback
import com.tencent.qqnt.kernel.nativeinterface.MsgAttributeInfo
import com.tencent.qqnt.kernel.nativeinterface.TextElement
import me.teble.xposed.autodaily.hook.base.load
import me.teble.xposed.autodaily.hook.base.loadAs
import me.teble.xposed.autodaily.hook.function.BaseSendMessage
import me.teble.xposed.autodaily.hook.utils.NtUidUtil
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.appRuntime
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.getMethods
import me.teble.xposed.autodaily.utils.invoke
import me.teble.xposed.autodaily.utils.new
import java.lang.reflect.Method
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


open class NtSendMessageManager : BaseSendMessage(
    TAG = "NtSendMessageManager"
) {
    private lateinit var msgService: Any
    private lateinit var msgUtilApi: Any
    private lateinit var contactClass: Class<*>

    // NtQQ 8.9.30+
    private var generateMsgUniqueIdMethod: Method? = null
    // lower nt qq
    private var getMsgUniqueIdMethod: Method? = null

    override fun init() {
        val kernelService = appRuntime.getRuntimeService(loadAs("com.tencent.qqnt.kernel.api.IKernelService"), "all")
        msgService = kernelService.invoke("getMsgService")!!
        val msgServiceMethods = msgService.getMethods(false)
        generateMsgUniqueIdMethod = msgServiceMethods.firstOrNull {
            it.returnType == Long::class.javaObjectType
                    && it.parameterTypes.size == 1 && it.parameterTypes[0] == Int::class.java
        }
        generateMsgUniqueIdMethod ?: run {
            getMsgUniqueIdMethod = msgServiceMethods.firstOrNull {
                it.returnType == Long::class.javaObjectType && it.parameterTypes.isEmpty()
            }
        }

        msgUtilApi = QRoute.api(loadAs("com.tencent.qqnt.msg.api.IMsgUtilApi"))
        contactClass = load("com.tencent.qqnt.kernelpublic.nativeinterface.Contact")
            ?: load("com.tencent.qqnt.kernel.nativeinterface.Contact")!!
    }

    override fun sendTextMessage(uin: String, msg: String, isGroup: Boolean) {
        val chatType = if (isGroup) 2 else 1
        val peerUid = if (isGroup) {
            uin
        } else {
            NtUidUtil.getUidFromUin(uin)
        }
        val guildId = ""
        val contact = contactClass.new(chatType, peerUid, guildId)

        val textElement = TextElement()
        textElement.content = msg
        val msgElement = msgUtilApi.invoke("createTextElement", textElement)!!
        val msgElements = arrayListOf(msgElement)

        val msgUniqueId = getMsgUniqueId(chatType)

        LogUtil.d("msgId: $msgUniqueId, contact: $contact, msgElements: $msgElements")

        val countDownLatch = CountDownLatch(1)

        msgService.invoke(
            "sendMsg",
            msgUniqueId,
            contact,
            msgElements,
            HashMap<Int, MsgAttributeInfo>(),
            object : IOperateCallback {
                override fun onResult(result: Int, msg: String) {
                    countDownLatch.countDown()
                }
            }
        )

        countDownLatch.await(10, TimeUnit.SECONDS)
    }

    private fun getMsgUniqueId(chatType: Int): Long {
        val ret = generateMsgUniqueIdMethod?.invoke(msgService, chatType)
            ?: getMsgUniqueIdMethod!!.invoke(msgService)
        return ret as Long
    }
}