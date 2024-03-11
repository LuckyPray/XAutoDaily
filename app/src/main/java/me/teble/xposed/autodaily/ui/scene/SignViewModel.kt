package me.teble.xposed.autodaily.ui.scene

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.ui.ConfUnit


class SignViewModel : ViewModel() {

    val globalEnable = mutableStateOf(ConfUnit.globalEnable)
    val taskGroupsState = mutableStateListOf(*ConfigUtil.loadSaveConf().taskGroups.toTypedArray())

    fun updateGlobalEnable(boolean: Boolean) {
        ConfUnit.globalEnable = boolean
        globalEnable.value = boolean
    }
}