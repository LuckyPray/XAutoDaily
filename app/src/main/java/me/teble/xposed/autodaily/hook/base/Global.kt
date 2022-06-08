package me.teble.xposed.autodaily.hook.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.XModuleResources
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import me.teble.xposed.autodaily.hook.enums.QQTypeEnum
import me.teble.xposed.autodaily.hook.shizuku.ShizukuApi
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.getAppVersionCode
import me.teble.xposed.autodaily.utils.getAppVersionName

/**
 * @author teble
 * @date 2021/6/5 1:19
 * @description
 */
@SuppressLint("StaticFieldLeak")
object Global {
    const val TAG = "Global"

    // host
    lateinit var hostClassLoader: ClassLoader
    lateinit var hostPackageName: String
    lateinit var hostProcessName: String
    lateinit var hostContext: Context
    lateinit var qqTypeEnum: QQTypeEnum
    var qqVersionCode: Long = 0
    lateinit var qqVersionName: String

    var initLPPFlag = false
    var initContextFlag = false

    fun isInit() = initLPPFlag and initContextFlag

    // module
    val moduleClassLoader: ClassLoader = this::class.java.classLoader!!
    lateinit var modulePath: String
    lateinit var moduleRes: XModuleResources

    fun init(loadPackageParam: LoadPackageParam) {
        hostPackageName = loadPackageParam.packageName
        LogUtil.d("hostPackageName -> $hostPackageName")
        val strings = loadPackageParam.processName.split(":", ignoreCase = false, limit = 2)
        // TODO ProcessEnum
        hostProcessName = if (strings.size > 1) strings[strings.size - 1] else ""
        LogUtil.d("hostProcessName -> ${hostProcessName.ifEmpty { "main" }}")
        initLPPFlag = true
    }

    fun initContext(qContext: Context) {
        hostContext = qContext
        hostClassLoader = hostContext.classLoader
        qqTypeEnum = QQTypeEnum.valueOfPackage(hostContext.packageName)
        qqVersionCode = getAppVersionCode(hostContext, qqTypeEnum.packageName)
        qqVersionName = getAppVersionName(hostContext, qqTypeEnum.packageName)
        initContextFlag = true
    }
}