package me.teble.xposed.autodaily.task.util

import me.teble.xposed.autodaily.utils.TimeUtil
import java.util.*

object CalculationUtil {

    private const val CSRF_TOKEN_END_STR = "tencentQQVIP123443safde&!%^%1282"

    fun getBkn(sKey: String): Int {
        var base = 5381
        for (element in sKey) {
            base += (base shl 5) + element.code
        }
        return base and 2147483647
    }

    fun getPsToken(pskey: String): Int {
        var base = 5381
        for (element in pskey) {
            base += (base shl 5) + element.code
        }
        return base and 2147483647
    }

    fun getCSRFToken(sKey: String): String {
        var cnt = 5381
        val stringBuilder = StringBuilder()
        stringBuilder.append(cnt shl 5)
        for (element in sKey) {
            stringBuilder.append((cnt shl 5) + element.code)
            cnt = element.code
        }
        stringBuilder.append(CSRF_TOKEN_END_STR)
        return ConfigUtil.getMd5Hex(stringBuilder.toString()).lowercase(Locale.getDefault())
    }

    fun getMicrosecondTime(): Long {
        return TimeUtil.cnTimeMillis()
    }

    fun getSecondTime(): Int {
        return (TimeUtil.cnTimeMillis() / 1000).toInt()
    }

    fun getRandom(): Double {
        return Random().nextDouble()
    }
}