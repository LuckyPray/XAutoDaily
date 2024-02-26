package me.teble.xposed.autodaily.ui.scene

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.task.model.TaskGroup
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.ui.ConfUnit


class SignViewModel : ViewModel() {

    private val _globalEnable = MutableStateFlow(ConfUnit.globalEnable)
    val globalEnable = _globalEnable.asStateFlow()
    private val _taskGroupsState = MutableStateFlow(emptyList<TaskGroup>())
    val taskGroupsState = _taskGroupsState.asStateFlow()

    init {
        viewModelScope.launch(IO) {
            _taskGroupsState.value = ConfigUtil.loadSaveConf().taskGroups
        }

    }

    fun updateGlobalEnable(boolean: Boolean) {
        ConfUnit.globalEnable = boolean
        _globalEnable.value = boolean
    }
}