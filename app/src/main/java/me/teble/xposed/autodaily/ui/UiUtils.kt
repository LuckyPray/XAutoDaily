package me.teble.xposed.autodaily.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.lang.reflect.Field

@Composable
fun Dp.toPx(): Float {
    val context = LocalContext.current
    return context.resources.displayMetrics.density * value
}

fun Int.toDp(context: Context): Int =
    (this / context.resources.displayMetrics.density).toInt()

fun Dp.toPx(context: Context): Float {
    return context.resources.displayMetrics.density * value
}

@Composable
fun Float.toDp(): Dp {
    val context = LocalContext.current
    return (this / context.resources.displayMetrics.density).dp
}

@Composable
fun Int.toDp(): Dp {
    return this.toFloat().toDp()
}

@SuppressLint("PrivateApi")
fun Context.getStatusBarHeightPx(): Int {
    return try {
        val c = Class.forName("com.android.internal.R\$dimen")
        val obj = c.newInstance()
        val field: Field = c.getField("status_bar_height")
        val x: Int = field.get(obj)!!.toString().toInt()
        this.resources.getDimensionPixelSize(x)
    } catch (e: Exception) {
        val resId = this.resources.getIdentifier("status_bar_height", "dimen", "android")
        this.resources.getDimensionPixelOffset(resId)
    }
}

fun Context.getActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}