package me.teble.xposed.autodaily.hook.config

import android.annotation.SuppressLint
import com.tencent.mmkv.MMKV
import me.teble.xposed.autodaily.hook.base.Global
import me.teble.xposed.autodaily.hook.base.Global.hostContext
import me.teble.xposed.autodaily.hook.base.Initiator.getSimpleName
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.currentUin
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.NativeUtil
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object Config {

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    fun init() {
        val mmkvDir = File(hostContext.filesDir, "xa_mmkv")
        if (mmkvDir.isFile) {
            mmkvDir.delete()
        }
        if (!mmkvDir.exists()) {
            mmkvDir.mkdirs()
        }
        val soFile = NativeUtil.getNativeLibrary(hostContext, "mmkv")
        try {
            MMKV.initialize(hostContext, mmkvDir.absolutePath) {
                try {
                    System.load(soFile.absolutePath)
                } catch (e: Exception) {
                    LogUtil.e(e, "load mmkv lib failed")
                    throw UnsatisfiedLinkError("load mmkv lib failed")
                }
            }
        } catch (e: Throwable) {
            LogUtil.d("MMKV", "加载失败: " + e.stackTraceToString())
            throw e
        }
    }

    val classCache by lazy {
        if (!Global.isInit()) MockConfProxy()
        else ConfProxy(MMKV.mmkvWithID("XAHooks", MMKV.SINGLE_PROCESS_MODE))
    }

    val xaConfig by lazy {
        if (!Global.isInit()) MockConfProxy()
        else ConfProxy(MMKV.mmkvWithID("XAConfig", MMKV.SINGLE_PROCESS_MODE))
    }

    private val confProxyMap = ConcurrentHashMap<Long, ConfProxy>(5)

    val accountConfig: ConfProxy
        get() {
            if (!Global.isInit()) return MockConfProxy()
            return confProxyMap[currentUin] ?: let {
                val currentConf =
                    ConfProxy(MMKV.mmkvWithID("XASettings-${currentUin}", MMKV.SINGLE_PROCESS_MODE))
                confProxyMap[currentUin] = currentConf
                currentConf
            }
        }

    private val obfuscate = mapOf(
        "Lcom/tencent/mobileqq/activity/ChatActivityFacade;" to setOf(
            "reSendEmo",
        ),
        "Lcooperation/qzone/PlatformInfor;" to setOf(
            "52b7f2",
            "qimei",
        )
    )
    val hooksVersion = 1
    val confuseInfo = mutableMapOf<String, Set<String>>().apply {
        obfuscate.forEach { (k, v) ->
            put(getSimpleName(k), v)
        }
    }
}