package me.teble.xposed.autodaily.utils

import me.teble.xposed.autodaily.hook.utils.ToastUtil
import java.net.URL
import java.net.URLConnection
import java.time.LocalDateTime
import java.time.ZoneOffset

object TimeUtil {
    private const val TAG = "TimeUtil"

    private var timeDiff: Long? = null

    private val diff: Long
        get() = timeDiff ?: 0

    fun init() {
        val networkTime = getNetworkTime()
        LogUtil.d("networkTime: $networkTime")
        networkTime?.let {
            timeDiff = it - getCNTime()
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
            LogUtil.e(e, "get network time error:")
            ToastUtil.send("获取网络时间失败，将使用本地时间执行任务，可能存在误差")
            null
        }
    }

    private fun getCNTime(): Long {
        val time: LocalDateTime = LocalDateTime.now()
        return time.toInstant(ZoneOffset.of("+8")).toEpochMilli()
    }

    fun currentTimeMillis(): Long {
        return getCNTime() + diff
    }
}