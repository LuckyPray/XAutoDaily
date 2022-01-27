package me.teble.xposed.autodaily.hook.proxy.activity

import android.content.Intent
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.hook.base.Global
import me.teble.xposed.autodaily.hook.proxy.ProxyManager
import me.teble.xposed.autodaily.utils.LogUtil
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class IActivityManagerHandler(private val mOrigin: Any) : InvocationHandler {
    companion object {
        const val TAG = "IActivityManagerHandler"
    }

    private fun foundFirstIntentOfArgs(args: Array<Any>): Pair<Int, Intent>? {
        for (i in args.indices) {
            if (args[i] is Intent) {
                return Pair(i, args[i] as Intent)
            }
        }
        return null
    }

    override fun invoke(proxy: Any, method: Method, args: Array<Any>?): Any? {
        args?.let {
//            LogUtil.d(TAG, " -------> ${method.name}")
            when (method.name) {
                "startActivity" -> {
                    LogUtil.d(TAG, " -------> startActivity")
                    foundFirstIntentOfArgs(args)?.let { pair ->
                        val component = pair.second.component
                        component?.let {
                            if (it.packageName == Global.hostPackageName
                                && it.className.startsWith(BuildConfig.APPLICATION_ID)) {
                                LogUtil.d(TAG, "开始替换")
                                val wrapper = Intent().apply {
                                    this.setClassName(component.packageName, ProxyManager.STUB_DEFAULT_QQ_ACTIVITY)
                                    this.putExtra(ProxyManager.ACTIVITY_PROXY_INTENT, pair.second)
                                }
                                args[pair.first] = wrapper
                                LogUtil.d(TAG, "替换完成")
                            }
                        }
                    }
                }
//                "startService" -> {
//                    LogUtil.d(TAG, " -------> startService")
//                    foundFirstIntentOfArgs(args)?.let { pair ->
//                        val component = (pair.second).component
//                        component?.let {
//                            if (it.packageName == Global.hostPackageName
//                                && it.className.startsWith(BuildConfig.APPLICATION_ID)) {
//                                val wrapper = Intent().apply {
//                                    this.setClassName(component.packageName, ProxyManager.STUB_DEFAULT_QQ_SERVICE)
//                                    this.putExtra(ProxyManager.ACTIVITY_PROXY_INTENT, pair.second)
//                                }
//                                args[pair.first] = wrapper
//                            }
//                        }
//                    }
//                }
            }
            return method.invoke(mOrigin, *args)
        }
        return method.invoke(mOrigin)
    }
}