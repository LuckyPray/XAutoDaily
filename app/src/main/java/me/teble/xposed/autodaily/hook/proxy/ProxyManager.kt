package me.teble.xposed.autodaily.hook.proxy

import android.app.Instrumentation
import android.os.Handler
import me.teble.xposed.autodaily.hook.base.Global
import me.teble.xposed.autodaily.hook.base.Initiator.load
import me.teble.xposed.autodaily.hook.proxy.activity.IActivityManagerHandler
import me.teble.xposed.autodaily.hook.proxy.activity.MyHandler
import me.teble.xposed.autodaily.hook.proxy.activity.MyInstrumentation
import me.teble.xposed.autodaily.utils.*
import java.lang.reflect.Proxy

object ProxyManager {
    const val TAG = "ProxyManager"
    const val STUB_DEFAULT_QQ_ACTIVITY = "com.tencent.mobileqq.activity.photo.CameraPreviewActivity"
    const val STUB_DEFAULT_QQ_SERVICE = "com.tencent.mobileqq.emosm.web.MessengerService"
    const val ACTIVITY_PROXY_INTENT = "xautodaily_proxy_intent"

    private var isInit = false

    fun init() {
        if (isInit) return
        LogUtil.d(TAG, "start init proxy manager")
        val currentActivityThread = load("android.app.ActivityThread")!!
            .fieldValue("sCurrentActivityThread")!!
        replaceInstrumentation(currentActivityThread)
        replaceHandler(currentActivityThread)
        replaceIActivityManager()
        replaceIActivityTaskManager()
        isInit = true
    }

    private fun replaceInstrumentation(activityThread: Any) {
        val fInstrumentation = activityThread.field("mInstrumentation")!!
        val mInstrumentation = fInstrumentation.get(activityThread) as Instrumentation
        fInstrumentation.set(activityThread, MyInstrumentation(mInstrumentation))
        LogUtil.d(TAG, "replace Instrumentation success")
    }

    private fun replaceHandler(activityThread: Any) {
        val handler: Handler = activityThread.fieldValueAs("mH")!!
        val fCallback = handler.field("mCallback")!!
        val callback = fCallback.get(handler) as Handler.Callback?
        fCallback.set(handler, MyHandler(callback))
        LogUtil.d(TAG, "replace Handler success")
    }

    private fun replaceIActivityManager() {
        val mInstance = load("android.app.ActivityManagerNative")?.fieldValue("gDefault")
            ?: load("android.app.ActivityManager")?.fieldValue("IActivityManagerSingleton")!!
        val fmInstance = mInstance.field("mInstance")!!
        val activityManager = mInstance.invoke("get")!!
        fmInstance.set(mInstance, Proxy.newProxyInstance(
            Global.moduleClassLoader,
            arrayOf(load("android.app.IActivityManager")),
            IActivityManagerHandler(activityManager)
        ))
        LogUtil.d(TAG, "replace IActivityManager success")
    }

    private fun replaceIActivityTaskManager() {
        val mInstance = load("android.app.ActivityTaskManager")?.fieldValue("IActivityTaskManagerSingleton")!!
        val fmInstance = mInstance.field("mInstance")!!
        val activityTaskManager = mInstance.invoke("get")!!
        fmInstance.set(mInstance, Proxy.newProxyInstance(
            Global.moduleClassLoader,
            arrayOf(load("android.app.IActivityTaskManager")),
            IActivityManagerHandler(activityTaskManager)
        ))
        LogUtil.d(TAG, "replace IActivityTaskManager success")
    }
}