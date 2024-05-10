package me.teble.xposed.autodaily.hook.utils

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
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
        val toast: Toast
        if (Build.BRAND.lowercase(Locale.getDefault()) == "xiaomi") {
            // 小米系统需要使用该方式避免 Toast 弹窗附带宿主应用名前缀
            toast = Toast.makeText(context, null, parse(longDuration))
        } else {
            // 部分系统会因为魔改 makeText 中的 text 参数为 @NonNull 从而导致空指针异常
            toast = Toast(context).apply {
                duration = parse(longDuration)
            }
        }
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