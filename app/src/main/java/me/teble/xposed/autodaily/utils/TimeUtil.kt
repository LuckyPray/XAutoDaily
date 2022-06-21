package me.teble.xposed.autodaily.utils

import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.task.util.millisecond
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

object TimeUtil {

    private var timeDiff: Long? = null

    private val diff: Long
        get() = timeDiff ?: 0

    private val cnZoneId by lazy { ZoneId.of("+8") }

    private val defaultZoneOffset by lazy { OffsetDateTime.now().offset }

    val offsetTime by lazy {
        (ZonedDateTime.now(cnZoneId).offset.totalSeconds - ZonedDateTime.now(defaultZoneOffset).offset.totalSeconds) * 1000
    }

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
            val uc = url.openConnection() as HttpURLConnection
            uc.connectTimeout = 2000
            uc.connect()
            // 避免时间误差
            uc.date + 500
        } catch (e: Exception) {
            try {
                LogUtil.e(e, "get network time error, will used localtime -> ${getCNTime()}:")
                ToastUtil.send("获取网络时间失败，将使用本地时间执行任务，可能存在误差")
            } catch (ignore: Exception) {
                LogUtil.e(e,"get network time error, will used localtime -> ${getCNTime()}:")
            }
            null
        }
    }

    private fun getCNTime(): Long {
        val time: LocalDateTime = LocalDateTime.now(cnZoneId)
        return time.millisecond
    }

    private fun getLocalTime(): Long {
        val time: LocalDateTime = LocalDateTime.now()
        return time.toInstant(defaultZoneOffset).toEpochMilli()
    }

    fun cnTimeMillis(): Long {
        return getCNTime() + diff
    }

    fun localTimeMillis(): Long {
        return getLocalTime() + diff
    }

    fun getCNDate(): Date {
        return Date(localTimeMillis() + offsetTime)
    }
}