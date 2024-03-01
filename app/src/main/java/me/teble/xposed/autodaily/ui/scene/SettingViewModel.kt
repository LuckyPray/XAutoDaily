package me.teble.xposed.autodaily.ui.scene

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.ui.ConfUnit

class SettingViewModel : ViewModel() {

    var showTaskToast by mutableStateOf(ConfUnit.showTaskToast)

    var usedThreadPool by mutableStateOf(ConfUnit.usedThreadPool)

    var taskNotification by mutableStateOf(ConfUnit.enableTaskNotification)

    var taskExceptionNotification by mutableStateOf(ConfUnit.enableTaskExceptionNotification)

    var logToXposed by mutableStateOf(ConfUnit.logToXposed)

    var debugLog by mutableStateOf(ConfUnit.enableDebugLog)


    private val _snackbarText = MutableSharedFlow<String>()
    val snackbarText = _snackbarText.asSharedFlow()

    var themeDialog by mutableStateOf(false)

    fun showThemeDialog() {
        updateThemeDialogState(true)
    }

    fun dismissThemeDialog() {
        updateThemeDialogState(false)
    }

    fun updateThemeDialogState(boolean: Boolean) {
        if (themeDialog != boolean) {
            themeDialog = boolean
        }
    }

    fun showSnackbar(text: String) {
        viewModelScope.launch {
            _snackbarText.emit(text)
        }
    }

    fun updateShowTaskToast(boolean: Boolean) {
        ConfUnit.showTaskToast = boolean
        showTaskToast = ConfUnit.showTaskToast
    }

    fun updateUsedThreadPool(boolean: Boolean) {
        ConfUnit.usedThreadPool = boolean
        usedThreadPool = ConfUnit.usedThreadPool
    }

    fun updateTaskExceptionNotification(boolean: Boolean) {
        ConfUnit.enableTaskExceptionNotification = boolean
        taskExceptionNotification = ConfUnit.enableTaskExceptionNotification
    }

    fun updateLogToXposed(boolean: Boolean) {
        ConfUnit.logToXposed = boolean
        logToXposed = ConfUnit.logToXposed
    }

    fun updateTaskNotification(boolean: Boolean) {
        ConfUnit.enableTaskNotification = boolean
        taskNotification = ConfUnit.enableTaskNotification
    }

    fun updateDebugLog(boolean: Boolean) {
        ConfUnit.enableDebugLog = boolean
        debugLog = ConfUnit.enableDebugLog
    }

    fun saveLog() {
//        this.viewModelScope.launch {
//            logSaveLauncher.launch("XAutoDaily_${LocalDateTime.now()}.zip")
//        }
    }


}