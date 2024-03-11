package me.teble.xposed.autodaily.activity.common

import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import me.teble.xposed.autodaily.application.xaApp
import me.teble.xposed.autodaily.shizuku.ShizukuApi

class SettingViewModel : ViewModel() {

    val shizukuState = derivedStateOf {
        ShizukuApi.isPermissionGranted.value && ShizukuApi.binderAvailable.value
    }


    val keepAlive = mutableStateOf(AppConfUnit.keepAlive)


    val qqKeepAlive = mutableStateOf(AppConfUnit.qKeepAlive)

    val timKeepAlive = mutableStateOf(AppConfUnit.timKeepAlive)

    val showThemeDialog = mutableStateOf(false)

    val untrustedTouchEvents = mutableStateOf(AppConfUnit.untrustedTouchEvents)

    val hiddenAppIcon = mutableStateOf(AppConfUnit.hiddenAppIcon)

    fun updateKeepAliveChecked(bool: Boolean) {
        AppConfUnit.keepAlive = bool
        keepAlive.value = bool

    }

    fun updateQQKeepAlive(bool: Boolean) {
        AppConfUnit.qKeepAlive = bool
        qqKeepAlive.value = bool
    }


    fun updateTimKeepAlive(bool: Boolean) {
        AppConfUnit.timKeepAlive = bool
        timKeepAlive.value = bool
    }


    fun updateUntrustedTouchEvents(bool: Boolean) {
        AppConfUnit.untrustedTouchEvents = bool
        untrustedTouchEvents.value = bool
        ShizukuApi.setUntrustedTouchEvents(bool)
    }


    fun updateHiddenAppIcon(bool: Boolean) {
        AppConfUnit.hiddenAppIcon = bool
        hiddenAppIcon.value = bool
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
        if (showThemeDialog.value != boolean)
            showThemeDialog.value = boolean
    }
}