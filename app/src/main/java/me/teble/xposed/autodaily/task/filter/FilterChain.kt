package me.teble.xposed.autodaily.task.filter

import function.task.module.Task


interface FilterChain {

    fun doFilter(
        relayTaskMap: MutableMap<String, Task>,
        taskList: MutableList<Task>,
        env: MutableMap<String, Any>
    )
}