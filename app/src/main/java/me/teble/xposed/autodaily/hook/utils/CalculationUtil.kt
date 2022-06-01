package me.teble.xposed.autodaily.hook.utils

import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.TimeUtil
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

object CalculationUtil {
    private const val TAG = "CalculationUtil"
    private const val CSRF_TOKEN_END_STR = "tencentQQVIP123443safde&!%^%1282"

    private fun getBkn(sKey: String): Int {
        var base = 5381
        for (element in sKey) {
            base += (base shl 5) + element.code
        }
        return base and 2147483647
    }

    private fun getPsToken(pskey: String): Int {
        var base = 5381
        for (element in pskey) {
            base += (base shl 5) + element.code
        }
        return base and 2147483647
    }

    fun getCSRFToken(sKey: String): String? {
        var cnt = 5381
        val stringBuilder = StringBuilder()
        stringBuilder.append(cnt shl 5)
        for (element in sKey) {
            stringBuilder.append((cnt shl 5) + element.code)
            cnt = element.code
        }
        stringBuilder.append(CSRF_TOKEN_END_STR)
        return getMD5(stringBuilder.toString())
    }

    fun getMD5(string: String): String? {
        try {
            val messageDigest = MessageDigest.getInstance("MD5")
            messageDigest.update(string.toByteArray())
            val bytes = messageDigest.digest()
            val md5 = StringBuilder()
            for (aByte in bytes) {
                val v: Int = aByte.toInt() and 0XFF
                if (v < 16) {
                    md5.append(0)
                }
                md5.append(v.toString(16))
            }
            return md5.toString()
        } catch (e: NoSuchAlgorithmException) {
            LogUtil.e(e)
        }
        return null
    }

    val microsecondTime: Long
        get() = TimeUtil.cnTimeMillis()

    val secondTime: Int
        get() = (TimeUtil.cnTimeMillis() / 1000).toInt()

    val random: Double
        get() = Random().nextDouble()
}