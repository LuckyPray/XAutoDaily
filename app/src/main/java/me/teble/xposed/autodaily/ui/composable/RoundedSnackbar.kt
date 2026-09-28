package me.teble.xposed.autodaily.ui.composable

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme

@Composable
fun RoundedSnackbar(
    data: SnackbarData,
) {
    val colors = XAutodailyTheme.colors

    Snackbar(
        snackbarData = data,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .heightIn(min = 72.dp),
        shape = SmootherShape(16.dp),
        containerColor = colors.colorBgContainer,
        contentColor = colors.colorText
    )
}
