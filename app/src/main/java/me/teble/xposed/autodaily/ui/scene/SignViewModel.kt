package me.teble.xposed.autodaily.ui.scene

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.teble.xposed.autodaily.task.model.TaskGroup
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.ui.ConfUnit


class SignViewModel : ViewModel() {

    var globalEnable by mutableStateOf(ConfUnit.globalEnable)
    var taskGroupsState = mutableStateListOf<TaskGroup>()


    init {
        viewModelScope.launch(IO) {
            val taskGroups = ConfigUtil.loadSaveConf().taskGroups
            withContext(Main) {
                taskGroupsState.clear()
                taskGroupsState.addAll(taskGroups)
            }

        }

    }

    fun updateGlobalEnable(boolean: Boolean) {
        ConfUnit.globalEnable = boolean
        globalEnable = boolean
    }
}