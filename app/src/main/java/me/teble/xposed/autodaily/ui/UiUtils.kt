package me.teble.xposed.autodaily.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.lang.reflect.Field

@Composable
fun Dp.toPx(): Float {
    val context = LocalContext.current
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
    val res by lazy {
        try {
            val c = Class.forName("com.android.internal.R\$dimen")
            val obj = c.newInstance()
            val field: Field = c.getField("status_bar_height")
            val x: Int = field.get(obj)!!.toString().toInt()
            return@lazy this.resources.getDimensionPixelSize(x)
        } catch (e: Exception) {
            val resId = this.resources.getIdentifier("status_bar_height", "dimen", "android")
            return@lazy this.resources.getDimensionPixelOffset(resId)
        }
    }
    return res
}

fun Context.getActivity(): ComponentActivity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is ComponentActivity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.click(
    onClick: (() -> Unit)? = null,
    doubleClick: (() -> Unit)? = null,
    longPress: (() -> Unit)? = null,
): Modifier = this.pointerInput(Unit) {
    detectTapGestures(
        onTap = { onClick?.invoke() },
        onDoubleTap = { doubleClick?.invoke() },
        onLongPress = { longPress?.invoke() },
    )
}