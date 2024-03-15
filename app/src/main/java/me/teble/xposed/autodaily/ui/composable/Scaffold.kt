package me.teble.xposed.autodaily.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.teble.xposed.autodaily.ui.layout.contentWindowInsets


@Composable
fun XaScaffold(
    text: String,
    containerColor: Color,
    backClick: () -> Unit,
    hasBackProvider: () -> Boolean = { true },
    modifier: Modifier = Modifier,
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    XaScaffold(
        topBar = {
            TopBar(
                text = text,
                backClick = backClick,
                hasBackProvider = hasBackProvider
            )
        },
        modifier = modifier,
        snackbarHost = snackbarHost,
        containerColor = containerColor,
        floatingActionButton = floatingActionButton,
        content = content
    )
}

@Composable
fun XaScaffold(
    topBar: @Composable () -> Unit,
    containerColor: Color,
    snackbarHost: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,

    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        contentWindowInsets = contentWindowInsets,
        topBar = topBar,
        modifier = modifier,
        snackbarHost = snackbarHost,
        containerColor = containerColor,
        floatingActionButton = floatingActionButton
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp),
            content = content
        )
    }
}