@file:SuppressLint("StaticFieldLeak")

package me.teble.xposed.autodaily.application

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.GET_META_DATA
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext
import me.teble.xposed.autodaily.config.PACKAGE_NAME_QQ
import me.teble.xposed.autodaily.config.PACKAGE_NAME_TIM
import me.teble.xposed.autodaily.shizuku.DaemonConnectionManager
import me.teble.xposed.autodaily.shizuku.ShizukuApi
import rikka.shizuku.Shizuku

lateinit var xaApp: XaApplication

lateinit var context: Context

class XaApplication : MultiDexApplication() {

    companion object {
        var applicationScope = MainScope()
    }


    val qPackageState = mutableStateMapOf<String, Boolean>()
    var hostAppListState by mutableStateOf(HostAppListState.Checking)


    @SuppressLint("UnsafeDynamicallyLoadedCode")
    override fun onCreate() {
        super.onCreate()
        Shizuku.pingBinder()
        ShizukuApi.init()
        MultiDex.install(this)
        context = applicationContext
        xaApp = this
        // 由可见的 Activity 检查运行时权限后，再开始读取宿主列表。

        MMKV.initialize(this) {
            System.loadLibrary("xa_native")
        }
        DaemonConnectionManager.initialize()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        applicationScope.cancel("onLowMemory() called by system")
        applicationScope = MainScope()
    }

}

val qPackageSet = setOf(PACKAGE_NAME_QQ, PACKAGE_NAME_TIM)

suspend fun fetchAppList() {
    val needsPermission = withContext(Main.immediate) {
        HostAppAccess.needsPermission(xaApp).also { required ->
            xaApp.hostAppListState = if (required) HostAppListState.PermissionRequired else HostAppListState.Checking
            if (required) xaApp.qPackageState.clear()
        }
    }
    if (needsPermission) return
    try {
        val hosts = withContext(IO) {
            xaApp.packageManager.getInstalledApplications(GET_META_DATA)
                .filter { it.packageName in qPackageSet }
                .associate { it.packageName to true }
        }
        withContext(Main.immediate) {
            xaApp.qPackageState.clear()
            if (HostAppAccess.needsPermission(xaApp)) {
                xaApp.hostAppListState = HostAppListState.PermissionRequired
            } else {
                xaApp.qPackageState.putAll(hosts)
                xaApp.hostAppListState = HostAppListState.Ready
                Log.d("XALog", "宿主检测完成: ${hosts.keys}")
            }
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.w("XALog", "读取宿主应用列表失败", e)
        withContext(Main.immediate) {
            xaApp.hostAppListState = if (HostAppAccess.needsPermission(xaApp)) {
                HostAppListState.PermissionRequired
            } else {
                HostAppListState.Failed
            }
        }
    }
}
