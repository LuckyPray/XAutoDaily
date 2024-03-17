package me.teble.xposed.autodaily.activity.common

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme

@Stable
class ModuleThemeViewModel : ViewModel() {

    var blackTheme by mutableStateOf(
        XAutodailyTheme.getAppBlack()
    )

    var currentTheme by mutableStateOf(
        XAutodailyTheme.getAppTheme()
    )

    private fun updateBlack(black: Boolean) {
        AppConfUnit.blackTheme = black
        blackTheme = black
    }

    private fun updateTheme(theme: XAutodailyTheme.Theme) {
        AppConfUnit.theme = theme
        currentTheme = theme
    }

    fun confirmTheme(theme: XAutodailyTheme.Theme, black: Boolean) {
        updateTheme(theme)
        updateBlack(black)
    }


}