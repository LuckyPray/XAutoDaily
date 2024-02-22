package me.teble.xposed.autodaily.ui.composable

import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import me.teble.xposed.autodaily.ui.graphics.SmootherShape

@Composable
fun RoundedSnackbar(data: SnackbarData) {
    Snackbar(
        snackbarData = data,
        shape = SmootherShape(12.dp)
    )
}