package me.teble.xposed.autodaily.hook.enums

import me.teble.xposed.autodaily.config.Constants

enum class QQTypeEnum(
    val packageName: String,
    val appName: String
) {
    QQ(Constants.PACKAGE_NAME_QQ, "QQ"),
    TIM(Constants.PACKAGE_NAME_TIM, "TIM"),
    INTERNATIONAL(Constants.PACKAGE_NAME_QQ_INTERNATIONAL, "QQ国际版"),
    LITE(Constants.PACKAGE_NAME_QQ_LITE, "QQ极速版");

    companion object {
        fun valueOfPackage(packageName: String): QQTypeEnum {
            val qqTypeEnums = values()
            for (qqTypeEnum in qqTypeEnums) {
                if (qqTypeEnum.packageName == packageName) {
                    return qqTypeEnum
                }
            }
            throw UnSupportQQTypeException("不支持的包名")
        }

        fun contain(packageName: String): Boolean {
            val qqTypeEnums = values()
            for (qqTypeEnum in qqTypeEnums) {
                if (qqTypeEnum.packageName == packageName) {
                    return true
                }
            }
            return false
        }
    }
}