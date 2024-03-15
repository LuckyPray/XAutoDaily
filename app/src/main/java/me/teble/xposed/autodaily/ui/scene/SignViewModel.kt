package me.teble.xposed.autodaily.ui.scene

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.ui.ConfUnit

class SignViewModel : ViewModel() {

    var globalEnable by mutableStateOf(ConfUnit.globalEnable)
    val taskGroupsState = mutableStateListOf(*ConfigUtil.loadSaveConf().taskGroups.toTypedArray())

    fun updateGlobalEnable(boolean: Boolean) {
        ConfUnit.globalEnable = boolean
        globalEnable = boolean
    }
}