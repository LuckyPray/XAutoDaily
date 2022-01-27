package me.teble.xposed.autodaily.utils

open class BaseException : RuntimeException {

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)
}