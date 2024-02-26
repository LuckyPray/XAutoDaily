package me.teble.xposed.autodaily.ui.scene

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.teble.xposed.autodaily.task.model.Task

class SignStateViewModel : ViewModel() {

    private val _tasksState = MutableStateFlow(emptyList<Task>())
    val tasksState = _tasksState.asStateFlow()
}