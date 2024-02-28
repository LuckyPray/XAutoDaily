package me.teble.xposed.autodaily.activity.common

import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.application.xaApp
import me.teble.xposed.autodaily.data.HiddenAppIcon
import me.teble.xposed.autodaily.data.KeepAlive
import me.teble.xposed.autodaily.data.QKeepAlive
import me.teble.xposed.autodaily.data.TimKeepAlive
import me.teble.xposed.autodaily.data.UntrustedTouchEvents
import me.teble.xposed.autodaily.data.dataStore
import me.teble.xposed.autodaily.shizuku.ShizukuApi

class SettingViewModel(private val dataStore: DataStore<Preferences> = xaApp.dataStore) :
    ViewModel() {

    val shizukuState =
        ShizukuApi.isPermissionGranted.combine(ShizukuApi.binderAvailable) { isPermissionGranted, binderAvailable ->
            isPermissionGranted && binderAvailable
        }


    val keepAlive = dataStore.data.map {
        it[KeepAlive] ?: false
    }

    val qqKeepAlive = dataStore.data.map {
        it[QKeepAlive] ?: false
    }

    val timKeepAlive = dataStore.data.map {
        it[TimKeepAlive] ?: false
    }


    private val _showThemeDialog = MutableStateFlow(false)
    val showThemeDialog = _showThemeDialog.asStateFlow()

    val untrustedTouchEvents = dataStore.data.map {
        it[UntrustedTouchEvents] ?: false
    }

    val hiddenAppIcon = dataStore.data.map {
        it[HiddenAppIcon] ?: false
    }

    fun updateKeepAliveChecked(bool: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.edit {
                it[KeepAlive] = bool
            }
        }

    }

    fun updateQQKeepAlive(bool: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.edit {
                it[QKeepAlive] = bool
            }
        }
    }


    fun updateTimKeepAlive(bool: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.edit {
                it[TimKeepAlive] = bool
            }
        }
    }


    fun updateUntrustedTouchEvents(bool: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.edit {
                it[UntrustedTouchEvents] = bool
            }
        }
        ShizukuApi.setUntrustedTouchEvents(bool)
    }


    fun updateHiddenAppIcon(bool: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.edit {
                it[HiddenAppIcon] = bool
            }
        }
        xaApp.packageManager.setComponentEnabledSetting(
            ComponentName(xaApp, MainActivity::class.java.name + "Alias"),
            if (bool) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    fun showThemeDialog() {
        updateThemeDialogState(true)
    }

    fun dismissThemeDialog() {
        updateThemeDialogState(false)
    }

    fun updateThemeDialogState(boolean: Boolean) {
        if (_showThemeDialog.value != boolean) {
            _showThemeDialog.value = boolean
        }
    }
}