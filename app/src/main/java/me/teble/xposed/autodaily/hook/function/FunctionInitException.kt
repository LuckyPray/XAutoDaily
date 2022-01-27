package me.teble.xposed.autodaily.hook.function

import me.teble.xposed.autodaily.utils.BaseException

class FunctionInitException : BaseException {

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)
}