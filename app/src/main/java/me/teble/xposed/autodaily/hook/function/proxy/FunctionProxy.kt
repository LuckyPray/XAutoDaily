package me.teble.xposed.autodaily.hook.function.proxy

import me.teble.xposed.autodaily.hook.function.BaseFunction
import net.bytebuddy.implementation.bind.annotation.*
import java.lang.reflect.Method
import java.util.concurrent.Callable

class FunctionProxy {

    @RuntimeType
    fun intercept(@This mFunction: BaseFunction,
                  @Origin method: Method,
                  @AllArguments args: Array<out Any>?,
                  @SuperCall zuper: Callable<*>
    ): Any? {
        val isInit = mFunction.isInit
        if (!isInit) {
            return null
        }
        return zuper.call()
    }
}