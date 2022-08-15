package me.teble.xposed.autodaily.hook.config

import android.annotation.SuppressLint
import com.tencent.mmkv.MMKV
import me.teble.xposed.autodaily.hook.base.getSimpleName
import me.teble.xposed.autodaily.hook.base.hostContext
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.currentUin
import me.teble.xposed.autodaily.utils.NativeUtil
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object Config {

    var isInit = false
        private set
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
        isInit = true
    }

    val classCache by lazy {
        ConfProxy(MMKV.mmkvWithID("XAHooks", MMKV.MULTI_PROCESS_MODE))
    }

    val xaConfig by lazy {
        ConfProxy(MMKV.mmkvWithID("XAConfig", MMKV.MULTI_PROCESS_MODE))
    }

    private val confProxyMap = ConcurrentHashMap<Long, ConfProxy>(5)

    val accountConfig: ConfProxy
        get() {
            return confProxyMap[currentUin] ?: let {
                val currentConf =
                    ConfProxy(MMKV.mmkvWithID("XASettings-${currentUin}", MMKV.MULTI_PROCESS_MODE))
                confProxyMap[currentUin] = currentConf
                currentConf
            }
        }

    private val obfuscate = mapOf(
        "Lcom/tencent/mobileqq/activity/ChatActivityFacade;" to setOf(
            "^reSendEmo",
        ),
        "Lcooperation/qzone/PlatformInfor;" to setOf(
            "^52b7f2$",
            "^qimei",
        ),
        "Lcom/tencent/mobileqq/troop/clockin/handler/TroopClockInHandler;" to setOf(
            "^TroopClockInHandler$"
        )
    )
    const val hooksVersion = 1
    val confuseInfo = mutableMapOf<String, Set<String>>().apply {
        obfuscate.forEach { (k, v) ->
            put(getSimpleName(k), v)
        }
    }
}