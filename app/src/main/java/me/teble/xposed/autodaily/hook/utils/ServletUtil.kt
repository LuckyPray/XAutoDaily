package me.teble.xposed.autodaily.hook.utils

import com.tencent.mobileqq.pb.ByteStringMicro
import me.teble.xposed.autodaily.config.QQClasses.Companion.StQWebReq
import me.teble.xposed.autodaily.hook.base.Initiator
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.currentUin
import me.teble.xposed.autodaily.utils.*
import java.lang.reflect.Modifier
import java.text.SimpleDateFormat
import java.util.*


object ServletUtil {
    private val sdf = SimpleDateFormat("MMddHHmmss", Locale.CHINA)

    fun getTraceId(): String {
        return buildString {
            append(currentUin)
            append("_")
            append(sdf.format(Date()))
            append(System.currentTimeMillis() % 1000)
            append("_")
            append(
                Random().apply {
                    setSeed(System.currentTimeMillis())
                }.nextInt(90000) + 10000
            )
        }
    }

    fun encodeReq(seq: Long, reqBuf: ByteArray): ByteArray {
        val webReq = Initiator.load(StQWebReq)?.new()
        webReq?.fieldValue("Seq")?.invoke("set", seq)
        webReq?.fieldValue("qua")?.invoke("set", VersionUtil.qua)
        webReq?.fieldValue("deviceInfo")?.invoke("set", deviceInfo)
        webReq?.fieldValue("traceid")?.invoke("set", getTraceId())
        val byteStringMicro = ByteStringMicro.copyFrom(reqBuf)
        webReq?.fieldValue("busiBuff")?.invoke("set", byteStringMicro)
        return webReq?.invoke("toByteArray") as ByteArray
    }

    val deviceInfo by lazy {
        val cPlatformInfor = Initiator.load("cooperation.qzone.PlatformInfor")
        val platformInfor = cPlatformInfor?.fieldValue(cPlatformInfor)!!
        var info = ""
        platformInfor::class.java.methods.forEach {
            if (it.returnType == String::class.java && Modifier.isPublic(it.modifiers)
                    && it.parameterTypes.isEmpty()
                ) {
                val res = it.invoke(platformInfor) as String?
                if (res?.contains("qimei") == true) {
                    info = res
                    return@forEach
                }
            }
        }
        info
    }
}