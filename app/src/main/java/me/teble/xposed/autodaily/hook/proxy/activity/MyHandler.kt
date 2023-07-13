package me.teble.xposed.autodaily.hook.proxy.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import me.teble.xposed.autodaily.hook.base.hostClassLoader
import me.teble.xposed.autodaily.hook.proxy.ProxyManager
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.field
import me.teble.xposed.autodaily.utils.fieldValueAs
import me.teble.xposed.autodaily.utils.invoke

class MyHandler(private val mDefault: Handler.Callback?) : Handler.Callback {

    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            // LAUNCH_ACTIVITY     sdk <= 8.0
            100 -> {
                runCatching {
                    val record = msg.obj
                    val fIntent = record.field(Intent::class.java)!!
                    val intent = fIntent.get(record) as Intent
                    //获取bundle
                    val bundle: Bundle? = intent.fieldValueAs("mExtras")
                    //设置
                    bundle?.let {
                        it.classLoader = hostClassLoader
                        if (intent.hasExtra(ProxyManager.ACTIVITY_PROXY_INTENT)) {
                            val rIntent = intent.getParcelableExtra<Intent>(
                                ProxyManager.ACTIVITY_PROXY_INTENT
                            )
                            fIntent.set(record, rIntent)
                        }
                    }
                }.onFailure {
                    LogUtil.e(it)
                }
            }
            // EXECUTE_TRANSACTION    8.0+
            159 -> {
                val clientTransaction = msg.obj
                runCatching {
                    clientTransaction?.let { cTrans ->
                        //获取列表
//                        LogUtil.log("clientTransaction -> $cTrans")
                        val clientTransactionItems =
                            cTrans.invoke("getCallbacks") as List<*>
                        for (item in clientTransactionItems) {
                            val clz = item!!::class.java
                            if (clz.name.contains("LaunchActivityItem")) {
                                val fmIntent = item.field(Intent::class.java)!!
                                val wrapper: Intent = fmIntent.get(item) as Intent
                                //获取Bundle
                                val bundle: Bundle? = wrapper.fieldValueAs("mExtras")
                                //设置
                                bundle?.let {
                                    it.classLoader = hostClassLoader
                                    if (wrapper.hasExtra(ProxyManager.ACTIVITY_PROXY_INTENT)) {
                                        val rIntent = wrapper.getParcelableExtra<Intent>(
                                            ProxyManager.ACTIVITY_PROXY_INTENT
                                        )
                                        fmIntent.set(item, rIntent)
                                        // android 12
                                        if (Build.VERSION.SDK_INT >= 31) {
                                            val cActivityThread =
                                                Class.forName("android.app.ActivityThread")
                                            val currentActivityThread =
                                                cActivityThread.getDeclaredMethod("currentActivityThread")
                                            currentActivityThread.isAccessible = true
                                            val activityThread = currentActivityThread.invoke(null)
                                            val acr = activityThread.javaClass.getMethod(
                                                "getLaunchingActivity",
                                                IBinder::class.java
                                            ).invoke(
                                                activityThread,
                                                cTrans.javaClass.getMethod("getActivityToken")
                                                    .invoke(cTrans)
                                            )
                                            if (acr != null) {
                                                val fAcrIntent =
                                                    acr.javaClass.getDeclaredField("intent")
                                                fAcrIntent.isAccessible = true
                                                fAcrIntent[acr] = rIntent
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }.onFailure {
                    LogUtil.e(it)
                }
            }
//            else -> LogUtil.log("code -> " + msg.what)
        }
        return mDefault?.handleMessage(msg) ?: false
    }

}