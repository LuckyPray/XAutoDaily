package me.teble.xposed.autodaily.task.filter

import me.teble.xposed.autodaily.task.model.Task


interface FilterChain {

    fun doFilter(
        relayTaskMap: MutableMap<String, Task>,
        taskList: MutableList<Task>,
        env: MutableMap<String, Any>
    )
}