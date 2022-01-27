package me.teble.xposed.autodaily.task.util

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import java.time.format.DateTimeFormatter.ISO_LOCAL_TIME
import java.time.format.DateTimeFormatterBuilder
import java.util.*
import java.util.regex.Pattern


private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
private val regexTime = Pattern.compile("^(\\d{4}-\\d{2}-\\d{2}.\\d{2}:\\d{2}.*)$")

fun parseDateTime(resolver: String?): LocalDateTime? {
    if (resolver != null && regexTime.matcher(resolver).find()) {
        when {
            resolver.endsWith("Z") -> {
                return LocalDateTime.ofInstant(Instant.parse(resolver), ZoneId.of("GMT+8"))
            }
            resolver[10] == 'T' -> {
                return LocalDateTime.parse(resolver, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            }
            resolver[10] == ' ' -> {
                return LocalDateTime.parse(
                    resolver, DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .append(ISO_LOCAL_DATE)
                        .appendLiteral(' ')
                        .append(ISO_LOCAL_TIME)
                        .toFormatter()
                )
            }
        }
    }
    return null
}

fun parseDate(resolver: String?): Date? {
    return parseDateTime(resolver)?.toDate()
}

fun LocalDateTime.format(): String =
    dateTimeFormat.format(this)

fun Date.format(): String =
    simpleDateFormat.format(this)

fun LocalDateTime.toDate(): Date =
    Date.from(this.atZone(ZoneId.of("GMT+8")).toInstant())

fun Date.toLocalDateTime(): LocalDateTime =
    this.toInstant().atZone(ZoneId.of("GMT+8")).toLocalDateTime()

inline val LocalDateTime.second: Int
    get() = this.toEpochSecond(ZoneOffset.of("+8")).toInt()

inline val LocalDateTime.millisecond: Long
    get() = this.toInstant(ZoneOffset.of("+8")).toEpochMilli()