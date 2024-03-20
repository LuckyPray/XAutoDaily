package me.teble.xposed.autodaily.ui.layout


import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp


val StatusBarsTopPadding: Dp
    @Composable
    get() = WindowInsets.statusBars.union(
        WindowInsets.captionBar
    ).asPaddingValues().calculateTopPadding()

val NavigationBarsBottomPadding: Dp
    @Composable
    get() = WindowInsets.navigationBars.union(
        WindowInsets.captionBar
    ).asPaddingValues().calculateBottomPadding()


@Composable
fun Modifier.defaultNavigationBarPadding() = this
    .padding(bottom = NavigationBarsBottomPadding)