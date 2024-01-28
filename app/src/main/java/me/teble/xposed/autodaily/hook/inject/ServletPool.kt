package me.teble.xposed.autodaily.hook.inject

import me.teble.xposed.autodaily.hook.inject.servlets.FavoriteServlet
import me.teble.xposed.autodaily.hook.inject.servlets.TroopClockInServlet
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.appRuntime
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.fieldValueAs
import me.teble.xposed.autodaily.utils.invoke
import mqq.app.Servlet
import mqq.app.ServletContainer
import java.util.concurrent.ConcurrentHashMap

object ServletPool {

    private val servletArray = arrayOf<Class<out Servlet>>(
        FavoriteServlet::class.java,
        TroopClockInServlet::class.java,
    )

    private val servletMap = ConcurrentHashMap<Class<out Servlet>, Servlet>()

    fun injectServlet() {
        val servletContainer = appRuntime.invoke("getServletContainer") as ServletContainer
        val managedServlet = servletContainer
            .fieldValueAs<ConcurrentHashMap<String, Servlet>>("managedServlet")!!
        for (servletClass in servletArray) {
            val servlet = servletClass.getDeclaredConstructor().newInstance() as Servlet
            servlet.invoke("init", appRuntime, servletContainer)
            servlet.invoke("onCreate")
            managedServlet[servlet::class.java.name] = servlet
            servletMap[servlet::class.java] = servlet
            LogUtil.d("inject servlet: $servletClass")
        }
    }

    @Synchronized
    @Suppress("UNCHECKED_CAST")
    fun <T : Servlet> getServlet(servletClass: Class<T>): T {
        return servletMap[servletClass] as T
    }

    val favoriteServlet: FavoriteServlet by lazy { getServlet(FavoriteServlet::class.java) }
    val troopClockInServlet: TroopClockInServlet by lazy { getServlet(TroopClockInServlet::class.java) }
}

