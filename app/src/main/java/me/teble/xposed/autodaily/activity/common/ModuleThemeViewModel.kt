package me.teble.xposed.autodaily.activity.common

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.application.xaApp
import me.teble.xposed.autodaily.data.BlackTheme
import me.teble.xposed.autodaily.data.Theme
import me.teble.xposed.autodaily.data.dataStore
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme
import me.teble.xposed.autodaily.utils.toCode
import me.teble.xposed.autodaily.utils.toTheme

class ModuleThemeViewModel(private val dataStore: DataStore<Preferences> = xaApp.dataStore) :
    ViewModel() {

    val blackTheme = dataStore.data.map {
        it[BlackTheme] ?: false
    }

    val theme = dataStore.data.map {
        val themeCode = it[Theme] ?: 0
        themeCode.toTheme()
    }

    fun updateBlack(black: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.edit {
                it[BlackTheme] = black
            }
        }
    }

    fun updateTheme(themeCode: XAutodailyTheme.Theme) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.edit {
                it[Theme] = themeCode.toCode()
            }
        }
    }


}