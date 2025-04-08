package me.teble.xposed.autodaily.activity.common

import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import me.teble.xposed.autodaily.application.xaApp
import me.teble.xposed.autodaily.shizuku.ShizukuApi
import me.teble.xposed.autodaily.ui.ConfUnit

@Stable
class SettingViewModel : ViewModel() {

    val shizukuState by derivedStateOf {
        ShizukuApi.isPermissionGranted && ShizukuApi.binderAvailable
    }


    var keepAlive by mutableStateOf(ConfUnit.keepAlive)


    var qqKeepAlive by mutableStateOf(ConfUnit.qKeepAlive)

    var timKeepAlive by mutableStateOf(ConfUnit.timKeepAlive)

    var showThemeDialog by mutableStateOf(false)

    var untrustedTouchEvents by mutableStateOf(ConfUnit.untrustedTouchEvents)

    var hiddenAppIcon by mutableStateOf(ConfUnit.hiddenAppIcon)

    fun updateKeepAliveChecked(bool: Boolean) {
        ConfUnit.keepAlive = bool
        keepAlive = bool

    }

    fun updateQQKeepAlive(bool: Boolean) {
        ConfUnit.qKeepAlive = bool
        qqKeepAlive = bool
    }


    fun updateTimKeepAlive(bool: Boolean) {
        ConfUnit.timKeepAlive = bool
        timKeepAlive = bool
    }


    fun updateUntrustedTouchEvents(bool: Boolean) {
        ConfUnit.untrustedTouchEvents = bool
        untrustedTouchEvents = bool
        ShizukuApi.setUntrustedTouchEvents(bool)
    }


    fun updateHiddenAppIcon(bool: Boolean) {
        ConfUnit.hiddenAppIcon = bool
        hiddenAppIcon = bool
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
        if (showThemeDialog != boolean)
            showThemeDialog = boolean
    }
}