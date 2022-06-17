@file:JvmName("HostInfo")
package me.teble.xposed.autodaily.hook.base

import android.app.Application
import me.teble.xposed.autodaily.utils.getAppName
import me.teble.xposed.autodaily.utils.getAppVersionCode
import me.teble.xposed.autodaily.utils.getAppVersionName

lateinit var hostApp: Application
lateinit var hostClassLoader: ClassLoader
lateinit var hostPackageName: String
lateinit var hostProcessName: String

val hostContext get() = hostApp
val hostAppName by lazy { getAppName(hostContext, hostPackageName) }
val hostVersionCode by lazy { getAppVersionCode(hostContext, hostPackageName) }
val hostVersionName by lazy { getAppVersionName(hostContext, hostProcessName) }

val hostInit get() = ::hostApp.isInitialized