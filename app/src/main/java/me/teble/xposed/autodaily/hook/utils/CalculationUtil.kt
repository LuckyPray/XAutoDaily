package me.teble.xposed.autodaily.hook.utils

import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

object CalculationUtil {
    private val TAG = "CalculationUtil"
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

    fun getCSRFToken(sKey: String): String? {
        var cnt = 5381
        val stringBuilder = StringBuilder()
        stringBuilder.append(cnt shl 5)
        for (element in sKey) {
            val ch = element
            stringBuilder.append((cnt shl 5) + ch.code)
            cnt = ch.code
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
            Log.d(TAG, "getCSRFToken: $e")
        }
        return null
    }

    val microsecondTime: Long
        get() = System.currentTimeMillis()

    val secondTime: Int
        get() = (System.currentTimeMillis() / 1000).toInt()

    val random: Double
        get() = Random().nextDouble()

    @JvmStatic
    fun main(args: Array<String>) {
        println(getPsToken("rX7a-zQDUMn8vF3A9bpcvj-Y4ORXrAY6TMVZvvJsdkQ_"))
        println(getBkn("MD0CNWKHXD"))
    }
}