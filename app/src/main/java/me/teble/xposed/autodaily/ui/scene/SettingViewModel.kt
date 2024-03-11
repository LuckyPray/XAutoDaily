package me.teble.xposed.autodaily.ui.scene

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.ui.ConfUnit

class SettingViewModel : ViewModel() {

    val showTaskToast = mutableStateOf(ConfUnit.showTaskToast)

    val usedThreadPool = mutableStateOf(ConfUnit.usedThreadPool)

    val taskNotification = mutableStateOf(ConfUnit.enableTaskNotification)

    val taskExceptionNotification = mutableStateOf(ConfUnit.enableTaskExceptionNotification)

    val logToXposed = mutableStateOf(ConfUnit.logToXposed)

    val debugLog = mutableStateOf(ConfUnit.enableDebugLog)


    private val _snackbarText = MutableSharedFlow<String>()
    val snackbarText = _snackbarText.asSharedFlow()

    val themeDialog = mutableStateOf(false)

    fun showThemeDialog() {
        updateThemeDialogState(true)
    }

    fun dismissThemeDialog() {
        updateThemeDialogState(false)
    }

    fun updateThemeDialogState(boolean: Boolean) {
        if (themeDialog.value != boolean) {
            themeDialog.value = boolean
        }
    }

    fun showSnackbar(text: String) {
        viewModelScope.launch {
            _snackbarText.emit(text)
        }
    }

    fun updateShowTaskToast(boolean: Boolean) {
        ConfUnit.showTaskToast = boolean
        showTaskToast.value = ConfUnit.showTaskToast
    }

    fun updateUsedThreadPool(boolean: Boolean) {
        ConfUnit.usedThreadPool = boolean
        usedThreadPool.value = ConfUnit.usedThreadPool
    }

    fun updateTaskExceptionNotification(boolean: Boolean) {
        ConfUnit.enableTaskExceptionNotification = boolean
        taskExceptionNotification.value = ConfUnit.enableTaskExceptionNotification
    }

    fun updateLogToXposed(boolean: Boolean) {
        ConfUnit.logToXposed = boolean
        logToXposed.value = ConfUnit.logToXposed
    }

    fun updateTaskNotification(boolean: Boolean) {
        ConfUnit.enableTaskNotification = boolean
        taskNotification.value = ConfUnit.enableTaskNotification
    }

    fun updateDebugLog(boolean: Boolean) {
        ConfUnit.enableDebugLog = boolean
        debugLog.value = ConfUnit.enableDebugLog
    }

    fun saveLog() {
//        this.viewModelScope.launch {
//            logSaveLauncher.launch("XAutoDaily_${LocalDateTime.now()}.zip")
//        }
    }


}