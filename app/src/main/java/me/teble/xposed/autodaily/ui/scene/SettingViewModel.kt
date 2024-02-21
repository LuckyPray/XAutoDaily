package me.teble.xposed.autodaily.ui.scene

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.ui.ConfUnit

class SettingViewModel : ViewModel() {

    private val _showTaskToast = MutableStateFlow(ConfUnit.showTaskToast)
    val showTaskToast = _showTaskToast.asStateFlow()

    private val _usedThreadPool = MutableStateFlow(ConfUnit.usedThreadPool)
    val usedThreadPool = _usedThreadPool.asStateFlow()

    private val _taskNotification = MutableStateFlow(ConfUnit.enableTaskNotification)
    val taskNotification = _taskNotification.asStateFlow()

    private val _taskExceptionNotification =
        MutableStateFlow(ConfUnit.enableTaskExceptionNotification)
    val taskExceptionNotification = _taskExceptionNotification.asStateFlow()

    private val _logToXposed = MutableStateFlow(ConfUnit.logToXposed)
    val logToXposed = _logToXposed.asStateFlow()

    private val _debugLog = MutableStateFlow(ConfUnit.enableDebugLog)
    val debugLog = _debugLog.asStateFlow()


    private val _snackbarText = MutableSharedFlow<String>()
    val snackbarText = _snackbarText.asSharedFlow()

    fun showSnackbar(text: String) {
        viewModelScope.launch {
            _snackbarText.emit(text)
        }
    }

    fun updateShowTaskToast(boolean: Boolean) {
        ConfUnit.showTaskToast = boolean
        _showTaskToast.value = ConfUnit.showTaskToast
    }

    fun updateUsedThreadPool(boolean: Boolean) {
        ConfUnit.usedThreadPool = boolean
        _usedThreadPool.value = ConfUnit.usedThreadPool
    }

    fun updateTaskExceptionNotification(boolean: Boolean) {
        ConfUnit.enableTaskExceptionNotification = boolean
        _taskExceptionNotification.value = ConfUnit.enableTaskExceptionNotification
    }

    fun updateLogToXposed(boolean: Boolean) {
        ConfUnit.logToXposed = boolean
        _logToXposed.value = ConfUnit.logToXposed
    }

    fun updateTaskNotification(boolean: Boolean) {
        ConfUnit.enableTaskNotification = boolean
        _taskNotification.value = ConfUnit.enableTaskNotification
    }

    fun updateDebugLog(boolean: Boolean) {
        ConfUnit.enableDebugLog = boolean
        _debugLog.value = ConfUnit.enableDebugLog
    }

    fun saveLog() {
//        this.viewModelScope.launch {
//            logSaveLauncher.launch("XAutoDaily_${LocalDateTime.now()}.zip")
//        }
    }


}