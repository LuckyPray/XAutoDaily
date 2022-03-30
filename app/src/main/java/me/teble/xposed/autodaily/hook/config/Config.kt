package me.teble.xposed.autodaily.hook.config

import android.annotation.SuppressLint
import com.tencent.mmkv.MMKV
import me.teble.xposed.autodaily.hook.base.Global
import me.teble.xposed.autodaily.hook.base.Global.hostContext
import me.teble.xposed.autodaily.hook.base.Initiator.getSimpleName
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.currentUin
import me.teble.xposed.autodaily.utils.NativeUtil
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object Config {

    private const val TAG = "Config"
    val mmkvDir by lazy {
        val dir = File(hostContext.filesDir, "xa_mmkv")
        if (dir.isFile) {
            dir.delete()
        }
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return@lazy dir
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    fun init() {
        val soFile = NativeUtil.getNativeLibrary(hostContext, "xa_native")
        try {
            MMKV.initialize(hostContext, mmkvDir.absolutePath) {
                System.load(soFile.absolutePath)
            }
        } catch (e: Throwable) {
            System.load(soFile.absolutePath)
            MMKV.initialize(hostContext, soFile.absolutePath) {}
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