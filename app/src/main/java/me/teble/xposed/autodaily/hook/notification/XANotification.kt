package me.teble.xposed.autodaily.hook.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.github.kyuubiran.ezxhelper.utils.loadClass
import me.teble.xposed.autodaily.R
import me.teble.xposed.autodaily.config.QQClasses.Companion.SplashActivity
import me.teble.xposed.autodaily.hook.base.Global

object XANotification {

    private const val NOTIFICATION_ID = 1101
    private const val CHANNEL_ID = "me.teble.xposed.autodaily.XA_FOREST_NOTIFY_CHANNEL"

    private val notificationManager by lazy {
        Global.hostContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    private lateinit var mNotification: Notification
    private lateinit var builder: Notification.Builder
    private var isStart = false

    fun start() {
        initNotification(Global.hostContext)
        if (isStart) {
            return
        }
        notificationManager.notify(NOTIFICATION_ID, mNotification)
    }

    fun setContent(content: String, onGoing: Boolean = true) {
        mNotification = builder.setContentText(content).setOngoing(onGoing).build()
        notificationManager.notify(NOTIFICATION_ID, mNotification)
    }

    fun stop() {
        if (!isStart) {
            return
        }
        notificationManager.cancel(NOTIFICATION_ID)
        isStart = false
    }

    private fun initNotification(context: Context) {
        if (XANotification::mNotification.isInitialized) {
            return
        }
        val intent: Intent = Intent(context, loadClass(SplashActivity))
        val activity = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(CHANNEL_ID, "XAutoDaily签到提醒",
                NotificationManager.IMPORTANCE_DEFAULT).apply {
                enableLights(false)
                enableVibration(false)
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(notificationChannel)
            builder = Notification.Builder(context, CHANNEL_ID)
                .setContentTitle("XAutoDaily")
                .setSmallIcon(R.drawable.icon_x_auto_daily_2)
                .setOngoing(true)
        } else {
            @Suppress("DEPRECATION")
            builder = Notification.Builder(context)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setContentTitle("XAutoDaily")
                .setSmallIcon(R.drawable.icon_x_auto_daily_2)
                .setOngoing(true)
        }
        mNotification = builder
            .setContentText("开始执行签到")
            .setAutoCancel(false)
            .setContentIntent(activity)
            .build()
    }
}