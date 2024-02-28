package me.teble.xposed.autodaily.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue

val LocalXAutodailyShapes = compositionLocalOf {
    XAutodailyShapes
}

val LocalXAutodailyColors = compositionLocalOf {
    LightColorPalette
}


object XAutodailyTheme {
    val shapes: XAutodailyShapes
        @Composable
        get() = LocalXAutodailyShapes.current


    val colors: XAutodailyColors
        @Composable
        get() = LocalXAutodailyColors.current

    enum class Theme {
        Light,
        Dark,
        System;
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XAutodailyTheme(
    isBlack: Boolean = false,
    colorTheme: XAutodailyTheme.Theme = XAutodailyTheme.Theme.Light,
    content: @Composable () -> Unit
) {

    val isDark = when (colorTheme) {
        XAutodailyTheme.Theme.Light -> false
        XAutodailyTheme.Theme.Dark -> true
        XAutodailyTheme.Theme.System -> isSystemInDarkTheme()
    }
    val targetColors = if (isDark)
        if (isBlack) BlackColorPalette else DarkColorPalette
    else LightColorPalette

    val themeColor by animateColorAsState(
        targetValue = targetColors.themeColor,
        animationSpec = tween(600), label = "theme color"
    )

    val rippleColor by animateColorAsState(
        targetValue = targetColors.rippleColor,
        animationSpec = tween(600), label = "ripple color"
    )

    val colorBgLayout by animateColorAsState(
        targetValue = targetColors.colorBgLayout,
        animationSpec = tween(600), label = "ripple color"
    )


    val colorBgContainer by animateColorAsState(
        targetValue = targetColors.colorBgContainer,
        animationSpec = tween(600), label = "ripple color"
    )

    val colorText by animateColorAsState(
        targetValue = targetColors.colorText,
        animationSpec = tween(600), label = "ripple color"
    )

    val colorTextSecondary by animateColorAsState(
        targetValue = targetColors.colorTextSecondary,
        animationSpec = tween(600), label = "ripple color"
    )

    val colorTextTheme by animateColorAsState(
        targetValue = targetColors.colorTextTheme,
        animationSpec = tween(600), label = "ripple color"
    )

    val colorAboutText by animateColorAsState(
        targetValue = targetColors.colorAboutText,
        animationSpec = tween(600), label = "ripple color"
    )
    val colorTextSmallTitle by animateColorAsState(
        targetValue = targetColors.colorTextSmallTitle,
        animationSpec = tween(600), label = "ripple color"
    )

    val colorBgDialog by animateColorAsState(
        targetValue = targetColors.colorBgDialog,
        animationSpec = tween(600), label = "ripple color"
    )
    val colorBgSearch by animateColorAsState(
        targetValue = targetColors.colorBgSearch,
        animationSpec = tween(600), label = "ripple color"
    )

    val colorBgMask by animateColorAsState(
        targetValue = targetColors.colorBgMask,
        animationSpec = tween(600), label = "ripple color"
    )

    val colorBgEdit by animateColorAsState(
        targetValue = targetColors.colorBgEdit,
        animationSpec = tween(600), label = "ripple color"
    )
    val colorSwitch by animateColorAsState(
        targetValue = targetColors.colorSwitch,
        animationSpec = tween(600), label = "ripple color"
    )

    val colorIcon by animateColorAsState(
        targetValue = targetColors.colorIcon,
        animationSpec = tween(600), label = "ripple color"
    )

    val colorSelection by animateColorAsState(
        targetValue = targetColors.colorSelection,
        animationSpec = tween(600), label = "ripple color"
    )

    val colorDialogDivider by animateColorAsState(
        targetValue = targetColors.colorDialogDivider,
        animationSpec = tween(600), label = "ripple color"
    )

    val colorTextSearch by animateColorAsState(
        targetValue = targetColors.colorTextSearch,
        animationSpec = tween(600), label = "ripple color"
    )


    val colors = XAutodailyColors(
        themeColor = themeColor,
        rippleColor = rippleColor,


        colorBgLayout = colorBgLayout,
        colorBgContainer = colorBgContainer,
        colorBgDialog = colorBgDialog,
        colorBgSearch = colorBgSearch,
        colorBgMask = colorBgMask,
        colorBgEdit = colorBgEdit,

        colorText = colorText,
        colorTextSecondary = colorTextSecondary,
        colorTextTheme = colorTextTheme,
        colorTextSmallTitle = colorTextSmallTitle,
        colorTextSearch = colorTextSearch,
        colorAboutText = colorAboutText,

        colorSwitch = colorSwitch,
        colorIcon = colorSwitch,
        colorSelection = colorSelection,
        colorDialogDivider = colorDialogDivider,

    )
    val rippleConfiguration = RippleConfiguration(
        color = colors.rippleColor, rippleAlpha = RippleAlpha(
            pressedAlpha = 0.24f,
            focusedAlpha = 0.24f,
            draggedAlpha = 0.18f,
            hoveredAlpha = 0.14f
        )
    )

    CompositionLocalProvider(
        LocalXAutodailyShapes provides XAutodailyTheme.shapes,
        LocalXAutodailyColors provides colors,
        LocalRippleConfiguration provides rippleConfiguration
    ) {
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(primary = colors.themeColor)
        ) {
            content()
        }
    }

}