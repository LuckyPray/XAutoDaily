package me.teble.xposed.autodaily.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

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

        ) {

            content()
        }
    }

}