package me.teble.xposed.autodaily.ui.scene

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.ui.scene.base.BaseViewModel

@Stable
class SignViewModel : BaseViewModel() {

    var globalEnable by mutableStateOf(ConfUnit.globalEnable)

    fun updateGlobalEnable(boolean: Boolean) {
        ConfUnit.globalEnable = boolean
        globalEnable = boolean
    }
}