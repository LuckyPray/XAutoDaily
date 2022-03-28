package me.teble.xposed.autodaily.utils

import java.net.URL
import java.net.URLConnection

object TimeUtil {
    private const val TAG = "TimeUtil"

    private var timeDiff: Long? = null

    private val diff: Long
        get() = timeDiff ?: 0

    fun init() {
        val networkTime = getNetworkTime()
        LogUtil.d("networkTime: $networkTime")
        networkTime?.let {
            timeDiff = it - System.currentTimeMillis()
        }
    }

    private fun getNetworkTime(): Long? {
        return try {
            val url = URL("http://www.baidu.com")
            val uc: URLConnection = url.openConnection()
            uc.connectTimeout = 2000
            uc.connect()
            uc.date
        } catch (e: Exception) {
            LogUtil.e(e)
            null
        }
    }

    fun getCurrentTime(): Long {
        return System.currentTimeMillis() + diff
    }
}