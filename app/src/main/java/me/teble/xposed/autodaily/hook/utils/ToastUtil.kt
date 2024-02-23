package me.teble.xposed.autodaily.hook.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.agoines.system.miui.MiuiStringToast
import me.teble.xposed.autodaily.hook.base.hostContext

object ToastUtil {

    private val handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private fun toast(context: Context, msg: String, longDuration: Boolean) {
        MiuiStringToast.showStringToast(context, text = msg, longDuration = longDuration)
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