package me.teble.xposed.autodaily.ui.composable

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.theme.DarkColorPalette
import me.teble.xposed.autodaily.ui.theme.LightColorPalette
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.Theme.Light
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.getCurrentTheme

@Composable
fun RoundedSnackbar(data: SnackbarData) {
    Snackbar(
        snackbarData = data,
        shape = SmootherShape(12.dp),
        containerColor = if (getCurrentTheme() == Light) LightColorPalette.colorBgContainer else DarkColorPalette.colorBgContainer,
        contentColor = if (getCurrentTheme() == Light) LightColorPalette.colorText else DarkColorPalette.colorText
    )
}