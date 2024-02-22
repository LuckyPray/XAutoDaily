package me.teble.xposed.autodaily.ui.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@get:NonRestartableComposable
val bottomPaddingValue: PaddingValues
    @Composable
    get() = WindowInsets.navigationBars.add(WindowInsets(bottom = 24.dp)).asPaddingValues()

fun Modifier.verticalScrollPadding() = this
    .padding(bottom = 24.dp)
    .navigationBarsPadding()