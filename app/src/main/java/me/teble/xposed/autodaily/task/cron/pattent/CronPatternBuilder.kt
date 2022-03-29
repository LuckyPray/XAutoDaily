package me.teble.xposed.autodaily.task.cron.pattent

import cn.hutool.core.builder.Builder
import cn.hutool.core.util.StrUtil


class CronPatternBuilder : Builder<String> {
    private val parts = arrayOfNulls<String>(7)
    operator fun set(part: Part, value: String?): CronPatternBuilder {
        parts[part.ordinal] = value
        return this
    }

    override fun build(): String {
        @Suppress("UNCHECKED_CAST")
        return StrUtil.join(StrUtil.SPACE, *parts as Array<Any?>)
    }

    companion object {
        private const val serialVersionUID = 1L
        fun of(): CronPatternBuilder {
            return CronPatternBuilder()
        }
    }
}