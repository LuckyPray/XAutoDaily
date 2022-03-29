package me.teble.xposed.autodaily.task.cron.pattent.matcher

import cn.hutool.core.date.Month

/**
 * 每月第几天匹配<br></br>
 * 考虑每月的天数不同，且存在闰年情况，日匹配单独使用
 *
 * @author Looly
 */
class DayOfMonthMatcher(intValueList: List<Int>) : BoolArrayMatcher(intValueList) {
    /**
     * 给定的日期是否匹配当前匹配器
     *
     * @param value      被检查的值，此处为日
     * @param month      实际的月份，从1开始
     * @param isLeapYear 是否闰年
     * @return 是否匹配
     */
    fun match(value: Int, month: Int, isLeapYear: Boolean): Boolean {
        return (super.match(value) // 在约定日范围内的某一天
            //匹配器中用户定义了最后一天（31表示最后一天）
            || value > 27 && match(31) && isLastDayOfMonth(value, month, isLeapYear))
    }

    companion object {
        /**
         * 是否为本月最后一天，规则如下：
         * <pre>
         * 1、闰年2月匹配是否为29
         * 2、其它月份是否匹配最后一天的日期（可能为30或者31）
        </pre> *
         *
         * @param value      被检查的值
         * @param month      月份，从1开始
         * @param isLeapYear 是否闰年
         * @return 是否为本月最后一天
         */
        private fun isLastDayOfMonth(value: Int, month: Int, isLeapYear: Boolean): Boolean {
            return value == Month.getLastDay(month - 1, isLeapYear)
        }
    }
}