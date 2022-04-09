package me.teble.xposed.autodaily.task.filter

import me.teble.xposed.autodaily.task.model.Task

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