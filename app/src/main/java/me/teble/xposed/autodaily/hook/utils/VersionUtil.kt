package me.teble.xposed.autodaily.hook.utils

import me.teble.xposed.autodaily.config.Constants.PACKAGE_NAME_QQ
import me.teble.xposed.autodaily.hook.base.Global
import me.teble.xposed.autodaily.utils.fieldValueAs
import me.teble.xposed.autodaily.utils.getAppVersionName

object VersionUtil {
    val qqVersionName: String = getAppVersionName(Global.hostContext, PACKAGE_NAME_QQ)

    val qqBuildNum: String = QApplicationUtil.application.fieldValueAs("buildNum")!!

    val qqVersionInfo: String = "$qqVersionName,3,$qqBuildNum"

    val qua: String = "V1_AND_SQ_${qqVersionName}_${qqBuildNum}_YYB_D"
}