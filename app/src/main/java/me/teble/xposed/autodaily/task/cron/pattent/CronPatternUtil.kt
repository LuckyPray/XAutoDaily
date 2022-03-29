package me.teble.xposed.autodaily.task.cron.pattent

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.date.DateUnit
import cn.hutool.core.date.DateUtil
import cn.hutool.core.lang.Assert
import java.util.*


/**
 * 定时任务表达式工具类
 *
 * @author looly
 */
object CronPatternUtil {
    /**
     * 列举指定日期之后（到开始日期对应年年底）内第一个匹配表达式的日期
     *
     * @param pattern 表达式
     * @param start 起始时间
     * @param isMatchSecond 是否匹配秒
     * @return 日期
     * @since 4.5.8
     */
    fun nextDateAfter(pattern: CronPattern, start: Date, isMatchSecond: Boolean): Date? {
        val matchedDates: List<Date> = matchedDates(
            pattern,
            start.time,
            DateUtil.endOfYear(start).time,
            1,
            isMatchSecond
        )
        return if (CollUtil.isNotEmpty(matchedDates)) {
            matchedDates[0]
        } else null
    }

    /**
     * 列举指定日期之后（到开始日期对应年年底）内所有匹配表达式的日期
     *
     * @param patternStr 表达式字符串
     * @param start 起始时间
     * @param count 列举数量
     * @param isMatchSecond 是否匹配秒
     * @return 日期列表
     */
    fun matchedDates(
        patternStr: String,
        start: Date,
        count: Int,
        isMatchSecond: Boolean
    ): List<Date> {
        return matchedDates(patternStr, start, DateUtil.endOfYear(start), count, isMatchSecond)
    }

    /**
     * 列举指定日期范围内所有匹配表达式的日期
     *
     * @param patternStr 表达式字符串
     * @param start 起始时间
     * @param end 结束时间
     * @param count 列举数量
     * @param isMatchSecond 是否匹配秒
     * @return 日期列表
     */
    fun matchedDates(
        patternStr: String,
        start: Date,
        end: Date,
        count: Int,
        isMatchSecond: Boolean
    ): List<Date> {
        return matchedDates(patternStr, start.getTime(), end.getTime(), count, isMatchSecond)
    }

    /**
     * 列举指定日期范围内所有匹配表达式的日期
     *
     * @param patternStr 表达式字符串
     * @param start 起始时间
     * @param end 结束时间
     * @param count 列举数量
     * @param isMatchSecond 是否匹配秒
     * @return 日期列表
     */
    fun matchedDates(
        patternStr: String,
        start: Long,
        end: Long,
        count: Int,
        isMatchSecond: Boolean
    ) = matchedDates(CronPattern(patternStr), start, end, count, isMatchSecond)

    /**
     * 列举指定日期范围内所有匹配表达式的日期
     *
     * @param pattern 表达式
     * @param start 起始时间
     * @param end 结束时间
     * @param count 列举数量
     * @param isMatchSecond 是否匹配秒
     * @return 日期列表
     */
    fun matchedDates(
        pattern: CronPattern,
        start: Long,
        end: Long,
        count: Int,
        isMatchSecond: Boolean
    ): List<Date> {
        Assert.isTrue(start < end, "Start date is later than end !")
        val result: MutableList<Date> = ArrayList(count)
        val step = if (isMatchSecond) DateUnit.SECOND.millis else DateUnit.MINUTE.millis
        var i = start
        while (i < end) {
            if (pattern.match(i, isMatchSecond)) {
                result.add(DateUtil.date(i))
                if (result.size >= count) {
                    break
                }
            }
            i += step
        }
        return result
    }
}