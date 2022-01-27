package me.teble.xposed.autodaily.task.filter

import function.task.module.Task

abstract class GroupTaskFilter(
    val TAG: String
) {

    abstract fun doFilter(
        relayTaskMap: MutableMap<String, Task>,
        taskList: MutableList<Task>,
        env: MutableMap<String, Any>,
        chain: FilterChain
    )
}