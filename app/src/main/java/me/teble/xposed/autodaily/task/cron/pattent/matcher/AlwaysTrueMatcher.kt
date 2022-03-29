package me.teble.xposed.autodaily.task.cron.pattent.matcher

import cn.hutool.core.util.StrUtil

/**
 * 所有值匹配，始终返回`true`
 *
 * @author Looly
 */
open class AlwaysTrueMatcher : PartMatcher {
    override fun match(t: Int?): Boolean {
        return true
    }

    override fun nextAfter(value: Int): Int {
        return value
    }

    override fun toString(): String {
        return StrUtil.format("[Matcher]: always true.")
    }

    companion object {
        var INSTANCE = AlwaysTrueMatcher()
    }
}