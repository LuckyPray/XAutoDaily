package me.teble.xposed.autodaily.hook.utils

import com.tencent.mobileqq.pb.ByteStringMicro
import me.teble.xposed.autodaily.config.StQWebReq
import me.teble.xposed.autodaily.hook.base.load
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.currentUin
import me.teble.xposed.autodaily.utils.TimeUtil
import me.teble.xposed.autodaily.utils.fieldValue
import me.teble.xposed.autodaily.utils.invoke
import me.teble.xposed.autodaily.utils.new
import java.lang.reflect.Modifier
import java.text.SimpleDateFormat
import java.util.*


object ServletUtil {
    private val sdf = SimpleDateFormat("MMddHHmmss", Locale.CHINA)

    private fun getTraceId(): String {
        return buildString {
            append(currentUin)
            append("_")
            append(sdf.format(Date()))
            append(TimeUtil.cnTimeMillis() % 1000)
            append("_")
            append(
                Random().apply {
                    setSeed(TimeUtil.cnTimeMillis())
                }.nextInt(90000) + 10000
            )
        }
    }

    fun encodeReq(seq: Long, reqBuf: ByteArray): ByteArray {
        val webReq = load(StQWebReq)?.new()
        webReq?.fieldValue("Seq")?.invoke("set", seq)
        webReq?.fieldValue("qua")?.invoke("set", VersionUtil.qua)
        webReq?.fieldValue("deviceInfo")?.invoke("set", deviceInfo)
        webReq?.fieldValue("traceid")?.invoke("set", getTraceId())
        val byteStringMicro = ByteStringMicro.copyFrom(reqBuf)
        webReq?.fieldValue("busiBuff")?.invoke("set", byteStringMicro)
        return webReq?.invoke("toByteArray") as ByteArray
    }

    private val deviceInfo by lazy {
        val cPlatformInfor = load("cooperation.qzone.PlatformInfor")
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