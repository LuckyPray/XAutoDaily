package me.teble.xposed.autodaily.hook.inject

import com.tencent.mobileqq.app.BusinessHandler
import me.teble.xposed.autodaily.hook.inject.handler.TroopClockInHandler
import java.util.concurrent.ConcurrentHashMap

object HandlerPool {

    private val servletArray = arrayOf<Class<out BusinessHandler>>(
        TroopClockInHandler::class.java,
    )

    private val handlerMap = ConcurrentHashMap<Class<out BusinessHandler>, BusinessHandler>()

    fun injectHandler() {

    }
}