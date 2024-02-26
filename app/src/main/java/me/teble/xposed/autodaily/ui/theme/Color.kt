package me.teble.xposed.autodaily.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

val LightColorPalette = XAutodailyColors(
    themeColor = Color(0xFF0095FF),
    rippleColor = Color(0xFF3C4043),
)

/**
 * 这是颜色对应的类
 */
@Immutable
data class XAutodailyColors(
    val themeColor: Color,
    val rippleColor: Color

)