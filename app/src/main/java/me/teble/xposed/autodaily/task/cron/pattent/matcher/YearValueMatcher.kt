package me.teble.xposed.autodaily.task.cron.pattent.matcher

/**
 * 年匹配<br></br>
 * 考虑年数字太大，不适合boolean数组，单独使用[LinkedHashSet]匹配
 *
 * @author Looly
 */
class YearValueMatcher(intValueList: Collection<Int>) :
    PartMatcher {
    private val valueList: LinkedHashSet<Int> = LinkedHashSet(intValueList)
    override fun match(t: Int): Boolean {
        return valueList.contains(t)
    }

    override fun nextAfter(value: Int): Int {
        for (year in valueList) {
            if (year >= value) {
                return year
            }
        }

        // 年无效，此表达式整体无效
        return -1
    }

}