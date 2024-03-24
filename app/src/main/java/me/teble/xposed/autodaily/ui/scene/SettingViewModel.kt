package me.teble.xposed.autodaily.ui.scene

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.hook.base.hostContext
import me.teble.xposed.autodaily.hook.config.Config
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.utils.FileUtil
import me.teble.xposed.autodaily.utils.LogUtil
import java.io.File

@Stable
class SettingViewModel : ViewModel() {

    var showTaskToast by mutableStateOf(ConfUnit.showTaskToast)

    var usedThreadPool by mutableStateOf(ConfUnit.usedThreadPool)

    var taskNotification by mutableStateOf(ConfUnit.enableTaskNotification)

    var taskExceptionNotification by mutableStateOf(ConfUnit.enableTaskExceptionNotification)

    var logToXposed by mutableStateOf(ConfUnit.logToXposed)

    var debugLog by mutableStateOf(ConfUnit.enableDebugLog)

    var snackbarHostState = SnackbarHostState()


    fun logSaveResult(uri: Uri?) {
        uri?.let {
            viewModelScope.launch(IO) {
                try {
                    hostContext.contentResolver.openFileDescriptor(uri, "wt").use { zipFd ->
                        zipFd?.let {
                            FileUtil.saveLogs(zipFd)
                        }
                    }
                    showSnackbar("导出成功")
                } catch (e: Throwable) {
                    LogUtil.e(e, "save log failed")
                }
            }
        }
    }

    fun restoreResult(onNavigateToRestore: () -> Unit): (Uri?) -> Unit {
        return { uri ->
            uri?.let {
                val backupTmpFile = File(Config.mmkvDir, "tmp.zip")
                FileUtil.saveFile(it, backupTmpFile)
                FileUtil.restoreBackupConfig(backupTmpFile, Config.mmkvDir)
                backupTmpFile.delete()
                onNavigateToRestore()
            }
        }

    }

    fun backupSaveResult(uri: Uri?) {
        uri?.let {
            viewModelScope.launch(IO) {
                try {
                    hostContext.contentResolver.openFileDescriptor(uri, "wt").use { zipFd ->
                        zipFd?.let {
                            FileUtil.backupConfig(zipFd)
                        }
                    }
                    showSnackbar("备份成功")
                } catch (e: Throwable) {
                    LogUtil.e(e, "save log failed")
                }
            }
        }
    }


    private fun showSnackbar(text: String) {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(text)
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

}