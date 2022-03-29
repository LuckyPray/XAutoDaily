package me.teble.xposed.autodaily.task.cron

import cn.hutool.core.util.StrUtil


/**
 * 定时任务异常
 *
 * @author xiaoleilu
 */
class CronException : RuntimeException {
    constructor(e: Throwable) : super(e.message, e)

    constructor(message: String?) : super(message)

    constructor(messageTemplate: String?, vararg params: Any?) : super(
        StrUtil.format(
            messageTemplate,
            *params
        )
    )

    constructor(
        message: String?,
        throwable: Throwable?,
        enableSuppression: Boolean,
        writableStackTrace: Boolean
    ) : super(message, throwable, enableSuppression, writableStackTrace)

    constructor(throwable: Throwable?, messageTemplate: String?, vararg params: Any?) : super(
        StrUtil.format(messageTemplate, *params),
        throwable
    )

    companion object {
        private const val serialVersionUID = 1L
    }
}