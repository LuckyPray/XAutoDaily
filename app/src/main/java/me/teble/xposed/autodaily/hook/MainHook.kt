package me.teble.xposed.autodaily.hook

import android.app.AndroidAppHelper
import android.app.Application
import android.content.res.XModuleResources
import android.text.TextUtils
import cn.hutool.core.util.ReflectUtil.*
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.*
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.config.QQClasses.Companion.LoadDex
import me.teble.xposed.autodaily.dex.utils.DexKit.locateClasses
import me.teble.xposed.autodaily.hook.base.BaseHook
import me.teble.xposed.autodaily.hook.base.Global
import me.teble.xposed.autodaily.hook.base.Global.hostClassLoader
import me.teble.xposed.autodaily.hook.base.Global.init
import me.teble.xposed.autodaily.hook.base.Global.initContext
import me.teble.xposed.autodaily.hook.base.Global.qqVersionCode
import me.teble.xposed.autodaily.hook.base.Initiator
import me.teble.xposed.autodaily.hook.base.XAClassLoader
import me.teble.xposed.autodaily.hook.config.Config
import me.teble.xposed.autodaily.hook.config.Config.confuseInfo
import me.teble.xposed.autodaily.hook.config.Config.hooksVersion
import me.teble.xposed.autodaily.hook.enums.QQTypeEnum
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.fieldValueAs
import me.teble.xposed.autodaily.utils.new
import java.lang.reflect.Method

class MainHook : IXposedHookLoadPackage, IXposedHookZygoteInit {

    companion object {
        private const val TAG = "MainHook"
    }

//    private lateinit var subHookClasses: Set<String>

    private val subHookClasses: Set<Class<out BaseHook>> = setOf(
        FromServiceMsgHook::class.java,
        QLogHook::class.java,
        QQSettingSettingActivityHook::class.java,
        SplashActivityHook::class.java,
        ToServiceMsgHook::class.java,
        CoreServiceHook::class.java
    )
    private lateinit var loadPackageParam: LoadPackageParam

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        Global.modulePath = startupParam.modulePath
        Global.moduleRes = XModuleResources.createInstance(Global.modulePath, null)
        EzXHelperInit.initZygote(startupParam)
    }

    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        LogUtil.i(TAG, "handleLoadPackage: $loadPackageParam")
        this.loadPackageParam = loadPackageParam
        if (!QQTypeEnum.contain(loadPackageParam.packageName)
            || !loadPackageParam.isFirstApplication
        ) {
            return
        }
        // TODO 分进程处理
        if (loadPackageParam.processName != loadPackageParam.packageName) {
            return
        }
        init
    }

    val init by lazy {
        LogUtil.d(TAG, "current process: ${loadPackageParam.processName}")
        EzXHelperInit.initHandleLoadPackage(loadPackageParam)
        EzXHelperInit.setLogTag("XAutoDaily")
        EzXHelperInit.setToastTag("XAutoDaily")
        doInit()
    }

    private fun doInit() {
        // 初始化全局ClassLoader
        Initiator.init(loadPackageParam.classLoader)
        init(loadPackageParam)
        // 替换classloader
        replaceParentClassloader(loadPackageParam.classLoader)
        LogUtil.d(TAG, "loadPackageParam.packageName -> ${loadPackageParam.packageName}")

        findMethod(LoadDex) { returnType == Boolean::class.java && emptyParam }.hookAfter {
            // 防止hook多次被执行
            try {
                if (isInjector(this::class.java.name)) {
                    return@hookAfter
                }
                val context = AndroidAppHelper.currentApplication()
                // 初始化全局Context
                initContext(context)
//                EzXHelperInit.initAppContext(context)
                // MMKV
                Config.init()
                //加载资源注入
//                ResInjectUtil.injectRes(context.resources)
                LogUtil.d(TAG, "injectRes")
                EzXHelperInit.initAppContext(context, addPath = true, initModuleResources = true)
                //初始化代理
                LogUtil.d(TAG, "initActivityProxyManager")
                EzXHelperInit.initActivityProxyManager(
                    modulePackageName = BuildConfig.APPLICATION_ID,
                    hostActivityProxyName = "com.tencent.mobileqq.activity.photo.CameraPreviewActivity",
                    moduleClassLoader = MainHook::class.java.classLoader!!,
                    hostClassLoader = context.classLoader
                )
                LogUtil.d(TAG, "initSubActivity")
                EzXHelperInit.initSubActivity()
//                ProxyManager.init
                // dex相关
                LogUtil.d(TAG, "doDexInit")
                doDexInit()
                //初始化hook
                LogUtil.d(TAG, "doDexInit")
                initHook()
            } catch (e: Exception) {
                LogUtil.e(TAG, e)
                ToastUtil.send("初始化失败: " + e.stackTraceToString())
            }
        }
    }

    private fun doDexInit() {
        val cache = Config.classCache
        // dex解析
        // module dex
//        if (cache.getInt("moduleVersion", 0) < BuildConfig.VERSION_CODE || BuildConfig.DEBUG) {
//            LogUtil.d(TAG, "模块版本更新/Debug版本，重新定位Hook子类")
//            subHookClasses =
//                findSubClasses(MainHook::class.java.classLoader!!, BaseHook::class.java).toSet()
//            cache.putStringSet("subHookClasses", subHookClasses)
//            cache.putInt("moduleVersion", BuildConfig.VERSION_CODE)
//        } else {
//            LogUtil.d(TAG, "缓存生效，跳过hook子类解析")
//            subHookClasses = cache.getStringSet("subHookClasses") ?: emptySet()
//        }
        // qq dex
        val confuseInfoKeys = confuseInfo.keys
        val needLocateClasses = mutableSetOf<String>()
        // 清空混淆缓存
        if (cache.getInt("hooksVersion", 0) < hooksVersion) {
            LogUtil.d(TAG, "清空Hooks缓存")
            cache.clearAll()
            cache.putInt("hooksVersion", hooksVersion)
        }
        // 加入修改了特征的类
        confuseInfo.forEach {
            val key = "${it.key}#hash"
            val hash = it.value.hashCode()
            val cacheHash = cache.getInt(key, 0)
            LogUtil.d(TAG, "${it.key} cacheHash -> $cacheHash")
            LogUtil.d(TAG, "${it.key} hash -> $hash")
            if (cacheHash != hash) {
                cache.putInt(key, hash)
                needLocateClasses.add(it.key)
                LogUtil.d(TAG, "need locate -> ${it.key}")
            }
        }
        // 尝试获取，成功则加入新版缓存
        needLocateClasses.removeIf { classSimpleName ->
            LogUtil.d(TAG, "尝试获取类：$classSimpleName")
            try {
                val cls = hostClassLoader.loadClass(classSimpleName)
                LogUtil.d(TAG, "尝试获取类成功 -> ${cls.canonicalName}")
                cache.putString("$classSimpleName#${qqVersionCode}", classSimpleName)
                return@removeIf true
            } catch (e: Exception) {
                return@removeIf false
            }
        }
        if (needLocateClasses.isEmpty()) {
//            ToastUtil.send("无需重新定位，跳过执行")
            return
        }
        LogUtil.log("needLocateClasses -> $needLocateClasses")
        ToastUtil.send("正在尝试定位QQ混淆类")
        val info = needLocateClasses.associateWith { confuseInfo[it] }
        val startTime = System.currentTimeMillis()
        val locateRes = locateClasses(info)
        val useTime = System.currentTimeMillis() - startTime
        var locateNum = 0
        locateRes.forEach {
            if (it.value.size == 1) {
                LogUtil.i(TAG, "locate info: ${it.key} -> ${it.value[0]}")
                cache.putString("${it.key}#${qqVersionCode}", it.value[0])
                locateNum++
            } else {
                LogUtil.w(TAG, "locate not instance class: ${it.key} -> ${it.value}")
                cache.putString("${it.key}#${qqVersionCode}", null)
            }
        }
        cache.putStringSet("confuseClasses", confuseInfoKeys)
        ToastUtil.send("dex搜索完毕，成功${locateNum}个，失败${needLocateClasses.size - locateNum}个，耗时${useTime}ms")
    }

    private fun replaceParentClassloader(qClassloader: ClassLoader) {
        val fParent = getField(ClassLoader::class.java, "parent")
        val mClassloader = MainHook::class.java.classLoader
        val parentClassloader = getFieldValue(mClassloader, "parent") as ClassLoader
        try {
            if (XAClassLoader::class.java != parentClassloader.javaClass) {
                LogUtil.d(TAG, "replace parent classloader")
                setFieldValue(mClassloader, fParent, XAClassLoader(qClassloader, parentClassloader))
            }
        } catch (e: Exception) {
            LogUtil.e(TAG, e)
        }
    }

    private fun initHook() {
        LogUtil.d(TAG, "initHook: ->$this")
        for (cls in subHookClasses) {
            try {
                LogUtil.d(TAG, "load hook class -> $cls")
//                loadAs<BaseHook>(cls, Global.moduleClassLoader).new().init()
                cls.new().init()
            } catch (e: Exception) {
                LogUtil.e(TAG, e)
            }
        }
        ToastUtil.send("模块加载完毕")
        LogUtil.i("模块加载完毕")
    }

    private fun isInjector(flag: String): Boolean {
        try {
            if (TextUtils.isEmpty(flag)) return false
            val methodCache: HashMap<String, Method> =
                XposedHelpers::class.java.fieldValueAs("methodCache")
                    ?: return false
            val method: Method = Application::class.java.getMethod("onCreate")
            val key = "$flag#${method.name}"
            if (methodCache.containsKey(key)) {
                return true
            }
            methodCache[key] = method
            return false
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return false
    }
}