package me.teble.xposed.autodaily.ui.scene

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.teble.xposed.autodaily.task.model.TaskGroup
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.ui.ConfUnit


class SignViewModel : ViewModel() {

    private val _globalEnable = MutableStateFlow(ConfUnit.globalEnable)
    val globalEnable = _globalEnable.asStateFlow()
    private val _taskGroupsState = MutableStateFlow(emptyList<TaskGroup>())
    val taskGroupsState = _taskGroupsState.asStateFlow()

    init {
        _taskGroupsState.value = ConfigUtil.loadSaveConf().taskGroups
    }

    fun updateGlobalEnable(boolean: Boolean) {
        ConfUnit.globalEnable = boolean
        _globalEnable.value = boolean
    }
}