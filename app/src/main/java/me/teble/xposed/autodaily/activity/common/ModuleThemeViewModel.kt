package me.teble.xposed.autodaily.activity.common

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme

class ModuleThemeViewModel : ViewModel() {

    val blackTheme = mutableStateOf(AppConfUnit.blackTheme)

    val currentTheme = mutableStateOf(AppConfUnit.theme)

    private fun updateBlack(black: Boolean) {
        AppConfUnit.blackTheme = black
        blackTheme.value = black
    }

    private fun updateTheme(theme: XAutodailyTheme.Theme) {
        AppConfUnit.theme = theme
        currentTheme.value = theme
    }

    fun confirmTheme(theme: XAutodailyTheme.Theme, black: Boolean) {
        updateTheme(theme)
        updateBlack(black)
    }



}