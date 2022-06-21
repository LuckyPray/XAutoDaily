package me.teble.xposed.autodaily.hook.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.github.kyuubiran.ezxhelper.utils.loadClass
import me.teble.xposed.autodaily.R
import me.teble.xposed.autodaily.config.QQClasses.Companion.SplashActivity
import me.teble.xposed.autodaily.hook.base.hostContext
import me.teble.xposed.autodaily.ui.ConfUnit

object XANotification {

    private const val NOTIFICATION_ID = 1101
    private const val CHANNEL_ID = "me.teble.xposed.autodaily.XA_FOREST_NOTIFY_CHANNEL"

    private val notificationManager by lazy {
        hostContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    private lateinit var mNotification: Notification

    @SuppressLint("StaticFieldLeak")
    private lateinit var builder: NotificationCompat.Builder
    private var isStart = false
    private val enabled get() = ConfUnit.enableTaskNotification

    fun start() {
        if (isStart || !enabled) return

        initNotification(hostContext)
        isStart = true
        notificationManager.notify(NOTIFICATION_ID, mNotification)
    }

    fun setContent(content: String, onGoing: Boolean = true, bigText: Boolean = false) {
        if (!enabled) return
        if (!isStart) start()

        mNotification = builder.apply {
            if (bigText) {
                val bigTextStyle = NotificationCompat.BigTextStyle()
                bigTextStyle.setBigContentTitle("XAutoDaily")
                bigTextStyle.bigText(content)
                setStyle(bigTextStyle)
            } else {
                setContentText(content)
            }
            setOngoing(onGoing)
        }.build()
        notificationManager.notify(NOTIFICATION_ID, mNotification)
    }

    fun stop() {
        if (!isStart) return

        notificationManager.cancel(NOTIFICATION_ID)
        isStart = false
    }

    private fun initNotification(context: Context) {
        if (XANotification::mNotification.isInitialized) return

        val intent = Intent(context, loadClass(SplashActivity))
        val activity = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(CHANNEL_ID, "XAutoDaily签到提醒",
                NotificationManager.IMPORTANCE_LOW).apply {
                enableLights(false)
                enableVibration(false)
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(notificationChannel)
            builder = NotificationCompat.Builder(context, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            builder = NotificationCompat.Builder(context)
                .setPriority(Notification.PRIORITY_LOW)
        }
        builder = builder.setContentTitle("XAutoDaily")
            .setSmallIcon(R.drawable.icon_x_auto_daily_2)
            .setOngoing(true)
            .setShowWhen(true)

        mNotification = builder
            .setContentText("开始执行签到")
            .setAutoCancel(false)
            .setContentIntent(activity)
            .build()
    }
}