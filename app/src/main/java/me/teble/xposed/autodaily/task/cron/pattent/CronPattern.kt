package me.teble.xposed.autodaily.task.cron.pattent

import cn.hutool.core.collection.CollUtil
import me.teble.xposed.autodaily.task.cron.pattent.matcher.PatternMatcher
import me.teble.xposed.autodaily.task.cron.pattent.parser.PatternParser
import java.util.*


/**
 * 定时任务表达式<br></br>
 * 表达式类似于Linux的crontab表达式，表达式使用空格分成5个部分，按顺序依次为：
 *
 *  1. **分** ：范围：0~59
 *  1. **时** ：范围：0~23
 *  1. **日** ：范围：1~31，**"L"** 表示月的最后一天
 *  1. **月** ：范围：1~12，同时支持不区分大小写的别名："jan","feb", "mar", "apr", "may","jun", "jul", "aug", "sep","oct", "nov", "dec"
 *  1. **周** ：范围：0 (Sunday)~6(Saturday)，7也可以表示周日，同时支持不区分大小写的别名："sun","mon", "tue", "wed", "thu","fri", "sat"，**"L"** 表示周六
 *
 *
 *
 * 为了兼容Quartz表达式，同时支持6位和7位表达式，其中：<br></br>
 *
 * <pre>
 * 当为6位时，第一位表示**秒** ，范围0~59，但是第一位不做匹配
 * 当为7位时，最后一位表示**年** ，范围1970~2099，但是第7位不做解析，也不做匹配
</pre> *
 *
 *
 * 当定时任务运行到的时间匹配这些表达式后，任务被启动。<br></br>
 * 注意：
 *
 * <pre>
 * 当isMatchSecond为`true`时才会匹配秒部分
 * 默认都是关闭的
</pre> *
 *
 *
 * 对于每一个子表达式，同样支持以下形式：
 *
 *  * ***** ：表示匹配这个位置所有的时间
 *  * **?** ：表示匹配这个位置任意的时间（与"*"作用一致）
 *  * ***&#47;2** ：表示间隔时间，例如在分上，表示每两分钟，同样*可以使用数字列表代替，逗号分隔
 *  * **2-8** ：表示连续区间，例如在分上，表示2,3,4,5,6,7,8分
 *  * **2,3,5,8** ：表示列表
 *  * **cronA | cronB** ：表示多个定时表达式
 *
 * 注意：在每一个子表达式中优先级：
 *
 * <pre>
 * 间隔（/） &gt; 区间（-） &gt; 列表（,）
</pre> *
 *
 *
 * 例如 2,3,6/3中，由于“/”优先级高，因此相当于2,3,(6/3)，结果与 2,3,6等价<br></br>
 * <br></br>
 *
 *
 * 一些例子：
 *
 *  * **5 * * * *** ：每个点钟的5分执行，00:05,01:05……
 *  * *** * * * *** ：每分钟执行
 *  * ***&#47;2 * * * *** ：每两分钟执行
 *  * *** 12 * * *** ：12点的每分钟执行
 *  * **59 11 * * 1,2** ：每周一和周二的11:59执行
 *  * **3-18&#47;5 * * * *** ：3~18分，每5分钟执行一次，即0:03, 0:08, 0:13, 0:18, 1:03, 1:08……
 *
 *
 * @author Looly
 */
class CronPattern(private val pattern: String) {
    private val matchers: List<PatternMatcher> = PatternParser.parse(pattern)

    /**
     * 给定时间是否匹配定时任务表达式
     *
     * @param millis        时间毫秒数
     * @param isMatchSecond 是否匹配秒
     * @return 如果匹配返回 `true`, 否则返回 `false`
     */
    fun match(millis: Long, isMatchSecond: Boolean): Boolean {
        return match(TimeZone.getDefault(), millis, isMatchSecond)
    }

    /**
     * 给定时间是否匹配定时任务表达式
     *
     * @param timezone      时区 [TimeZone]
     * @param millis        时间毫秒数
     * @param isMatchSecond 是否匹配秒
     * @return 如果匹配返回 `true`, 否则返回 `false`
     */
    fun match(timezone: TimeZone?, millis: Long, isMatchSecond: Boolean): Boolean {
        val calendar = GregorianCalendar(timezone)
        calendar.timeInMillis = millis
        return match(calendar, isMatchSecond)
    }

    /**
     * 给定时间是否匹配定时任务表达式
     *
     * @param calendar      时间
     * @param isMatchSecond 是否匹配秒
     * @return 如果匹配返回 `true`, 否则返回 `false`
     */
    fun match(calendar: Calendar, isMatchSecond: Boolean): Boolean {
        val second = if (isMatchSecond) calendar.get(Calendar.SECOND) else -1
        val minute: Int = calendar.get(Calendar.MINUTE)
        val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val month: Int = calendar.get(Calendar.MONTH) + 1 // 月份从1开始
        val dayOfWeek: Int = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 星期从0开始，0和7都表示周日
        val year: Int = calendar.get(Calendar.YEAR)
        return match(second, minute, hour, dayOfMonth, month, dayOfWeek, year)
    }

    /**
     * 返回匹配到的下一个时间<br></br>
     * TODO 周定义后，结果错误，需改进
     *
     * @param calendar 时间
     * @return 匹配到的下一个时间
     */
    fun nextMatchAfter(calendar: Calendar): Calendar {
        val second: Int = calendar.get(Calendar.SECOND)
        val minute: Int = calendar.get(Calendar.MINUTE)
        val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val month: Int = calendar.get(Calendar.MONTH) + 1 // 月份从1开始
        val dayOfWeek: Int = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 星期从0开始，0和7都表示周日
        val year: Int = calendar.get(Calendar.YEAR)
        return nextMatchAfter(
            intArrayOf(second, minute, hour, dayOfMonth, month, dayOfWeek, year),
            calendar.timeZone
        )
    }

    override fun toString(): String {
        return pattern
    }

    /**
     * 给定时间是否匹配定时任务表达式
     *
     * @param second     秒数，-1表示不匹配此项
     * @param minute     分钟
     * @param hour       小时
     * @param dayOfMonth 天
     * @param month      月，从1开始
     * @param dayOfWeek  周，从0开始，0和7都表示周日
     * @param year       年
     * @return 如果匹配返回 `true`, 否则返回 `false`
     */
    private fun match(
        second: Int,
        minute: Int,
        hour: Int,
        dayOfMonth: Int,
        month: Int,
        dayOfWeek: Int,
        year: Int
    ): Boolean {
        for (matcher in matchers) {
            if (matcher.match(second, minute, hour, dayOfMonth, month, dayOfWeek, year)) {
                return true
            }
        }
        return false
    }

    /**
     * 获取下一个最近的匹配日期时间
     *
     * @param values     时间字段值
     * @param zone       时区
     * @return [Calendar]
     */
    private fun nextMatchAfter(values: IntArray, zone: TimeZone): Calendar {
        val nextMatches: MutableList<Calendar> = ArrayList(matchers.size)
        for (matcher in matchers) {
            nextMatches.add(matcher.nextMatchAfter(values, zone))
        }
        // 返回匹配到的最早日期
        return CollUtil.min(nextMatches)
    }

    companion object {
        /**
         * 解析表达式为 CronPattern
         *
         * @param pattern 表达式
         * @return CronPattern
         * @since 5.8.0
         */
        fun of(pattern: String): CronPattern {
            return CronPattern(pattern)
        }
    }
}
