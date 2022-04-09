package me.teble.xposed.autodaily.task.cron.pattent.parser

import cn.hutool.core.lang.Assert
import cn.hutool.core.util.StrUtil
import me.teble.xposed.autodaily.task.cron.CronException
import me.teble.xposed.autodaily.task.cron.pattent.Part
import me.teble.xposed.autodaily.task.cron.pattent.matcher.AlwaysTrueMatcher
import me.teble.xposed.autodaily.task.cron.pattent.matcher.PartMatcher
import me.teble.xposed.autodaily.task.cron.pattent.matcher.PatternMatcher


/**
 * 定时任务表达式解析器，用于将表达式字符串解析为[PatternMatcher]的列表
 *
 * @author looly
 * @since 5.8.0
 */
object PatternParser {
    private val SECOND_VALUE_PARSER = PartParser.of(Part.SECOND)
    private val MINUTE_VALUE_PARSER = PartParser.of(Part.MINUTE)
    private val HOUR_VALUE_PARSER = PartParser.of(Part.HOUR)
    private val DAY_OF_MONTH_VALUE_PARSER = PartParser.of(Part.DAY_OF_MONTH)
    private val MONTH_VALUE_PARSER = PartParser.of(Part.MONTH)
    private val DAY_OF_WEEK_VALUE_PARSER = PartParser.of(Part.DAY_OF_WEEK)
    private val YEAR_VALUE_PARSER = PartParser.of(Part.YEAR)

    /**
     * 解析表达式到匹配列表中
     *
     * @param cronPattern 复合表达式
     * @return [List]
     */
    fun parse(cronPattern: String): List<PatternMatcher> {
        return parseGroupPattern(cronPattern)
    }

    /**
     * 解析复合任务表达式，格式为：
     * <pre>
     * cronA | cronB | ...
    </pre> *
     *
     * @param groupPattern 复合表达式
     * @return [List]
     */
    private fun parseGroupPattern(groupPattern: String): List<PatternMatcher> {
        val patternList = StrUtil.splitTrim(groupPattern, '|')
        val patternMatchers: MutableList<PatternMatcher> = ArrayList(patternList.size)
        for (pattern in patternList) {
            patternMatchers.add(parseSinglePattern(pattern))
        }
        return patternMatchers
    }

    /**
     * 解析单一定时任务表达式
     *
     * @param pattern 表达式
     * @return [PatternMatcher]
     */
    private fun parseSinglePattern(pattern: String): PatternMatcher {
        val parts = pattern.split("\\s+".toRegex()).toTypedArray()
        Assert.checkBetween(
            parts.size, 5, 7
        ) { CronException("Pattern [{}] is invalid, it must be 5-7 parts! size -> ${parts.size}", pattern) }

        // 偏移量用于兼容Quartz表达式，当表达式有6或7项时，第一项为秒
        var offset = 0
        if (parts.size == 6 || parts.size == 7) {
            offset = 1
        }

        // 秒，如果不支持秒的表达式，则第一位赋值0，表示整分匹配
        val secondPart = if (1 == offset) parts[0] else "0"

        // 年
        val yearMatcher: PartMatcher = if (parts.size == 7) { // 支持年的表达式
            YEAR_VALUE_PARSER.parse(parts[6])
        } else { // 不支持年的表达式，全部匹配
            AlwaysTrueMatcher.INSTANCE
        }
        return PatternMatcher( // 秒
            SECOND_VALUE_PARSER.parse(secondPart),  // 分
            MINUTE_VALUE_PARSER.parse(parts[offset]),  // 时
            HOUR_VALUE_PARSER.parse(parts[1 + offset]),  // 天
            DAY_OF_MONTH_VALUE_PARSER.parse(parts[2 + offset]),  // 月
            MONTH_VALUE_PARSER.parse(parts[3 + offset]),  // 周
            DAY_OF_WEEK_VALUE_PARSER.parse(parts[4 + offset]),  // 年
            yearMatcher
        )
    }
}