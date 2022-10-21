package me.teble.xposed.autodaily.task.cron.pattent.parser

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.date.Month
import cn.hutool.core.date.Week
import cn.hutool.core.util.NumberUtil
import cn.hutool.core.util.StrUtil
import me.teble.xposed.autodaily.task.cron.CronException
import me.teble.xposed.autodaily.task.cron.pattent.Part
import me.teble.xposed.autodaily.task.cron.pattent.matcher.*
import kotlin.math.max


/**
 * 定时任务表达式各个部分的解析器，根据[Part]指定不同部分，解析为[PartMatcher]<br></br>
 * 每个部分支持：
 *
 *  * ***** ：表示匹配这个位置所有的时间
 *  * **?** ：表示匹配这个位置任意的时间（与"*"作用一致）
 *  * **L** ：表示匹配这个位置允许的最大值
 *  * ***&#47;2** ：表示间隔时间，例如在分上，表示每两分钟，同样*可以使用数字列表代替，逗号分隔
 *  * **2-8** ：表示连续区间，例如在分上，表示2,3,4,5,6,7,8分
 *  * **2,3,5,8** ：表示列表
 *  * **wed** ：表示周别名
 *  * **jan** ：表示月别名
 *
 *
 * @author looly
 * @since 5.8.0
 */
class PartParser(val part: Part) {

    /**
     * 将表达式解析为[PartMatcher]<br></br>
     *
     *  * * 或者 ? 返回[AlwaysTrueMatcher]
     *  * [Part.DAY_OF_MONTH] 返回[DayOfMonthMatcher]
     *  * [Part.YEAR] 返回[YearValueMatcher]
     *  * 其他 返回[BoolArrayMatcher]
     *
     *
     * @param value 表达式
     * @return [PartMatcher]
     */
    fun parse(value: String): PartMatcher {
        if (isMatchAllStr(value)) {
            //兼容Quartz的"?"表达式，不会出现互斥情况，与"*"作用相同
            return AlwaysTrueMatcher()
        }
        val values = parseArray(value)
        if (values.isEmpty()) {
            throw CronException("Invalid part value: [{}]", value)
        }
        return when (part) {
            Part.DAY_OF_MONTH -> DayOfMonthMatcher(values)
            Part.YEAR -> YearValueMatcher(values)
            else -> BoolArrayMatcher(values)
        }
    }

    /**
     * 处理数组形式表达式<br></br>
     * 处理的形式包括：
     *
     *  * **a** 或 *****
     *  * **a,b,c,d**
     *
     *
     * @param value 子表达式值
     * @return 值列表
     */
    private fun parseArray(value: String): List<Int> {
        val values: List<Int> = ArrayList()
        val parts = StrUtil.split(value, StrUtil.C_COMMA)
        for (part in parts) {
            CollUtil.addAllIfNotContains(values, parseStep(part))
        }
        return values
    }

    /**
     * 处理间隔形式的表达式<br></br>
     * 处理的形式包括：
     *
     *  * **a** 或 *****
     *  * **a&#47;b** 或 ***&#47;b**
     *  * **a-b/2**
     *
     *
     * @param value 表达式值
     * @return List
     */
    private fun parseStep(value: String): List<Int> {
        val parts = StrUtil.split(value, StrUtil.C_SLASH)
        val size = parts.size
        val results: List<Int>
        results = if (size == 1) { // 普通形式
            parseRange(value, -1)
        } else if (size == 2) { // 间隔形式
            val step = parseNumber(parts[1])
            if (step < 1) {
                throw CronException("Non positive divisor for field: [{}]", value)
            }
            parseRange(parts[0], step)
        } else {
            throw CronException("Invalid syntax of field: [{}]", value)
        }
        return results
    }

    /**
     * 处理表达式中范围表达式 处理的形式包括：
     *
     *  * *
     *  * 2
     *  * 3-8
     *  * 8-3
     *  * 3-3
     *
     *
     * @param value 范围表达式
     * @param step  步进
     * @return List
     */
    private fun parseRange(value: String, step: Int): List<Int> {
        var step = step
        val results: MutableList<Int> = ArrayList()

        // 全部匹配形式
        if (value.length <= 2) {
            //根据步进的第一个数字确定起始时间，类似于 12/3则从12（秒、分等）开始
            var minValue: Int = part.min
            if (!isMatchAllStr(value)) {
                minValue = max(minValue, parseNumber(value))
            } else {
                //在全匹配模式下，如果步进不存在，表示步进为1
                if (step < 1) {
                    step = 1
                }
            }
            if (step > 0) {
                val maxValue: Int = part.max
                if (minValue > maxValue) {
                    throw CronException("Invalid value {} > {}", minValue, maxValue)
                }
                //有步进
                var i = minValue
                while (i <= maxValue) {
                    results.add(i)
                    i += step
                }
            } else {
                //固定时间
                results.add(minValue)
            }
            return results
        }

        //Range模式
        val parts = StrUtil.split(value, '-')
        val size = parts.size
        if (size == 1) { // 普通值
            val v1 = parseNumber(value)
            if (step > 0) { //类似 20/2的形式
                NumberUtil.appendRange(v1, part.max, step, results)
            } else {
                results.add(v1)
            }
        } else if (size == 2) { // range值
            val v1 = parseNumber(parts[0])
            val v2 = parseNumber(parts[1])
            if (step < 1) {
                //在range模式下，如果步进不存在，表示步进为1
                step = 1
            }
            if (v1 < v2) { // 正常范围，例如：2-5
                NumberUtil.appendRange(v1, v2, step, results)
            } else if (v1 > v2) { // 逆向范围，反选模式，例如：5-2
                NumberUtil.appendRange(v1, part.max, step, results)
                NumberUtil.appendRange(part.min, v2, step, results)
            } else { // v1 == v2，此时与单值模式一致
                NumberUtil.appendRange(v1, part.max, step, results)
            }
        } else {
            throw CronException("Invalid syntax of field: [{}]", value)
        }
        return results
    }

    /**
     * 解析单个int值，支持别名
     *
     * @param value 被解析的值
     * @return 解析结果
     * @throws CronException 当无效数字或无效别名时抛出
     */
    @Throws(CronException::class)
    private fun parseNumber(value: String): Int {
        var i: Int
        i = try {
            value.toInt()
        } catch (ignore: NumberFormatException) {
            parseAlias(value)
        }

        // 支持负数
        if (i < 0) {
            i += part.max
        }

        // 周日可以用0或7表示，统一转换为0
        if (Part.DAY_OF_WEEK.equals(part) && Week.SUNDAY.iso8601Value == i) {
            i = Week.SUNDAY.ordinal
        }
        return part.checkValue(i)
    }

    /**
     * 解析别名支持包括：<br></br>
     *
     *  * **L 表示最大值**
     *  * [Part.MONTH]和[Part.DAY_OF_WEEK]别名
     *
     *
     * @param name 别名
     * @return 解析int值
     * @throws CronException 无匹配别名时抛出异常
     */
    @Throws(CronException::class)
    private fun parseAlias(name: String): Int {
        if ("L".equals(name, ignoreCase = true)) {
            // L表示最大值
            return part.max
        }
        return when (part) {
            Part.MONTH ->                // 月份从1开始
                Month.of(name).valueBaseOne

            Part.DAY_OF_WEEK ->                // 周从0开始，0表示周日
                Week.of(name).ordinal

            else -> throw CronException("Invalid alias value: [{}]", name)
        }
    }

    companion object {
        /**
         * 创建解析器
         *
         * @param part 对应解析的部分枚举
         * @return 解析器
         */
        fun of(part: Part): PartParser {
            return PartParser(part)
        }

        /**
         * 是否为全匹配符<br></br>
         * 全匹配符指 * 或者 ?
         *
         * @param value 被检查的值
         * @return 是否为全匹配符
         * @since 4.1.18
         */
        private fun isMatchAllStr(value: String): Boolean {
            return 1 == value.length && ("*" == value || "?" == value)
        }
    }
}
