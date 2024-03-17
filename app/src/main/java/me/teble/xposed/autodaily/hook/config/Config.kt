package me.teble.xposed.autodaily.hook.config

import android.annotation.SuppressLint
import com.tencent.mmkv.MMKV
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableMap
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

    private val obfuscate = persistentMapOf(
        "Lcom/tencent/mobileqq/activity/ChatActivityFacade;" to persistentSetOf(
            "^reSendEmo",
        ),
        "Lcooperation/qzone/PlatformInfor;" to persistentSetOf(
            "^52b7f2$",
            "^qimei",
        ),
        "Lcom/tencent/mobileqq/service/MobileQQServiceBase;" to persistentSetOf(
            "^req_pb_protocol_flag$", "^MobileQQServiceBase$"
        ),
    )
    const val hooksVersion = 2
    val confuseInfo = obfuscate.mapKeys { (k, _) ->
        getSimpleName(k)
    }.toImmutableMap()
}