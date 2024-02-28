@file:SuppressLint("StaticFieldLeak")

package me.teble.xposed.autodaily.application

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.GET_META_DATA
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.teble.xposed.autodaily.config.PACKAGE_NAME_QQ
import me.teble.xposed.autodaily.config.PACKAGE_NAME_TIM
import me.teble.xposed.autodaily.shizuku.ShizukuApi
import rikka.shizuku.Shizuku

lateinit var xaApp: XaApplication

lateinit var context: Context

class XaApplication : MultiDexApplication() {

    companion object {
        var applicationScope = MainScope()
    }


    var qPackageState = mutableStateMapOf<String, Boolean>()


    @SuppressLint("UnsafeDynamicallyLoadedCode")
    override fun onCreate() {
        super.onCreate()
        Shizuku.pingBinder()
        ShizukuApi.init()
        MultiDex.install(this)
        context = applicationContext
        xaApp = this
        applicationScope.launch(IO) {
            fetchAppList()
        }

        MMKV.initialize(this) {
            System.loadLibrary("xa_native")
        }

    }

    override fun onLowMemory() {
        super.onLowMemory()
        applicationScope.cancel("onLowMemory() called by system")
        applicationScope = MainScope()
    }

}

val qPackageSet = setOf(PACKAGE_NAME_QQ, PACKAGE_NAME_TIM)

suspend fun fetchAppList() {
    withContext(IO) {
        val pm = xaApp.packageManager
        pm.getInstalledApplications(GET_META_DATA).let {
            Log.d("XALog", it.count().toString())
            it.forEach { info ->
                val packageName = info.packageName
                if (qPackageSet.contains(packageName)) {
                    Log.d("XALog", packageName)
                    xaApp.qPackageState[packageName] = true
                }
            }
        }
    }
}