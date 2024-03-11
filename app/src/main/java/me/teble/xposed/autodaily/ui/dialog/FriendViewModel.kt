package me.teble.xposed.autodaily.ui.dialog

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import me.teble.xposed.autodaily.hook.function.proxy.FunctionPool

class FriendViewModel : ViewModel() {
    val friendsState =
        mutableStateListOf(*FunctionPool.friendsManager.getFriends().orEmpty().toTypedArray())
}