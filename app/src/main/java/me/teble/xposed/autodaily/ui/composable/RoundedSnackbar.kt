package me.teble.xposed.autodaily.ui.composable

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import me.teble.xposed.autodaily.activity.common.ThemeViewModel
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.theme.BlackColorPalette
import me.teble.xposed.autodaily.ui.theme.LightColorPalette
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme

@Composable
fun RoundedSnackbar(
    data: SnackbarData,
    themeViewModel: ThemeViewModel,
) {
    val colorTheme = themeViewModel.currentTheme
    val isDark = when (colorTheme) {
        XAutodailyTheme.Theme.Light -> false
        XAutodailyTheme.Theme.Dark -> true
        XAutodailyTheme.Theme.System -> isSystemInDarkTheme()
    }
    val targetColors = if (isDark) BlackColorPalette else LightColorPalette

    Snackbar(
        snackbarData = data,
        shape = SmootherShape(12.dp),
        containerColor = targetColors.colorBgContainer,
        contentColor = targetColors.colorText
    )
}