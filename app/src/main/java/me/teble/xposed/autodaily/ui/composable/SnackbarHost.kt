package me.teble.xposed.autodaily.ui.composable

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Modifier
import me.teble.xposed.autodaily.activity.common.ThemeViewModel


@androidx.compose.runtime.Composable
fun RoundedSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
    ) {
        RoundedSnackbar(it)
    }
}