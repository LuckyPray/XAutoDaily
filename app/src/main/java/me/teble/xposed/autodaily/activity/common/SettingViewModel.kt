package me.teble.xposed.autodaily.activity.common

import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import me.teble.xposed.autodaily.application.xaApp
import me.teble.xposed.autodaily.shizuku.ShizukuApi

class SettingViewModel : ViewModel() {

    val shizukuState by derivedStateOf {
        ShizukuApi.isPermissionGranted && ShizukuApi.binderAvailable
    }


    var keepAlive by mutableStateOf(AppConfUnit.keepAlive)


    var qqKeepAlive by mutableStateOf(AppConfUnit.qKeepAlive)

    var timKeepAlive by mutableStateOf(AppConfUnit.timKeepAlive)

    var showThemeDialog by mutableStateOf(false)

    var untrustedTouchEvents by mutableStateOf(AppConfUnit.untrustedTouchEvents)

    var hiddenAppIcon by mutableStateOf(AppConfUnit.hiddenAppIcon)

    fun updateKeepAliveChecked(bool: Boolean) {
        AppConfUnit.keepAlive = bool
        keepAlive = bool

    }

    fun updateQQKeepAlive(bool: Boolean) {
        AppConfUnit.qKeepAlive = bool
        qqKeepAlive = bool
    }


    fun updateTimKeepAlive(bool: Boolean) {
        AppConfUnit.timKeepAlive = bool
        timKeepAlive = bool
    }


    fun updateUntrustedTouchEvents(bool: Boolean) {
        AppConfUnit.untrustedTouchEvents = bool
        untrustedTouchEvents = bool
        ShizukuApi.setUntrustedTouchEvents(bool)
    }


    fun updateHiddenAppIcon(bool: Boolean) {
        AppConfUnit.hiddenAppIcon = bool
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