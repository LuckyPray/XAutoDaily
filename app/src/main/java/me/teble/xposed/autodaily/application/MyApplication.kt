@file:SuppressLint("StaticFieldLeak")

package me.teble.xposed.autodaily.application

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager.GET_META_DATA
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.teble.xposed.autodaily.config.PACKAGE_NAME_QQ
import me.teble.xposed.autodaily.config.PACKAGE_NAME_TIM
import rikka.shizuku.Shizuku

lateinit var xaApp: MyApplication
val xaAppIsInit get() = ::xaApp.isInitialized
lateinit var context: Context

@SuppressLint("MutableCollectionMutableState")
class MyApplication : Application() {

    lateinit var prefs: SharedPreferences

    private val globalScope = CoroutineScope(Dispatchers.Default)

    var qPackageState by mutableStateOf(mutableMapOf<String, Boolean>())

    override fun onCreate() {
        super.onCreate()
        Shizuku.pingBinder()
        context = applicationContext
        xaApp = this
        prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        globalScope.launch {
            fetchAppList()
        }
    }
}

val qPackageSet = setOf(PACKAGE_NAME_QQ, PACKAGE_NAME_TIM)

suspend fun fetchAppList() {
    withContext(Dispatchers.IO) {
        val pm = xaApp.packageManager
        pm.getInstalledApplications(GET_META_DATA).let {
            Log.d("XALog", it.count().toString())
            it.forEach {
                val packageName = it.packageName
                if (qPackageSet.contains(packageName)) {
                    Log.d("XALog", packageName)
                    xaApp.qPackageState[packageName] = true
                }
            }
        }
    }
}