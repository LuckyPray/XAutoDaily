package me.teble.xposed.autodaily.ui.layout


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.waterfall
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


val StatusBarsTopPadding: PaddingValues
    @Composable
    get() = WindowInsets.statusBars.union(
        WindowInsets.captionBar
    ).only(WindowInsetsSides.Top).asPaddingValues()

val HorizontalPadding: PaddingValues
    @Composable
    get() = WindowInsets.waterfall.union(
        WindowInsets(right = 16.dp, left = 16.dp)
    ).only(WindowInsetsSides.Horizontal).asPaddingValues()


val DialogHorizontalPadding: PaddingValues
    @Composable
    get() = WindowInsets.waterfall.union(
        WindowInsets(right = 32.dp, left = 32.dp)
    ).only(WindowInsetsSides.Horizontal).asPaddingValues()

val DialogVerticalPadding: PaddingValues
    @Composable
    get() = WindowInsets.statusBars
        .union(WindowInsets.captionBar)
        .only(WindowInsetsSides.Top)
        .union(WindowInsets(top = 20.dp, bottom = 20.dp))
        .only(WindowInsetsSides.Vertical)
        .asPaddingValues()

val NavigationBarsBottomPadding: PaddingValues
    @Composable
    get() = WindowInsets.navigationBars.union(
        WindowInsets.captionBar
    ).union(WindowInsets(bottom = 16.dp)).only(WindowInsetsSides.Bottom).asPaddingValues()


@Composable
fun Modifier.defaultNavigationBarPadding() = this
    .padding(NavigationBarsBottomPadding)