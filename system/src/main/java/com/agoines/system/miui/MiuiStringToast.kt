package com.agoines.system.miui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import com.agoines.system.miui.model.IconParams
import com.agoines.system.miui.model.Left
import com.agoines.system.miui.model.Right
import com.agoines.system.miui.model.StringToastBean
import com.agoines.system.miui.model.TextParams
import com.agoines.system.miui.res.StringToastBundle
import com.highcapable.betterandroid.system.extension.tool.SystemKind
import com.highcapable.betterandroid.system.extension.tool.SystemKind.HYPEROS
import com.highcapable.betterandroid.system.extension.tool.SystemVersion


object MiuiStringToast {
    fun isSupported(): Boolean {
        return SystemVersion.isHighOrEqualsTo(SystemVersion.T) && SystemKind.current == HYPEROS
    }

    @SuppressLint("WrongConstant")
    fun showStringToast(
        context: Context,
        packageName: String,
        colorType: Int = 1,
        iconResName: String = "ic_check_circle", // if (colorType == 1) "ic_check_circle" else "ic_error",
        text: String?,
    ) {
        runCatching {
            if (isSupported()) {
                val textParams = TextParams(
                    text = text,
                    textColor = if (colorType == 1) colorToInt("#4CAF50") else colorToInt("#E53935")
                )
                val iconParams = IconParams(
                    category = Category.DRAWABLE,
                    iconResName = iconResName,
                    iconType = 1,
                    iconFormat = FileType.SVG
                )
                val left = Left(iconParams = iconParams, textParams = textParams)
                val right = Right(iconParams = iconParams, textParams = textParams)
                val stringToastBean = StringToastBean(left, right)
                val str = stringToastBean.toJson()
                val bundle: Bundle = StringToastBundle.Builder()
                    .setPackageName(packageName)
                    .setStrongToastCategory(StrongToastCategory.TEXT_BITMAP.value)
                    .setTarget(null)
                    .setDuration(2500L)
                    .setLevel(0.0f)
                    .setRapidRate(0.0f)
                    .setCharge(null)
                    .setStringToastChargeFlag(0)
                    .setParam(str)
                    .setStatusBarStrongToast("show_custom_strong_toast")
                    .onCreate()
                val service = context.getSystemService(Context.STATUS_BAR_SERVICE)
                service.javaClass.getMethod(
                    "setStatus",
                    Int::class.javaPrimitiveType,
                    String::class.java,
                    Bundle::class.java
                )
                    .invoke(service, 1, "strong_toast_action", bundle)
            } else {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun colorToInt(color: String?): Int {
        val color1 = Color.parseColor(color)
        val color2 = Color.parseColor("#FFFFFF")
        val color3 = color1 xor color2
        return color3.inv()
    }

    object Category {
        const val RAW = "raw"
        const val DRAWABLE = "drawable"
        const val FILE = "file"
        const val MIPMAP = "mipmap"
    }

    object FileType {
        const val MP4 = "mp4"
        const val PNG = "png"
        const val SVG = "svg"
    }

    enum class StrongToastCategory(var value: String) {
        VIDEO_TEXT("video_text"),
        VIDEO_BITMAP_INTENT("video_bitmap_intent"),
        TEXT_BITMAP("text_bitmap"),
        TEXT_BITMAP_INTENT("text_bitmap_intent"),
        VIDEO_TEXT_TEXT_VIDEO("video_text_text_video")
    }
}