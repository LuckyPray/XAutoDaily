package me.teble.xposed.autodaily.task.cron.pattent.matcher

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.lang.Assert
import cn.hutool.core.util.StrUtil
import java.util.*
import kotlin.math.min


/**
 * 将表达式中的数字值列表转换为Boolean数组，匹配时匹配相应数组位
 *
 * @author Looly
 */
open class BoolArrayMatcher(intValueList: List<Int>) : PartMatcher {
    /**
     * 获取表达式定义的最小值
     *
     * @return 最小值
     */
    /**
     * 用户定义此字段的最小值
     */
    val minValue: Int
    private val bValues: BooleanArray
    override fun match(value: Int?): Boolean {
        return if (null == value || value >= bValues.size) {
            false
        } else bValues[value]
    }

    override fun nextAfter(value: Int): Int {
        var value = value
        if (value > minValue) {
            while (value < bValues.size) {
                if (bValues[value]) {
                    return value
                }
                value++
            }
        }

        // 两种情况返回最小值
        // 一是给定值小于最小值，那下一个匹配值就是最小值
        // 二是给定值大于最大值，那下一个匹配值也是下一轮的最小值
        return minValue
    }

    override fun toString(): String {
        return StrUtil.format("Matcher:{}", *arrayOf<Any>(bValues))
    }

    init {
        Assert.isTrue(CollUtil.isNotEmpty(intValueList), "Values must be not empty!")
        bValues = BooleanArray(Collections.max(intValueList) + 1)
        var min = Int.MAX_VALUE
        for (value in intValueList) {
            min = min(min, value)
            bValues[value] = true
        }
        minValue = min
    }
}