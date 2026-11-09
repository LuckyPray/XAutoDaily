package me.teble.xposed.autodaily.shizuku

import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import java.lang.reflect.Proxy
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * 仅在 shell 或 root 身份的 UserService 中使用，通过 getType 唤醒宿主，不读取 Provider 数据。
 */
internal class ProviderWakeController(private val userId: Int) {
    private val pm by lazy { Class.forName("android.app.AppGlobals").getMethod("getPackageManager").invoke(null) }
    private val pmClass by lazy { Class.forName("android.content.pm.IPackageManager") }

    fun packageInfo(packageName: String): PackageInfo {
        val method = pmClass.methods.first { it.name == "getPackageInfo" && it.parameterTypes.size == 3 }
        val flags: Any = if (method.parameterTypes[1] == Long::class.javaPrimitiveType)
            PackageManager.GET_PROVIDERS.toLong() else PackageManager.GET_PROVIDERS
        return checkNotNull(method.invoke(pm, packageName, flags, userId) as? PackageInfo) { "$packageName 未安装或不可见" }
    }

    fun wake(packageName: String) {
        require(packageName in WakeProtocol.hosts)
        val info = packageInfo(packageName)
        val candidates = listOf(
            "com.tencent.mqq.shared_file_accessor.ContentProviderImpl",
            "androidx.lifecycle.ProcessLifecycleOwnerInitializer",
        )
        val provider = candidates.firstNotNullOfOrNull { name ->
            info.providers?.firstOrNull {
                it.name == name && it.enabled && it.applicationInfo.enabled &&
                    it.packageName == packageName && it.processName == packageName && !it.authority.isNullOrBlank()
            }
        } ?: error("$packageName 没有支持的主进程 Provider；未尝试未知组件")
        val uri = Uri.Builder().scheme("content").authority(provider.authority.split(';').first())
            .path(WakeProtocol.WAKE_PATH).build()
        mime(uri)
    }

    private fun activityManager(): Any =
        runCatching { ActivityManager::class.java.getDeclaredMethod("getService").invoke(null) }
            .getOrElse { Class.forName("android.app.ActivityManagerNative").getMethod("getDefault").invoke(null) }

    fun offer(intent: Intent) {
        val iam = Class.forName("android.app.IActivityManager")
        val method = iam.methods.first { it.name == "broadcastIntent" && it.parameterTypes.size == 13 }
        method.invoke(activityManager(), null, intent, null, null, 0, null, null, null, -1, null, false, false, userId)
    }

    private fun mime(uri: Uri) {
        val am = activityManager()
        val iam = Class.forName("android.app.IActivityManager")
        val asyncMethod = listOf("getMimeTypeFilterAsync", "getProviderMimeTypeAsync").firstNotNullOfOrNull { name ->
            iam.methods.firstOrNull { it.name == name && it.parameterTypes.size == 3 }
        }
        if (asyncMethod == null) {
            iam.getMethod("getProviderMimeType", Uri::class.java, Int::class.javaPrimitiveType)
                .invoke(am, uri, userId)
            return
        }
        val callbackClass = Class.forName("android.os.RemoteCallback")
        val listenerClass = Class.forName("android.os.RemoteCallback\$OnResultListener")
        val complete = CountDownLatch(1)
        val listener = Proxy.newProxyInstance(listenerClass.classLoader, arrayOf(listenerClass)) { proxy, method, args ->
            when (method.name) {
                "onResult" -> { complete.countDown(); null }
                "hashCode" -> System.identityHashCode(proxy)
                "equals" -> proxy === args?.firstOrNull()
                "toString" -> "XAutoDailyMimeCallback"
                else -> null
            }
        }
        val callback = callbackClass.getConstructor(listenerClass).newInstance(listener)
        asyncMethod.invoke(am, uri, userId, callback)
        check(complete.await(15, TimeUnit.SECONDS)) { "Provider MIME 调用超时" }
        // MIME 回调完成或返回 null 均不代表任务环境已就绪，仍须等待宿主单独握手。
    }
}
