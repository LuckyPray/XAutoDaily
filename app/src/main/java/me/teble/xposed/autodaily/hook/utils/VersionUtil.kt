package me.teble.xposed.autodaily.hook.utils

import me.teble.xposed.autodaily.hook.base.hostVersionName
import me.teble.xposed.autodaily.utils.fieldValueAs

object VersionUtil {
    val qqVersionName: String = hostVersionName

    val qqBuildNum: String = QApplicationUtil.application.fieldValueAs("buildNum")!!

    val qqVersionInfo: String = "$qqVersionName,3,$qqBuildNum"

    val qua: String = "V1_AND_SQ_${qqVersionName}_${qqBuildNum}_YYB_D"
}