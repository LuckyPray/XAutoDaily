package me.teble.xposed.autodaily.activity.module

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.getCurrentBlack
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.getCurrentTheme

class MainThemeViewModel : ViewModel() {


    val blackTheme = mutableStateOf(getCurrentBlack())


    val currentTheme = mutableStateOf(getCurrentTheme())


    private fun updateBlack(black: Boolean) {
        ConfUnit.blackTheme = black
        blackTheme.value = black
    }

    private fun updateTheme(theme: XAutodailyTheme.Theme) {
        ConfUnit.theme = theme
        currentTheme.value = theme
    }

    fun confirmTheme(theme: XAutodailyTheme.Theme, black: Boolean) {
        updateTheme(theme)
        updateBlack(black)
    }


}