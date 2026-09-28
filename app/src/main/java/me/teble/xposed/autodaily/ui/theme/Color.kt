package me.teble.xposed.autodaily.ui.theme


import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
val LightColorPalette = XAutodailyColors(
    themeColor = Color(0xFF0095FF),
    rippleColor = Color(0xFF3C4043),
    colorSuccess = Color(0xFF2ECC71),

    colorBgLayout = Color(0xFFF7F7F7),
    colorBgContainer = Color(0xFFFFFFFF),
    colorBgDialog = Color(0xFFFFFFFF),
    colorBgSearch = Color(0xFFF2F2F2),
    colorBgMask = Color(0xFF202124).copy(0.38f),
    colorBgEdit = Color(0xFFF7F7F7),

    colorText = Color(0xFF202124),
    colorTextSecondary = Color(0xFF4F5355),
    colorTextTheme = Color(0xFF3C4043),
    colorTextSmallTitle = Color(0xFF7F98AF),
    colorTextSearch = Color(0xFF999999),
    colorAboutText = Color(0xFF5F6368),

    colorSwitch = Color(0xFFE6E6E6),
    colorIcon = Color(0xFFE6E6E6),
    colorSelection = Color(0xFFD6DDE7),
    colorDialogDivider = Color(0xFFF7F7F7),
)

@Stable
val DarkColorPalette = XAutodailyColors(
    themeColor = Color(0xFF47B6FF),
    rippleColor = Color(0xFFFFFFFF),
    colorSuccess = Color(0xFF60D893),

    colorBgLayout = Color(0xFF202124),
    colorBgContainer = Color(0xFF303134),
    colorBgDialog = Color(0xFF3C4043),
    colorBgSearch = Color(0xFF4F5355),
    colorBgMask = Color(0xFF202124).copy(0.38f),
    colorBgEdit = Color(0xFF3C4043),

    colorText = Color(0xFFFFFFFF),
    colorTextSecondary = Color(0xFFFFFFFF).copy(0.6f),
    colorTextTheme = Color(0xFFFFFFFF).copy(0.87f),
    colorTextSmallTitle = Color(0xFFFFFFFF).copy(0.6f),
    colorTextSearch = Color(0xFFFFFFFF).copy(0.38f),
    colorAboutText = Color(0xFFFFFFFF).copy(0.6f),

    colorSwitch = Color(0xFF4F5355),
    colorIcon = Color(0xFFFFFFFF).copy(0.38f),
    colorSelection = Color(0xFF5F6368),
    colorDialogDivider = Color(0xFF474C4F),
)

@Stable
val BlackColorPalette = XAutodailyColors(
    themeColor = Color(0xFF47B6FF),
    rippleColor = Color(0xFFFFFFFF),
    colorSuccess = Color(0xFF60D893),

    colorBgLayout = Color(0xFF000000),
    colorBgContainer = Color(0xFF303134),
    colorBgDialog = Color(0xFF3C4043),
    colorBgSearch = Color(0xFF4F5355),
    colorBgMask = Color(0xFF000000).copy(0.38f),
    colorBgEdit = Color(0xFF3C4043),

    colorText = Color(0xFFFFFFFF),
    colorTextSecondary = Color(0xFFFFFFFF).copy(0.6f),
    colorTextTheme = Color(0xFFFFFFFF).copy(0.87f),
    colorTextSmallTitle = Color(0xFFFFFFFF).copy(0.6f),
    colorTextSearch = Color(0xFFFFFFFF).copy(0.38f),
    colorAboutText = Color(0xFFFFFFFF).copy(0.6f),

    colorSwitch = Color(0xFF4F5355),
    colorIcon = Color(0xFFFFFFFF).copy(0.38f),

    colorSelection = Color(0xFF5F6368),
    colorDialogDivider = Color(0xFF474C4F),
)

/**
 * 这是颜色对应的类
 */
@Stable
data class XAutodailyColors(
    val themeColor: Color,
    val rippleColor: Color,
    val colorSuccess: Color,

    val colorBgLayout: Color,
    val colorBgContainer: Color,
    val colorBgDialog: Color,
    val colorBgSearch: Color,
    val colorBgMask: Color,
    val colorBgEdit: Color,

    val colorText: Color,
    val colorTextSecondary: Color,
    val colorTextTheme: Color,
    val colorTextSmallTitle: Color,
    val colorTextSearch: Color,
    val colorAboutText: Color,

    val colorSwitch: Color,
    val colorIcon: Color,
    val colorSelection: Color,
    val colorDialogDivider: Color,
)