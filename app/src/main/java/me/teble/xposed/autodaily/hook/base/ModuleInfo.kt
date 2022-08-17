@file:JvmName("ModuleInfo")
package me.teble.xposed.autodaily.hook.base

import android.content.res.XModuleResources
import me.teble.xposed.autodaily.hook.MainHook

lateinit var modulePath: String
lateinit var moduleRes: XModuleResources

val moduleClassLoader: ClassLoader = MainHook::class.java.classLoader!!
var moduleLoadInit = false