package me.teble.xposed.autodaily.task.util

import cn.hutool.core.codec.Base64Decoder
import cn.hutool.core.io.FileUtil
import cn.hutool.core.util.ReUtil
import com.charleskorn.kaml.Yaml
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.hook.base.hostContext
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.task.cron.pattent.CronPattern
import me.teble.xposed.autodaily.task.cron.pattent.CronPatternUtil
import me.teble.xposed.autodaily.task.model.MetaInfo
import me.teble.xposed.autodaily.task.model.Task
import me.teble.xposed.autodaily.task.model.TaskProperties
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.ui.ConfUnit.lastFetchTime
import me.teble.xposed.autodaily.ui.ConfUnit.metaInfoCache
import me.teble.xposed.autodaily.ui.enable
import me.teble.xposed.autodaily.ui.lastExecTime
import me.teble.xposed.autodaily.ui.nextShouldExecTime
import me.teble.xposed.autodaily.utils.*
import java.io.File
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import java.util.regex.Pattern
import javax.crypto.Cipher


object ConfigUtil {
    /**
     * 获取配置版本列表：https://data.jsdelivr.com/v1/package/gh/teble/XAutoDaily-Conf
     * {"tags": [], "versions": ["1"]}
     *
     * 配置文件下载链接：https://cdn.jsdelivr.net/gh/teble/XAutoDaily-Conf@1/xa_conf
     */

    private val confDir = File(hostContext.filesDir, "xa_conf")
    private val MIN_APP_VERSION_REG = Pattern.compile("minAppVersion:\\s+(\\d+)")
    private val CONFIG_VERSION_REG = Pattern.compile("version:\\s+(\\d+)")

    private val lock = ReentrantLock()
    private var _conf: TaskProperties? = null

    private external fun decryptXAConf(encConfBytes: ByteArray): ByteArray

    external fun getMd5Hex(value: String): String

    @JvmStatic
    @Throws(java.lang.Exception::class)
    fun decryptByPublicKey(
        encrypted: ByteArray,
        publicKey: ByteArray
    ): ByteArray {
        try {
            val keySpec = X509EncodedKeySpec(Base64Decoder.decode(publicKey))
            val keyFactory = KeyFactory.getInstance("RSA")
            val publicK = keyFactory.generatePublic(keySpec)
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.DECRYPT_MODE, publicK)
            return cipher.doFinal(encrypted)
        } catch (e: Exception) {
            LogUtil.e(e)
            throw e
        }
    }

    fun checkUpdate(showToast: Boolean): Boolean {
        val info = fetchMeta()
        info?.let {
            val currConfVer = loadSaveConf().version
            if (currConfVer < info.config.version) {
                if (BuildConfig.VERSION_CODE >= info.config.needAppVersion) {
                    updateConfig(showToast)
                } else {
//                    XANotification.notify("插件版本过低，无法应用最新配置，推荐更新插件")
                }
            }
            if (BuildConfig.VERSION_CODE < info.app.versionCode) {
                if (showToast) {
                    ToastUtil.send("插件版本存在更新")
                }
                return true
            }
        }
        if (showToast) {
            ToastUtil.send("当前插件与配置均是最新版本")
        }
        return false
    }

    private fun updateConfig(showToast: Boolean): Boolean {
        try {
            val encRes = RepoFileLoader.load(FileEnum.CONF) ?: return false
            val res = decodeConfStr(encRes)
            res ?: let {
                LogUtil.w("配置文件获取失败")
                return false
            }
            val minAppVersion = ReUtil.getGroup1(MIN_APP_VERSION_REG, res).toInt()
            val confVersion = ReUtil.getGroup1(CONFIG_VERSION_REG, res).toInt()
            if (minAppVersion > BuildConfig.VERSION_CODE) {
                if (showToast) {
                    ToastUtil.send("插件版本号低于${minAppVersion}，无法使用v${confVersion}版本的配置", true)
                }
                LogUtil.i("插件版本号低于${minAppVersion}，无法使用v${confVersion}版本的配置")
                return false
            }
            if (confVersion <= loadSaveConf().version) {
                if (showToast) {
                    ToastUtil.send("当前配置已是最新，无需更新")
                }
                return false
            }
            saveConfFile(encRes, confVersion)
            ConfUnit.needShowUpdateLog = true
            ToastUtil.send("配置文件更新完毕，如有选项更新，请前往配置目录进行勾选")
            return true
        } catch (e: Exception) {
            LogUtil.e(e)
            ToastUtil.send("更新配置文件失败，详情请看日志")
            return false
        }
    }

    private fun decodeConfStr(encodeConfStr: String): String? {
        LogUtil.d("conf length -> ${encodeConfStr.length}")
        val res = decryptXAConf(encodeConfStr.toByteArray())
        if (res.isEmpty()) {
            LogUtil.w("解密配置文件失败，请检查插件是否为最新版本")
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
            return true
        } catch (e: Exception) {
            LogUtil.e(e)
            ToastUtil.send("保存配置文件失败，详情请看日志")
            return false
        } finally {
            // clear cache
            _conf = null
            LogUtil.d("clear conf cache")
        }
    }

    private fun getDefaultConf(): String {
        return getTextFromModuleAssets("default_conf")
    }

    fun loadSaveConf(): TaskProperties {
        if (_conf != null) {
            return _conf as TaskProperties
        }
        var conf: TaskProperties? = null
        val propertiesFile = File(confDir, "xa_conf")
        if (propertiesFile.exists()) {
            conf = loadConf(FileUtil.readUtf8String(propertiesFile))
        }
        val encodeConfig = getDefaultConf()
        val defaultConf = loadConf(encodeConfig)
        defaultConf ?: let {
            LogUtil.w("加载默认配置失败")
            ToastUtil.send("加载默认配置失败")
            return TaskProperties(0, 0, listOf(), listOf())
        }
        if (conf == null) {
            conf = defaultConf
            ToastUtil.send("配置文件不存在/加载失败，正在解压默认配置，详情请看日志")
            LogUtil.i("defaultConf version -> ${defaultConf.version}")
            saveConfFile(encodeConfig, defaultConf.version)
            ConfUnit.needShowUpdateLog = true
        } else {
            // 配置正常加载，表示配置版本与当前model一致，判断内置文件版本是否需要覆盖当前配置
            // 内置配置版本与本地配置版本一致的情况，如果hashcode不一致，内置覆盖本地
            // 内置版本 > 本地配置版本，内置覆盖本地
            if (conf.version <= defaultConf.version && conf != defaultConf) {
                if (conf.version < defaultConf.version) {
                    ConfUnit.needShowUpdateLog = true
                }
                conf = defaultConf
                saveConfFile(encodeConfig, defaultConf.version)
                ToastUtil.send("正在解压内置配置文件，如有选项更新，请前往配置目录进行勾选")
            }
        }
        _conf = conf
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
            LogUtil.e(e)
//            ToastUtil.send("配置加载失败，请检查插件是否为最新版本", true)
        }
        return null
    }

    private fun readMinVersion(confStr: String): Int {
        var minAppVersion = 0
        val str = ReUtil.getGroup1(MIN_APP_VERSION_REG, confStr)
        if (str.isNotEmpty()) {
            minAppVersion = str.toInt()
        }
        return minAppVersion
    }

    fun getCurrentExecTaskNum(): Int {
        var num = 0
        val conf = loadSaveConf()
        val nowStr = TimeUtil.getCNDate().format().substring(0, 10)
        conf.taskGroups.forEach { taskGroup ->
            taskGroup.tasks.forEach { task ->
                val time = task.lastExecTime ?: ""
                if (time.startsWith(nowStr)) {
                    num++
                }
            }
        }
        return num
    }

    fun fetchMeta(): MetaInfo? {
        val text: String? = RepoFileLoader.load(FileEnum.META)
        LogUtil.d("fetch meta -> $text")
        val meta = runCatching {
            text?.parse<MetaInfo>()
        }.onFailure {
            LogUtil.e(it, "解析meta失败：")
        }.getOrNull()
        metaInfoCache = meta
        lastFetchTime = System.currentTimeMillis()
        return meta
    }

    fun checkExecuteTask(task: Task): Boolean {
        val enabled = task.enable
        if (!enabled) {
            return false
        }
        // 获取上次执行任务时间
        val lastExecTime = parseDate(task.lastExecTime)
        // 不保证一定在有效时间内执行
        val now = TimeUtil.getCNDate()
        val nextShouldExecTime =
            task.nextShouldExecTime ?: let {
                val realCron = TaskUtil.getRealCron(task)
                ///////////////////////////////////////////////////////////////////////////////////////
                val currDate = Date(TimeUtil.cnTimeMillis() + 1)
                val time = CronPatternUtil.nextDateAfter(
                    TimeZone.getTimeZone("GMT+8"),
                    CronPattern(realCron),
                    currDate,
                    true)!!
                task.nextShouldExecTime = Date(time.time - TimeUtil.offsetTime).format()
                Date(time.time + TimeUtil.offsetTime).format()
            }
        lastExecTime ?: let {
            // 第一次执行任务，如果下次执行时间不在当天，则立即执行
            if (task.nextShouldExecTime?.substring(0, 10) != now.format().substring(0, 10)) {
                return true
            }
        }
        if (nextShouldExecTime <= now.format()) {
            return true
        }
        return false
    }
}
