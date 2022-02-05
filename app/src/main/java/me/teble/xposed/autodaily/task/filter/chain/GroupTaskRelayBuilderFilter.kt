package me.teble.xposed.autodaily.task.filter.chain

import me.teble.xposed.autodaily.task.model.Task
import me.teble.xposed.autodaily.task.filter.FilterChain
import me.teble.xposed.autodaily.task.filter.GroupTaskFilter
import me.teble.xposed.autodaily.task.filter.GroupTaskFilterChain

class GroupTaskRelayBuilderFilter: GroupTaskFilter(
    TAG = "GroupTaskRelayBuilderFilter"
) {

    override fun doFilter(
        relayTaskMap: MutableMap<String, Task>,
        taskList: MutableList<Task>,
        env: MutableMap<String, Any>,
        chain: FilterChain
    ) {
        val taskGroup = (chain as GroupTaskFilterChain).taskGroup
        taskGroup.preTasks.forEach {
            if (it.cron == null) {
                relayTaskMap[it.id] = it
            }
        }
        chain.doFilter(relayTaskMap, taskList, env)
    }
}