package me.teble.xposed.autodaily.ui.composable

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun RoundedSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 48.dp,
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
            .navigationBarsPadding()
            .padding(bottom = bottomPadding)
    ) {
        RoundedSnackbar(it)
    }
}
