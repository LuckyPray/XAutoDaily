package me.teble.xposed.autodaily.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

val LightColorPalette = XAutodailyColors(
    themeColor = Color(0xFF0095FF),
    rippleColor = Color(0xFF3C4043),


    colorBgLayout = Color(0xFFF7F7F7),
    colorBgContainer = Color(0xFFFFFFFF),
    colorBgDialog = Color(0xFFFFFFFF),

    colorText = Color(0xFF202124),
    colorTextSecondary = Color(0xFF4F5355),

    colorSwitch = Color(0xFFE6E6E6)

)


val DarkColorPalette = XAutodailyColors(
    themeColor = Color(0xFF47B6FF),
    rippleColor = Color(0xFF3C4043),


    colorBgLayout = Color(0xFF202124),
    colorBgContainer = Color(0xFF303134),
    colorBgDialog = Color(0xFF3C4043),

    colorText = Color(0xFFFFFFFF),
    colorTextSecondary = Color(0xFFFFFFFF).copy(0.6f),


    colorSwitch = Color(0xFF4F5355)
)

val BlackColorPalette = XAutodailyColors(
    themeColor = Color(0xFF47B6FF),
    rippleColor = Color(0xFF3C4043),


    colorBgLayout = Color(0xFF202124),
    colorBgContainer = Color(0xFF303134),
    colorBgDialog = Color(0xFF3C4043),

    colorText = Color(0xFFFFFFFF),
    colorTextSecondary = Color(0xFFFFFFFF).copy(0.6f),


    colorSwitch = Color(0xFF4F5355)
)

/**
 * 这是颜色对应的类
 */
@Immutable
data class XAutodailyColors(
    val themeColor: Color,
    val rippleColor: Color,
    val colorBgLayout: Color,
    val colorBgContainer: Color,
    val colorBgDialog: Color,
    val colorText: Color,
    val colorTextSecondary: Color,
    val colorSwitch: Color
)