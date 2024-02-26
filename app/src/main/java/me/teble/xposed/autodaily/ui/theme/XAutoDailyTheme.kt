package me.teble.xposed.autodaily.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
        System
    }

    @JvmStatic
    fun getCurrentTheme(): Theme {
        return Theme.Light

    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XAutodailyTheme(content: @Composable () -> Unit) {

    val targetColors = LightColorPalette

    val themeColor by animateColorAsState(
        targetValue = targetColors.themeColor,
        animationSpec = tween(600), label = "theme color"
    )

    val rippleColor by animateColorAsState(
        targetValue = targetColors.rippleColor,
        animationSpec = tween(600), label = "ripple color"
    )

    val colors = XAutodailyColors(
        themeColor = themeColor,
        rippleColor = rippleColor
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