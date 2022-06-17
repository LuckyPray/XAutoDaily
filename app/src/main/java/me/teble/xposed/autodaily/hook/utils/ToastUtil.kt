package me.teble.xposed.autodaily.hook.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import me.teble.xposed.autodaily.config.Constants.NAME
import me.teble.xposed.autodaily.hook.base.hostContext

object ToastUtil {
    private val TAG = "ToastUtil"
    private val handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private fun parse(longDuration: Boolean): Int {
        return if (longDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    }

    private fun toast(context: Context, msg: String, longDuration: Boolean) {
        val toast = Toast.makeText(context, null, parse(longDuration))
        toast.setText("$NAME: $msg")
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