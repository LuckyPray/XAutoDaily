package me.teble.xposed.autodaily.hook

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.XModuleResources
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.emptyParam
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import com.github.kyuubiran.ezxhelper.utils.isPublic
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.R
import me.teble.xposed.autodaily.config.BaseApplicationImpl
import me.teble.xposed.autodaily.config.DataMigrationService
import me.teble.xposed.autodaily.config.NewRuntime
import me.teble.xposed.autodaily.config.PACKAGE_NAME_SELF
import me.teble.xposed.autodaily.hook.base.BaseHook
import me.teble.xposed.autodaily.hook.base.ProcUtil
import me.teble.xposed.autodaily.hook.base.hostApp
import me.teble.xposed.autodaily.hook.base.hostAppName
import me.teble.xposed.autodaily.hook.base.hostClassLoader
import me.teble.xposed.autodaily.hook.base.hostContext
import me.teble.xposed.autodaily.hook.base.hostInit
import me.teble.xposed.autodaily.hook.base.hostPackageName
import me.teble.xposed.autodaily.hook.base.hostProcessName
import me.teble.xposed.autodaily.hook.base.hostVersionCode
import me.teble.xposed.autodaily.hook.base.injectClassLoader
import me.teble.xposed.autodaily.hook.base.load
import me.teble.xposed.autodaily.hook.base.moduleLoadInit
import me.teble.xposed.autodaily.hook.base.modulePath
import me.teble.xposed.autodaily.hook.base.moduleRes
import me.teble.xposed.autodaily.hook.config.Config
import me.teble.xposed.autodaily.hook.config.Config.confuseInfo
import me.teble.xposed.autodaily.hook.config.Config.hooksVersion
import me.teble.xposed.autodaily.hook.enums.QQTypeEnum
import me.teble.xposed.autodaily.hook.inject.ServletPool
import me.teble.xposed.autodaily.hook.proxy.ProxyManager
import me.teble.xposed.autodaily.hook.proxy.activity.injectRes
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.TaskExecutor
import me.teble.xposed.autodaily.utils.TaskExecutor.CORE_SERVICE_FLAG
import me.teble.xposed.autodaily.utils.TaskExecutor.CORE_SERVICE_TOAST_FLAG
import me.teble.xposed.autodaily.utils.new
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.enums.StringMatchType
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier

class MainHook : IXposedHookLoadPackage, IXposedHookZygoteInit {

//    private lateinit var subHookClasses: Set<String>

    private val subHookClasses: Set<Class<out BaseHook>> = setOf(
        FromServiceMsgHook::class.java,
        QLogHook::class.java,
        QQSettingSettingActivityHook::class.java,
        SplashActivityHook::class.java,
        JumpActivityHook::class.java,
        ToServiceMsgHook::class.java,
        BugHook::class.java,
    )
    private lateinit var mLoadPackageParam: LoadPackageParam
    private lateinit var mStartupParam: IXposedHookZygoteInit.StartupParam
    private var dexIsInit = false

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        mStartupParam = startupParam
        modulePath = startupParam.modulePath
        moduleRes = XModuleResources.createInstance(modulePath, null)
        EzXHelperInit.initZygote(startupParam)
    }

    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        mLoadPackageParam = loadPackageParam
        val packageName = loadPackageParam.packageName
        when {
            packageName == PACKAGE_NAME_SELF -> {
                ModuleHook.hookSelf()
            }

            QQTypeEnum.contain(packageName) -> {
                EzXHelperInit.initHandleLoadPackage(mLoadPackageParam)
                EzXHelperInit.setLogTag("XALog")
                EzXHelperInit.setToastTag("XALog")
            }
        }

        if (QQTypeEnum.contain(loadPackageParam.packageName)) {
            hostPackageName = loadPackageParam.packageName
            hostProcessName = loadPackageParam.processName
            hostClassLoader = loadPackageParam.classLoader

            findMethod(loadPackageParam.classLoader.loadClass(BaseApplicationImpl)) {
                name == "onCreate"
            }.hookBefore {
                if (hostInit) return@hookBefore
                runCatching {
                    hostApp = it.thisObject as Application
                    EzXHelperInit.initAppContext(hostApp)
                    hostClassLoader = hostApp.classLoader
                    if (ProcUtil.isMain) {
                        // MMKV
                        Config.init()
                        injectClassLoader(hostClassLoader)
                        LogUtil.i("qq version -> ${hostAppName}($hostVersionCode)")
                        LogUtil.i("module version -> ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})")
                        LogUtil.d("init ActivityProxyManager")
                        ProxyManager.init
                        filterAndHook()
                    }

                    if (ProcUtil.isMain) {
                        doInit()
                    }
                    if (ProcUtil.isTool) {
                        Log.d("XALog", "tool进程：" + loadPackageParam.processName)
                        toolsHook()
                    }
                }.onFailure {
                    moduleLoadInit = true
                    ToastUtil.send(it.stackTraceToString(), true)
                    Log.e("XALog", it.stackTraceToString())
                    return@hookBefore
                }
            }
        }
    }

    private var hookIsInit: Boolean = false

    private fun toolsHook() {
        val cmdClass: Class<*> by lazy { load(DataMigrationService)!! }
        findMethod(cmdClass) {
            name == "onStartCommand"
        }.hookAfter {
            val args = it.args
            val context = it.thisObject as Service
            val intent = args[0] as Intent?
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                var builder: NotificationCompat.Builder
                val channelId = "me.teble.xposed.autodaily.XA_TOOLS_FOREST_NOTIFY_CHANNEL"
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                    val notificationChannel = NotificationChannel(
                        channelId, "XAutoDaily",
                        NotificationManager.IMPORTANCE_LOW
                    ).apply {
                        enableLights(false)
                        enableVibration(false)
                        setShowBadge(false)
                    }
                    notificationManager.createNotificationChannel(notificationChannel)
                    builder = NotificationCompat.Builder(
                        context,
                        channelId
                    )
                } else {
                    @Suppress("DEPRECATION")
                    builder = NotificationCompat.Builder(context)
                        .setPriority(Notification.PRIORITY_LOW)
                }
                builder = builder.setContentTitle("XAutoDaily")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setOngoing(false)
                    .setShowWhen(true)
                val notification = builder.setContentText("正在唤醒主进程").build()
                context.startForeground(1, notification)
                notificationManager.cancelAll()
            }
            if (intent?.extras?.containsKey(CORE_SERVICE_TOAST_FLAG) == true) {
                Toast.makeText(context, "唤醒测试: true", Toast.LENGTH_SHORT).show()
            }
            if (intent?.extras?.containsKey(CORE_SERVICE_FLAG) == true) {
                (it.thisObject as Service).stopSelf()
            }
        }
    }

    private fun filterAndHook() {
        val enabledHooks = subHookClasses
            .map { it.new() }
            .filter { it.enabled }
        if (enabledHooks.isNotEmpty()) {
            // 加载资源注入
            LogUtil.d("injectRes")
            injectRes(hostContext.resources)
            // dex相关
            LogUtil.d("doDexInit")
            doDexInit()
            //初始化hook
            LogUtil.d("initHook")
            initHook(enabledHooks)
            moduleLoadInit = true
            TaskExecutor.startCorn()
        }
    }

    private fun onStart() {
        ConfUnit.lastModuleVersion = BuildConfig.VERSION_CODE
    }

    private fun findInjectStepMethod(): Method {
        val cl = hostClassLoader
        // classic
        try {
            val kNewRuntime = cl.loadClass(NewRuntime)
            return findMethod(kNewRuntime) { returnType == Boolean::class.java && emptyParam }
        } catch (ignored: ClassNotFoundException) {
            // ignore
        }
        // for NT QQ
        val cKernelInitTask = cl.loadClass("com.tencent.mobileqq.startup.task.KernelInitTask")
        val method = cKernelInitTask.declaredMethods.first {
            it.returnType == Void.TYPE &&
                it.parameterTypes.size == 1 && it.parameterTypes[0] == Context::class.java
        } ?: throw NoSuchMethodException("KernelInitTask.run(Context)")
        method.isAccessible = true
        return method
    }

    private fun doInit() {
        val startupStep = findInjectStepMethod()
        LogUtil.d("startup method: $startupStep")
        startupStep.hookAfter {
            runCatching {
                if (hookIsInit) {
                    return@hookAfter
                }
                // 启动前数据迁移/初始化
                onStart()
                hookIsInit = true
                // 等待hook执行完毕
                while (!moduleLoadInit) {
                    Thread.sleep(100)
                }
            }.onFailure {
                LogUtil.e(it)
                ToastUtil.send("初始化失败: " + it.stackTraceToString())
            }
        }
        startupStep.hookBefore {
            runCatching {
                ServletPool.injectServlet()
            }.onFailure { LogUtil.e(it) }
        }
    }

    private fun doDexInit() {
        val cache = Config.classCache
        // dex解析
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
            if (!cache.contains("${it.key}#${hostVersionCode}")) {
                needLocateClasses.add(it.key)
                LogUtil.d("cache not found: ${it.key}#${hostVersionCode}")
            }
        }
        // 尝试获取，成功则加入新版缓存
        needLocateClasses.removeIf { classSimpleName ->
            LogUtil.d("尝试获取类：$classSimpleName")
            try {
                val cls = hostClassLoader.loadClass(classSimpleName)
                LogUtil.d("尝试获取类成功 -> ${cls.canonicalName}")
                cache.putString("$classSimpleName#${hostVersionCode}", classSimpleName)
                return@removeIf true
            } catch (e: Exception) {
                return@removeIf false
            }
        }
        if (needLocateClasses.isEmpty()) {
            return
        }
        LogUtil.log("needLocateClasses -> $needLocateClasses")
        val startTime = System.currentTimeMillis()
        val info = needLocateClasses.associateWith { confuseInfo[it]!! }
        var locateNum = 0
        try {
            DexKitBridge.create(hostApp.applicationInfo.sourceDir)?.use { bridge ->
                bridge.batchFindClassUsingStrings {
                    info.forEach { (key, valueSet) ->
                        addSearchGroup(key, valueSet.toList(), StringMatchType.SimilarRegex)
                    }
                }.forEach { (key, value) ->
                    LogUtil.d("search result: $key -> ${value.toList()}")
                    if (value.size == 1) {
                        LogUtil.i("locate info: $key -> ${value.first()}")
                        cache.putString("$key#${hostVersionCode}", value.first().descriptor)
                        locateNum++
                    } else {
                        LogUtil.w("locate not instance class: ${value.toList()}")
                        // 保存为空字符串，表示已经搜索过，下次不再搜索
                        cache.putString("${key}#${hostVersionCode}", "")
                    }
                }
            }
        } catch (e: Throwable) {
            LogUtil.e(e)
        }
        val usedTime = System.currentTimeMillis() - startTime
        cache.putStringSet("confuseClasses", confuseInfoKeys)
        LogUtil.d("dex搜索完毕，成功${locateNum}个，失败${needLocateClasses.size - locateNum}个，耗时${usedTime}ms")
        ToastUtil.send("dex搜索完毕，成功${locateNum}个，失败${needLocateClasses.size - locateNum}个，耗时${usedTime}ms")
        dexIsInit = true
    }

    private fun initHook(hooks: List<BaseHook>) {
        hooks.forEach {
            kotlin.runCatching {
                it.init()
            }.onFailure {
                LogUtil.e(it)
            }
        }
        LogUtil.i("模块加载完毕")
    }
}