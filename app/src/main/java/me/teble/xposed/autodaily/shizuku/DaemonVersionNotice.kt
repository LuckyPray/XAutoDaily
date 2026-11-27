package me.teble.xposed.autodaily.shizuku

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.ComponentCallbacks
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.activity.common.MainActivity
import me.teble.xposed.autodaily.hook.base.hostAppName
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.utils.LogUtil
import java.lang.ref.WeakReference

/**
 * 将握手中发现的版本差异提示给前台宿主。同一次不匹配只记录一次，连接恢复后清除提示。
 */
internal object DaemonVersionNotice : Application.ActivityLifecycleCallbacks {
    private val main = Handler(Looper.getMainLooper())
    private var foreground = WeakReference<Activity>(null)
    private var details: String? = null
    private var restartHost = false
    private var acknowledged = false
    private var dialog: AlertDialog? = null

    fun protocolMismatch(version: Int) {
        update("宿主协议 ${WakeProtocol.VERSION}，守护协议 $version", version > WakeProtocol.VERSION)
    }

    fun connected(info: Bundle) {
        val code = info.getInt("daemonVersionCode")
        val name = info.getString("daemonVersionName")
        if (code > 0 && (code != BuildConfig.VERSION_CODE || (name != null && name != BuildConfig.VERSION_NAME))) {
            update("宿主模块 ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})，守护模块 ${name.orEmpty()} ($code)",
                code > BuildConfig.VERSION_CODE)
        } else {
            update(null, false)
        }
    }

    private fun update(newDetails: String?, shouldRestartHost: Boolean) {
        main.post {
            if (details == newDetails) return@post
            details = newDetails
            restartHost = shouldRestartHost
            acknowledged = false
            dialog?.dismiss()
            dialog = null
            if (newDetails != null) {
                LogUtil.w("任务守护版本不匹配，$newDetails")
                showIfNeeded()
            }
        }
    }

    private fun showIfNeeded() {
        val activity = foreground.get() ?: return
        if (details == null || acknowledged || dialog != null || activity.isFinishing || activity.isDestroyed) return
        val message = if (restartHost) {
            "请重启 ${hostAppName} 以加载新版模块。你也可以打开 XAutoDaily 查看任务守护状态。"
        } else {
            "请打开 XAutoDaily，让任务守护加载新版本。"
        }
        val theme = ConfUnit.theme
        val isDark = theme.isDark(activity.resources.configuration)
        val dialogTheme = if (isDark) {
            android.R.style.Theme_DeviceDefault_Dialog_Alert
        } else {
            android.R.style.Theme_DeviceDefault_Light_Dialog_Alert
        }
        val notice = AlertDialog.Builder(activity, dialogTheme)
            .setTitle("XAutoDaily 任务守护版本不一致")
            .setMessage(message)
            .setPositiveButton("打开 XAutoDaily") { _, _ ->
                acknowledged = true
                val intent = Intent(Intent.ACTION_MAIN)
                    .setClassName(BuildConfig.APPLICATION_ID, MainActivity::class.java.name)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                runCatching { activity.startActivity(intent) }.onFailure {
                    ToastUtil.send("请从系统应用列表打开 XAutoDaily")
                }
            }
            .setNegativeButton("稍后") { _, _ -> acknowledged = true }
            .setOnCancelListener { acknowledged = true }
            .create()
        notice.setOwnerActivity(activity)
        val configurationListener = object : ComponentCallbacks {
            override fun onConfigurationChanged(newConfig: Configuration) {
                if (dialog !== notice || theme.isDark(newConfig) == isDark) return
                dialog = null
                notice.dismiss()
                main.post { showIfNeeded() }
            }

            override fun onLowMemory() = Unit
        }
        notice.setOnDismissListener {
            activity.unregisterComponentCallbacks(configurationListener)
            if (dialog === notice) dialog = null
        }
        try {
            notice.show()
            dialog = notice
            activity.registerComponentCallbacks(configurationListener)
        } catch (_: WindowManager.BadTokenException) {
            // 当前页面已退出，保留提示供下一个前台页面展示。
        }
    }

    override fun onActivityResumed(activity: Activity) {
        foreground = WeakReference(activity)
        activity.window.decorView.post { if (foreground.get() === activity) showIfNeeded() }
    }

    override fun onActivityPaused(activity: Activity) {
        if (foreground.get() === activity) foreground.clear()
        if (dialog?.ownerActivity === activity) {
            dialog?.dismiss()
            dialog = null
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
    override fun onActivityDestroyed(activity: Activity) = Unit
}
