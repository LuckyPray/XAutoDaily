package me.teble.xposed.autodaily.utils

import me.teble.xposed.autodaily.task.util.millisecond
import me.teble.xposed.autodaily.ui.ConfUnit
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date

object TimeUtil {

    private var timeDiff: Long = 0

    private val diff: Long
        get() = timeDiff

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
            ConfUnit.cnTimeDiff = timeDiff
        } ?: run {
            timeDiff = ConfUnit.cnTimeDiff
        }
    }

    private fun getNetworkTime(): Long? {
        return runRetry(3) {
            val url = URL("http://www.baidu.com")
            val uc = url.openConnection() as HttpURLConnection
            uc.connectTimeout = 2000
            uc.connect()
            // 避免时间误差
            if (uc.date == 0L) null else uc.date + 500
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