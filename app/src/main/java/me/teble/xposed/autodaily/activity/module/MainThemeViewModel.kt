package me.teble.xposed.autodaily.activity.module

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme

class MainThemeViewModel() : ViewModel() {


    private val _blackTheme = MutableStateFlow(ConfUnit.blackTheme)
    val blackTheme = _blackTheme.asStateFlow()


    private val _theme = MutableStateFlow(ConfUnit.theme)
    val theme = _theme.asStateFlow()


    fun updateBlack(black: Boolean) {
        ConfUnit.blackTheme = black
        _blackTheme.value = black
    }

    fun updateTheme(theme: XAutodailyTheme.Theme) {
        ConfUnit.theme = theme
        _theme.value = theme
    }


}