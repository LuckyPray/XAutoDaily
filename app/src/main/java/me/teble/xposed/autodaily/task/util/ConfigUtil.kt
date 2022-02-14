package me.teble.xposed.autodaily.task.util

import android.annotation.SuppressLint
import android.util.Log
import cn.hutool.core.io.FileUtil
import cn.hutool.core.util.ReUtil
import cn.hutool.http.HttpUtil
import com.charleskorn.kaml.Yaml
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.config.Constants.NOTICE
import me.teble.xposed.autodaily.config.Constants.XA_API_URL
import me.teble.xposed.autodaily.hook.base.Global
import me.teble.xposed.autodaily.hook.config.Config.accountConfig
import me.teble.xposed.autodaily.hook.config.Config.xaConfig
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.task.model.PackageData
import me.teble.xposed.autodaily.task.model.Result
import me.teble.xposed.autodaily.task.model.TaskProperties
import me.teble.xposed.autodaily.task.model.VersionInfo
import me.teble.xposed.autodaily.task.util.Const.CHANGE_SIGN_BUTTON
import me.teble.xposed.autodaily.task.util.Const.CONFIG_VERSION
import me.teble.xposed.autodaily.task.util.Const.MOST_RECENT_EXEC_TIME
import me.teble.xposed.autodaily.ui.Cache.configVer
import me.teble.xposed.autodaily.ui.Cache.lastFetchTime
import me.teble.xposed.autodaily.ui.Cache.versionInfoCache
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.NativeUtil
import me.teble.xposed.autodaily.utils.getTextFromModuleAssets
import me.teble.xposed.autodaily.utils.parse
import java.io.File
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import java.util.regex.Pattern

object ConfigUtil {
    /**
     * 获取配置版本列表：https://data.jsdelivr.com/v1/package/gh/teble/XAutoDaily-Conf
     * {"tags": [], "versions": ["1"]}
     *
     * 配置文件下载链接：https://cdn.jsdelivr.net/gh/teble/XAutoDaily-Conf@1/xa_conf
     */
    private const val TAG = "PropUtil"
    private val confDir = File(Global.hostContext.filesDir, "xa_conf")
    private val MIN_APP_VERSION_REG = Pattern.compile("minAppVersion:\\s+(\\d+)")
    private val CONFIG_VERSION_REG = Pattern.compile("version:\\s+(\\d+)")

    private val lock = ReentrantLock()

    init {
        loadLib()
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    fun loadLib() {
        val xaLibDir = File(Global.hostContext.filesDir, "xa_lib")
        if (xaLibDir.isFile) {
            xaLibDir.delete()
        }
        if (!xaLibDir.exists()) {
            xaLibDir.mkdirs()
        }
        val soFilePath = NativeUtil.getNativeLibrary(Global.hostContext, "xa_native").absolutePath
        Log.d(TAG, "loadLib: $soFilePath")
        System.load(soFilePath)
    }

    /**
     * 部分机型变量进入jni后字符丢失，这里分批传入，尝试避免单个变量11341个字符之后丢失的问题
     */
    private external fun decryptXAConf(encConfBytes: ByteArray): ByteArray

    external fun getTencentDigest(value: String): String

    fun checkConfigUpdate(currentConfigVersion: Int): String? {
        try {
            ToastUtil.send("正在检测更新")
            LogUtil.i("正在检测更新")
            val res = HttpUtil.get("https://data.jsdelivr.com/v1/package/gh/teble/XAutoDaily-Conf")
            val packageData: PackageData = res.parse()
            if (packageData.versions.isNotEmpty() && currentConfigVersion < packageData.versions[0].toInt()) {
                return packageData.versions[0]
            }
        } catch (e: Exception) {
            LogUtil.e(TAG, e)
        }
        return null
    }

    fun updateCdnConfig(newConfVersion: Int): Boolean {
        val encRes =
            HttpUtil.get("https://cdn.jsdelivr.net/gh/teble/XAutoDaily-Conf@${newConfVersion}/xa_conf")
        val res = decodeConfStr(encRes)
        val minAppVersion = ReUtil.getGroup1(MIN_APP_VERSION_REG, res).toInt()
        if (minAppVersion > BuildConfig.VERSION_CODE) {
            ToastUtil.send("插件版本号低于${minAppVersion}，无法使用v${newConfVersion}版本的配置", true)
            LogUtil.i(TAG, "插件版本号低于${minAppVersion}，无法使用v${newConfVersion}版本的配置")
            return false
        }
        saveConfFile(encRes, newConfVersion)
        ToastUtil.send("配置文件更新完毕，如有选项更新，请前往配置目录进行勾选")
        return true
    }

    fun checkUpdate(showToast: Boolean): Boolean {
        val info = fetchUpdateInfo()
        info?.let {
            val currConfVer = xaConfig.getInt(CONFIG_VERSION, 1)
            if (BuildConfig.VERSION_CODE < info.appVersion) {
                if (showToast) {
                    ToastUtil.send("插件版本存在更新")
                    return true
                }
            }
            if (currConfVer < info.confVersion) {
                if (BuildConfig.VERSION_CODE >= info.minAppVersion) {
                    updateConfig(info.confUrl, showToast)
                } else {
                    ToastUtil.send("插件版本过低无法更新配置")
                }
                return false
            }
        } ?: let {
            if (showToast) {
                ToastUtil.send("检查更新失败")
            }
        }
        if (showToast) {
            ToastUtil.send("当前插件与配置均是最新版本")
        }
        return false
    }

    fun updateConfig(confUrl: String, showToast: Boolean): Boolean {
        try {
            val encRes = HttpUtil.get(confUrl)
            val res = decodeConfStr(encRes)
            val minAppVersion = ReUtil.getGroup1(MIN_APP_VERSION_REG, res).toInt()
            val confVersion = ReUtil.getGroup1(CONFIG_VERSION_REG, res).toInt()
            if (minAppVersion > BuildConfig.VERSION_CODE) {
                if (showToast) {
                    ToastUtil.send("插件版本号低于${minAppVersion}，无法使用v${confVersion}版本的配置", true)
                }
                LogUtil.i(TAG, "插件版本号低于${minAppVersion}，无法使用v${confVersion}版本的配置")
                return false
            }
            if (confVersion <= xaConfig.getInt(CONFIG_VERSION, 0)) {
                if (showToast) {
                    ToastUtil.send("当前配置已是最新，无需更新")
                }
                return false
            }
            saveConfFile(encRes, confVersion)
            ToastUtil.send("配置文件更新完毕，如有选项更新，请前往配置目录进行勾选")
            return true
        } catch (e: Exception) {
            LogUtil.e(TAG, e)
            ToastUtil.send("更新配置文件失败，详情请看日志")
            return false
        }
    }

    fun decodeConfStr(encodeConfStr: String): String? {
        val res = decryptXAConf(encodeConfStr.toByteArray())
        if (res.isEmpty()) {
            LogUtil.w(TAG, "解密配置文件失败，请检查插件是否为最新版本")
            return null
        }
        return String(res)
    }

    private fun saveConfFile(encConfStr: String, configVersion: Int): Boolean {
        try {
            if (confDir.isFile) {
                confDir.delete()
            }
            if (!confDir.exists()) {
                confDir.mkdirs()
            }
            val propertiesFile = File(confDir, "xa_conf")
            if (!propertiesFile.exists()) {
                propertiesFile.createNewFile()
            }
            FileUtil.writeUtf8String(encConfStr, propertiesFile)
            configVer = configVersion
            xaConfig.putInt(CONFIG_VERSION, configVersion)
            return true
        } catch (e: Exception) {
            LogUtil.e(TAG, e)
            ToastUtil.send("保存配置文件失败，详情请看日志")
            return false
        }
    }

    fun getDefaultConf(): String {
        return getTextFromModuleAssets("default_conf")
    }

    fun loadSaveConf(): TaskProperties {
        var conf: TaskProperties? = null
        val propertiesFile = File(confDir, "xa_conf")
        if (propertiesFile.exists()) {
            conf = loadConf(FileUtil.readUtf8String(propertiesFile))
        }
        val encodeConfig = getDefaultConf()
        val defaultConf = loadConf(encodeConfig)!!
        if (conf == null) {
            conf = defaultConf
            ToastUtil.send("配置文件不存在/加载失败，正在解压默认配置，详情请看日志")
            LogUtil.d(TAG, "defaultConf version -> ${defaultConf.version}")
            saveConfFile(encodeConfig, defaultConf.version)
        } else {
            // 配置正常加载，表示配置版本与当前model一致，判断内置文件版本是否需要覆盖当前配置
            // 内置配置版本与本地配置版本一致的情况，如果hashcode不一致，内置覆盖本地
            // 内置版本 > 本地配置版本，内置覆盖本地
            if (conf.version <= defaultConf.version && conf != defaultConf) {
                conf = defaultConf
                saveConfFile(encodeConfig, defaultConf.version)
                ToastUtil.send("正在解压内置配置文件，如有选项更新，请前往配置目录进行勾选")
            }
        }
        return conf
    }

    private fun loadConf(encodeConfStr: String): TaskProperties? {
        try {
            decodeConfStr(encodeConfStr)?.let { decodeConfStr ->
                val version = readMinVersion(decodeConfStr)
                if (version > BuildConfig.VERSION_CODE) {
                    LogUtil.i("插件版本过低，无法加载配置。配置要求最低插件版本: ${version}，当前插件版本: ${BuildConfig.VERSION_CODE}")
//                    ToastUtil.send("插件版本过低，无法加载配置。配置要求最低插件版本: ${version}，当前插件版本: ${BuildConfig.VERSION_CODE}", true)
                    return null
                }
                // 版本不对应抛出异常
                return Yaml.default.decodeFromString(TaskProperties.serializer(), decodeConfStr)
            }
        } catch (e: Exception) {
            LogUtil.e(TAG, e)
//            ToastUtil.send("配置加载失败，请检查插件是否为最新版本", true)
        }
        return null
    }

    fun readMinVersion(confStr: String): Int {
        var minAppVersion = 0
        val str = ReUtil.getGroup1(MIN_APP_VERSION_REG, confStr)
        if (str.isNotEmpty()) {
            minAppVersion = str.toInt()
        }
        return minAppVersion
    }

    fun saveAndCheckMostRecentExecTime(date: Date) {
        lock.lock()
        val nextTime = xaConfig.getLong(MOST_RECENT_EXEC_TIME, Long.MAX_VALUE)
        val currentTime = Date().time
        // 缓存的下次执行时间已经过了，表示该任务已经准备执行了，或者执行完毕了
        if (nextTime < currentTime
            || date.time < nextTime
        ) {
            xaConfig.putLong(MOST_RECENT_EXEC_TIME, date.time)
        }
        lock.unlock()
    }

    fun getMostRecentExecTime(): Long {
        return xaConfig.getLong(MOST_RECENT_EXEC_TIME, -1)
    }

    fun changeSignButton(key: String, boolean: Boolean) {
        accountConfig.putBoolean(key, boolean)
        accountConfig.putBoolean(CHANGE_SIGN_BUTTON, true)
    }

    fun getCurrentExecTaskNum(): Int {
        var num = 0
        val conf = loadSaveConf()
        val nowStr = Date().format().split(' ').first()
        conf.taskGroups.forEach { taskGroup ->
            taskGroup.tasks.forEach { task ->
                val time = accountConfig.getString("${task.id}#${Const.LAST_EXEC_TIME}") ?: ""
                if (time.startsWith(nowStr)) {
                    num++
                }
            }
        }
        return num
    }

    fun fetchUpdateInfo(): VersionInfo? {
        return try {
            val text = HttpUtil.get("$XA_API_URL$NOTICE")
            LogUtil.d(TAG, "getNotice -> $text")
            val res: Result = text.parse()
            versionInfoCache = res.data
            lastFetchTime = System.currentTimeMillis()
            return res.data
        } catch (e: Exception) {
            LogUtil.e(e, "拉取公告失败：")
            null
        }
    }
}
