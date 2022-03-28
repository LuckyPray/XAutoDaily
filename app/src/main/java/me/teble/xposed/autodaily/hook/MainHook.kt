package me.teble.xposed.autodaily.hook

import android.app.AndroidAppHelper.currentApplication
import android.app.Application
import android.content.res.XModuleResources
import android.text.TextUtils
import cn.hutool.core.util.ReflectUtil.*
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.emptyParam
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.config.QQClasses.Companion.NewRuntime
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
import me.teble.xposed.autodaily.hook.proxy.ProxyManager
import me.teble.xposed.autodaily.hook.proxy.activity.ResInjectUtil
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.fieldValueAs
import me.teble.xposed.autodaily.utils.new
import java.lang.reflect.Method
import kotlin.collections.set

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
        this.loadPackageParam = loadPackageParam
        if (QQTypeEnum.contain(loadPackageParam.packageName)) {
            // TODO 分进程处理
            if (loadPackageParam.processName == loadPackageParam.packageName) {
                init
            }
        }
    }

    val init by lazy {
        LogUtil.d("current process: ${loadPackageParam.processName}")
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

        findMethod(NewRuntime) { returnType == Boolean::class.java && emptyParam }.hookAfter {
            // 防止hook多次被执行
            try {
                synchronized(this) {
                    if (isInjector(this::class.java.name)) {
                        return@hookAfter
                    }
                }
                val context = currentApplication()
                // 初始化全局Context
                initContext(context)
                LogUtil.i("qq version -> ${Global.qqTypeEnum.appName}($qqVersionCode)")
                LogUtil.i("module version -> ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})")
                EzXHelperInit.initAppContext(context)
                // MMKV
                Config.init()
                //加载资源注入
                LogUtil.d("injectRes")
                ResInjectUtil.injectRes(context.resources)
                LogUtil.d("init ActivityProxyManager")
                ProxyManager.init
                // dex相关
                LogUtil.d("doDexInit")
                doDexInit()
                //初始化hook
                LogUtil.d("initHook")
                initHook()
            } catch (e: Throwable) {
                LogUtil.e(e)
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
            LogUtil.d("清空Hooks缓存")
            cache.clearAll()
            cache.putInt("hooksVersion", hooksVersion)
        }
        confuseInfo.forEach {
            val key = "${it.key}#hash"
            val hash = it.value.hashCode()
            val cacheHash = cache.getInt(key, 0)
            LogUtil.d("${it.key} cacheHash -> $cacheHash")
            LogUtil.d("${it.key} hash -> $hash")
            // 加入修改了特征的类
            if (cacheHash != hash) {
                cache.putInt(key, hash)
                needLocateClasses.add(it.key)
                LogUtil.d("need locate -> ${it.key}")
            }
            // 加入没有被搜索过的类
            if (!cache.contains("${it.key}#${qqVersionCode}")) {
                needLocateClasses.add(it.key)
                LogUtil.d("cache not found: ${it.key}#${qqVersionCode}")
            }
        }
        // 尝试获取，成功则加入新版缓存
        needLocateClasses.removeIf { classSimpleName ->
            LogUtil.d("尝试获取类：$classSimpleName")
            try {
                val cls = hostClassLoader.loadClass(classSimpleName)
                LogUtil.d("尝试获取类成功 -> ${cls.canonicalName}")
                cache.putString("$classSimpleName#${qqVersionCode}", classSimpleName)
                return@removeIf true
            } catch (e: Exception) {
                return@removeIf false
            }
        }
        if (needLocateClasses.isEmpty()) {
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
                LogUtil.i("locate info: ${it.key} -> ${it.value[0]}")
                cache.putString("${it.key}#${qqVersionCode}", it.value[0])
                locateNum++
            } else {
                LogUtil.w("locate not instance class: ${it.key} -> ${it.value}")
                // 保存为空字符串，表示已经搜索过，下次不再搜索
                cache.putString("${it.key}#${qqVersionCode}", "")
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
                LogUtil.d("replace parent classloader")
                setFieldValue(mClassloader, fParent, XAClassLoader(qClassloader, parentClassloader))
            }
        } catch (e: Exception) {
            LogUtil.e(e)
        }
    }

    private fun initHook() {
        for (cls in subHookClasses) {
            try {
//                loadAs<BaseHook>(cls, Global.moduleClassLoader).new().init()
                cls.new().init()
            } catch (e: Exception) {
                LogUtil.e(e)
            }
        }
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