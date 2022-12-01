package me.teble.xposed.autodaily.hook.utils

import me.teble.xposed.autodaily.hook.base.hostContext
import me.teble.xposed.autodaily.hook.base.hostPackageName
import me.teble.xposed.autodaily.hook.base.hostVersionName
import me.teble.xposed.autodaily.hook.enums.QQTypeEnum
import me.teble.xposed.autodaily.utils.fieldValueAs

object VersionUtil {
    val qqVersionName: String = hostVersionName

    val qqBuildNum: String = QApplicationUtil.application.fieldValueAs("buildNum")!!

    val qqVersionInfo: String = "$qqVersionName,3,$qqBuildNum"

    val qua: String = "V1_AND_SQ_${qqVersionName}_${qqBuildNum}_YYB_D"

    val quaVersion = buildString {
        val type = QQTypeEnum.valueOfPackage(hostPackageName)
        if (type == QQTypeEnum.TIM) {
            append("TIM")
            append("/$qqVersionName.$qqBuildNum")
        } else if (type == QQTypeEnum.QQ) {
            append("QQ")
            append("/$qqVersionName.$qqBuildNum")
        } else {
            append("QQ/8.9.15.9425")
        }
    }
}