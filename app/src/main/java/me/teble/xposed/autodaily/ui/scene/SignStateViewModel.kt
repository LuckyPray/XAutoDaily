package me.teble.xposed.autodaily.ui.scene

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import me.teble.xposed.autodaily.task.model.Task

class SignStateViewModel : ViewModel() {
    val tasksState = mutableStateListOf<Task>()
}