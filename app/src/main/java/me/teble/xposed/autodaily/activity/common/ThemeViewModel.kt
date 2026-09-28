package me.teble.xposed.autodaily.activity.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme

class ThemeViewModel : ViewModel() {

    var blackTheme by mutableStateOf(
        XAutodailyTheme.getCurrentBlack()
    )

    var currentTheme by mutableStateOf(
        XAutodailyTheme.getCurrentTheme()
    )

    private fun updateBlack(black: Boolean) {
        ConfUnit.blackTheme = black
        blackTheme = black
    }

    private fun updateTheme(theme: XAutodailyTheme.Theme) {
        ConfUnit.theme = theme
        currentTheme = theme
    }

    fun confirmTheme(theme: XAutodailyTheme.Theme, black: Boolean) {
        updateTheme(theme)
        updateBlack(black)
    }


}