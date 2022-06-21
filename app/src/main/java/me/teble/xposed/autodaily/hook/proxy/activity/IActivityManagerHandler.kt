package me.teble.xposed.autodaily.hook.proxy.activity

import android.content.Intent
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.hook.base.hostPackageName
import me.teble.xposed.autodaily.hook.proxy.ProxyManager
import me.teble.xposed.autodaily.utils.LogUtil
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class IActivityManagerHandler(private val mOrigin: Any) : InvocationHandler {

    private fun foundFirstIntentOfArgs(args: Array<Any>): Pair<Int, Intent>? {
        for (i in args.indices) {
            if (args[i] is Intent) {
                return Pair(i, args[i] as Intent)
            }
        }
        return null
    }

    override fun invoke(proxy: Any, method: Method, args: Array<Any>?): Any? {
        try {
            args?.let {
                when (method.name) {
                    "startActivity" -> {
                        foundFirstIntentOfArgs(args)?.let { pair ->
                            val component = pair.second.component
                            component?.let {
                                if (it.packageName == hostPackageName
                                    && it.className.startsWith(BuildConfig.APPLICATION_ID)
                                ) {
                                    LogUtil.d("开始替换")
                                    val wrapper = Intent().apply {
                                        this.setClassName(
                                            component.packageName,
                                            ProxyManager.STUB_DEFAULT_QQ_ACTIVITY
                                        )
                                        this.putExtra(
                                            ProxyManager.ACTIVITY_PROXY_INTENT,
                                            pair.second
                                        )
                                    }
                                    args[pair.first] = wrapper
                                    LogUtil.d("替换完成")
                                }
                            }
                        }
                    }
                }
                return method.invoke(mOrigin, *args)
            }
            return method.invoke(mOrigin)
        } catch (e: InvocationTargetException) {
            throw e.targetException
        }
    }
}