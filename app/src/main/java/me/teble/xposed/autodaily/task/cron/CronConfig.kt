package me.teble.xposed.autodaily.task.cron

import java.util.*


/**
 * 定时任务配置类
 *
 * @author looly
 * @since 5.4.7
 */
class CronConfig {
    /**
     * 时区
     */
    var timeZone: TimeZone = TimeZone.getDefault()
    /**
     * 是否支持秒匹配
     *
     * @return `true`使用，`false`不使用
     */
    /**
     * 是否支持秒匹配
     */
    var matchSecond = false

    /**
     * 设置时区
     *
     * @param timezone 时区
     * @return this
     */
    fun setTimeZone(timezone: TimeZone): CronConfig {
        this.timeZone = timezone
        return this
    }

    /**
     * 设置是否支持秒匹配，默认不使用
     *
     * @param isMatchSecond `true`支持，`false`不支持
     * @return this
     */
    fun setMatchSecond(isMatchSecond: Boolean): CronConfig {
        this.matchSecond = isMatchSecond
        return this
    }
}