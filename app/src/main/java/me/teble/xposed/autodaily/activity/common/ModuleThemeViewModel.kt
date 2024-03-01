package me.teble.xposed.autodaily.activity.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme

class ModuleThemeViewModel : ViewModel() {

    var blackTheme by mutableStateOf(AppConfUnit.blackTheme)

    var currentTheme by mutableStateOf(AppConfUnit.theme)

    fun updateBlack(black: Boolean) {
        AppConfUnit.blackTheme = black
        blackTheme = black
    }

    fun updateTheme(theme: XAutodailyTheme.Theme) {
        AppConfUnit.theme = theme
        currentTheme = theme
    }


}