package me.teble.xposed.autodaily.task.util

import cn.hutool.core.codec.Base64Decoder
import cn.hutool.core.util.ReUtil
import com.charleskorn.kaml.Yaml
import kotlinx.collections.immutable.persistentListOf
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.hook.base.hostContext
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.task.cron.pattent.CronPattern
import me.teble.xposed.autodaily.task.cron.pattent.CronPatternUtil
import me.teble.xposed.autodaily.task.model.AppMeta
import me.teble.xposed.autodaily.task.model.MetaInfo
import me.teble.xposed.autodaily.task.model.Task
import me.teble.xposed.autodaily.task.model.TaskProperties
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.ui.ConfUnit.lastFetchTime
import me.teble.xposed.autodaily.ui.ConfUnit.metaInfoCache
import me.teble.xposed.autodaily.ui.enable
import me.teble.xposed.autodaily.ui.lastExecTime
import me.teble.xposed.autodaily.ui.nextShouldExecTime
import me.teble.xposed.autodaily.utils.FileEnum
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.RepoFileLoader
import me.teble.xposed.autodaily.utils.TaskScheduleEvents
import me.teble.xposed.autodaily.utils.TimeUtil
import me.teble.xposed.autodaily.utils.getTextFromModuleAssets
import me.teble.xposed.autodaily.utils.parse
import me.teble.xposed.autodaily.utils.runRetry
import java.io.File
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.Date
import java.util.TimeZone
import java.util.concurrent.locks.ReentrantLock
import java.util.regex.Pattern
import javax.crypto.Cipher
import kotlin.concurrent.withLock


object ConfigUtil {
    /**
     * 获取配置版本列表：https://data.jsdelivr.com/v1/package/gh/teble/XAutoDaily-Conf
     * {"tags": [], "versions": ["1"]}
     *
     * 配置文件下载链接：https://cdn.jsdelivr.net/gh/teble/XAutoDaily-Conf@1/xa_conf
     */

    private val confDir = File(hostContext.filesDir, "xa_conf")
    private val MIN_APP_VERSION_REG = Pattern.compile("minAppVersion:\\s+(\\d+)")

    private val lock = ReentrantLock()
    private var _conf: TaskProperties? = null

    private external fun decryptXAConf(encConfBytes: ByteArray): ByteArray

    external fun getMd5Hex(value: String): String

    @JvmStatic
    @Throws(Exception::class)
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

    data class UpdateResult(val configVersion: Int, val appUpdate: AppMeta?, val message: String)

    /**
     * 检查模块版本，并应用当前模块支持的新配置。
     *
     * @param notifyConfigUpdate 后台检查时通过系统提示告知配置更新结果
     * @return 本次检查结果，获取版本信息失败时返回 null
     */
    fun checkUpdate(notifyConfigUpdate: Boolean = true): UpdateResult? {
        val info = fetchMeta() ?: return null
        val appUpdate = info.app.takeIf { it.versionCode > BuildConfig.VERSION_CODE }
        val message = when {
            loadSaveConf().version >= info.config.version ->
                if (appUpdate == null) "模块和配置均已是最新版本" else "发现模块新版本 ${appUpdate.versionName}"
            info.config.needAppVersion > BuildConfig.VERSION_CODE ->
                "请先更新模块，再使用配置 v${info.config.version}"
            else -> updateConfig(info.config.version, notifyConfigUpdate)
        }
        return UpdateResult(loadSaveConf().version, appUpdate, message)
    }

    private fun updateConfig(expectedVersion: Int, notify: Boolean): String {
        return try {
            val encoded = RepoFileLoader.load(FileEnum.CONF) ?: return "下载配置失败，请稍后重试"
            val config = loadConf(encoded) ?: return "无法加载下载的配置，请查看日志"
            if (config.version < expectedVersion) {
                LogUtil.w("配置版本尚未同步，预期 v$expectedVersion，实际 v${config.version}")
                return "配置 v$expectedVersion 尚未同步，请稍后重试"
            }
            lock.withLock {
                val currentVersion = loadSaveConf().version
                if (config.version <= currentVersion) return "当前配置已是 v$currentVersion"
                saveConfFile(encoded, config)
                ConfUnit.needShowUpdateLog = true
            }
            val message = "配置已更新至 v${config.version}"
            LogUtil.i(message)
            if (notify) ToastUtil.send(message)
            message
        } catch (e: Exception) {
            LogUtil.e(e, "更新配置失败")
            val message = "更新配置失败，请查看日志"
            if (notify) ToastUtil.send(message)
            message
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

    private fun saveConfFile(encConfStr: String, config: TaskProperties) {
        ConfUnit.encConfStr = encConfStr
        _conf = config
        TaskScheduleEvents.changed()
    }

    private fun getDefaultConf(): String {
        return getTextFromModuleAssets("default_conf")
    }

    fun loadSaveConf(): TaskProperties = lock.withLock {
        _conf?.let { return@withLock it }
        var conf: TaskProperties? = loadConf()
        val encodeConfig = getDefaultConf()
        val defaultConf = loadConf(encodeConfig)
        defaultConf ?: let {
            LogUtil.w("加载默认配置失败")
            ToastUtil.send("加载默认配置失败")
            return@withLock TaskProperties(0, 0, persistentListOf(), persistentListOf())
        }
        if (conf == null) {
            conf = defaultConf
            ToastUtil.send("配置文件不存在/加载失败，正在解压默认配置，详情请看日志")
            LogUtil.i("defaultConf version -> ${defaultConf.version}")
            saveConfFile(encodeConfig, defaultConf)
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
                saveConfFile(encodeConfig, defaultConf)
                ToastUtil.send("正在解压内置配置文件，如有选项更新，请前往配置目录进行勾选")
            }
        }
        _conf = conf
        conf
    }

    private fun loadConf(): TaskProperties? {
        return ConfUnit.encConfStr?.let {
            loadConf(it)
        }
    }

    private fun loadConf(encodeConfStr: String): TaskProperties? {
        return runRetry(3, 20) {
            decodeConfStr(encodeConfStr)?.let { decodeConfStr ->
                val version = readMinVersion(decodeConfStr)
                if (version > BuildConfig.VERSION_CODE) {
                    LogUtil.i("插件版本过低，无法加载配置。配置要求最低插件版本: ${version}，当前插件版本: ${BuildConfig.VERSION_CODE}")
//                    ToastUtil.send("插件版本过低，无法加载配置。配置要求最低插件版本: ${version}，当前插件版本: ${BuildConfig.VERSION_CODE}", true)
                    return@let null
                }
                // 版本不对应抛出异常
                return@let Yaml.default.decodeFromString(TaskProperties.serializer(), decodeConfStr)
            }
        }
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
        if (meta != null) {
            metaInfoCache = meta
            lastFetchTime = System.currentTimeMillis()
        }
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
        return nextShouldExecTime <= now.format()
    }
}
