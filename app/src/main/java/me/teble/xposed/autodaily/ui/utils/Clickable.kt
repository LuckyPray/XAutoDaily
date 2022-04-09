package me.teble.xposed.autodaily.ui.utils

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/// 存储一些与点击相关的工具类
/**
 * 这是一个水波纹
 * @param color 水波纹颜色
 */
class RippleCustomTheme(private val color: Color) : RippleTheme {
    @Composable
    override fun defaultColor() =
        RippleTheme.defaultRippleColor(
            Color(color = color.toArgb()),
            lightTheme = true
        )

    @Composable
    override fun rippleAlpha(): RippleAlpha =
        RippleTheme.defaultRippleAlpha(
            Color(color = color.toArgb()),
            lightTheme = true
        )
}