package me.teble.xposed.autodaily.task.cron.pattent

import android.icu.util.Calendar
import cn.hutool.core.date.Month
import cn.hutool.core.date.Week
import cn.hutool.core.lang.Assert
import me.teble.xposed.autodaily.task.cron.CronException
import me.teble.xposed.autodaily.task.cron.pattent.Part.SECOND
import me.teble.xposed.autodaily.task.cron.pattent.Part.YEAR


/**
 * 表达式各个部分的枚举，用于限定在表达式中的位置和规则（如最小值和最大值）<br></br>
 * [.ordinal]表示此部分在表达式中的位置，如0表示秒<br></br>
 * 表达式各个部分的枚举位置为：
 * <pre>
 * 0       1    2        3         4       5         6
 * [SECOND] MINUTE HOUR DAY_OF_MONTH MONTH DAY_OF_WEEK [YEAR]
</pre> *
 *
 * @author looly
 * @since 5.8.0
 */
enum class Part(
    /**
     * 获取Calendar中对应字段项
     *
     * @return Calendar中对应字段项
     */
    val calendarField: Int, min: Int, max: Int
) {
    SECOND(Calendar.SECOND, 0, 59), MINUTE(Calendar.MINUTE, 0, 59), HOUR(
        Calendar.HOUR_OF_DAY,
        0,
        23
    ),
    DAY_OF_MONTH(Calendar.DAY_OF_MONTH, 1, 31), MONTH(
        Calendar.MONTH,
        Month.JANUARY.valueBaseOne,
        Month.DECEMBER.valueBaseOne
    ),
    DAY_OF_WEEK(
        Calendar.DAY_OF_WEEK,
        Week.SUNDAY.ordinal,
        Week.SATURDAY.ordinal
    ),
    YEAR(Calendar.YEAR, 1970, 2099);

    /**
     * 获取最小值
     *
     * @return 最小值
     */
    var min = 0

    /**
     * 获取最大值
     *
     * @return 最大值
     */
    var max = 0

    /**
     * 检查单个值是否有效
     *
     * @param value 值
     * @return 检查后的值
     * @throws CronException 检查无效抛出此异常
     */
    @Throws(CronException::class)
    fun checkValue(value: Int): Int {
        Assert.checkBetween(
            value, min, max
        ) { CronException("Value {} out of range: [{} , {}]", value, min, max) }
        return value
    }

    companion object {
        // ---------------------------------------------------------------
        private val ENUMS = values()

        /**
         * 根据位置获取Part
         *
         * @param i 位置，从0开始
         * @return Part
         */
        fun of(i: Int): Part {
            return ENUMS[i]
        }
    }

    init {
        if (min > max) {
            this.min = max
            this.max = min
        } else {
            this.min = min
            this.max = max
        }
    }
}
