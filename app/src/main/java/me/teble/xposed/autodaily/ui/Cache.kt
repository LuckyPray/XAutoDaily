package me.teble.xposed.autodaily.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.teble.xposed.autodaily.hook.base.Global
import me.teble.xposed.autodaily.hook.config.ConfProxy
import me.teble.xposed.autodaily.hook.config.Config.accountConfig
import me.teble.xposed.autodaily.hook.config.Config.xaConfig
import me.teble.xposed.autodaily.task.model.VersionInfo
import me.teble.xposed.autodaily.task.util.Const.CONFIG_VERSION
import me.teble.xposed.autodaily.task.util.Const.NEED_SHOW_LOG

object Cache {
    var qqVersionName: String by mutableStateOf(
        if (Global.isInit()) Global.qqVersionName else "qq-version"
    )
    var qqVersionCode: Long by mutableStateOf(
        if (Global.isInit()) Global.qqVersionCode else 9999L
    )
    var configVersion: Int by mutableStateOf(
        xaConfig.getInt(CONFIG_VERSION, 1)
    )
    var currConf: ConfProxy by mutableStateOf(
        accountConfig
    )
    var needUpdate: Boolean = false
    var needShowUpdateLog: Boolean
        get() = xaConfig.getBoolean(NEED_SHOW_LOG, false)
        set(value) = xaConfig.putBoolean(NEED_SHOW_LOG, value)

    var lastFetchTime: Long = 0
    var versionInfoCache: VersionInfo? = null
}