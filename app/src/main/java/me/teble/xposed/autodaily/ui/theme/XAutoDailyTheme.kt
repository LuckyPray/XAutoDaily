package me.teble.xposed.autodaily.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalXAutodailyShapes = compositionLocalOf {
    XAutodailyShapes
}

object XAutodailyTheme {
    val shapes: XAutodailyShapes
        @Composable
        get() = LocalXAutodailyShapes.current

    enum class Theme {
        Light,
        Dark,
        System
    }

}

@Composable
fun XAutodailyTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalXAutodailyShapes provides XAutodailyTheme.shapes) {
        MaterialTheme(
            colors = MaterialTheme.colors.copy(primary = Color(0xFF0095FF))
        ) {

            content()
        }
    }

}