package me.teble.xposed.autodaily.hook.utils

import android.R
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import me.teble.xposed.autodaily.config.NAME
import me.teble.xposed.autodaily.hook.base.hostContext
import java.util.Locale


object ToastUtil {

    private val handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private fun parse(longDuration: Boolean): Int {
        return if (longDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    }

    private fun toast(context: Context, msg: String, longDuration: Boolean) {
        lateinit var toast: Toast
        runCatching {
            toast = Toast.makeText(context, null, parse(longDuration))
            // Attempt to invoke interface method 'java.lang.String java.lang.CharSequence.toString()' on a null object reference
            // 不确定性太多了，摆烂 catch
            // toast = Toast(context).apply {
            //     setText("$NAME: $msg") // This Toast was not created with Toast.makeText()
            //     duration = parse(longDuration)
            // }
            toast.setText("$NAME: $msg")
        }.onFailure {
            toast = Toast.makeText(context, msg, parse(longDuration))
        }
        toast.show()
    }

    @JvmOverloads
    fun send(msg: String, longDuration: Boolean = false, must: Boolean = false) {
        send(hostContext, msg, longDuration)
    }

    @JvmOverloads
    fun send(context: Context, msg: String, longDuration: Boolean = false) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            toast(context, msg, longDuration)
        } else {
            handler.post { toast(context, msg, longDuration) }
        }
    }
}