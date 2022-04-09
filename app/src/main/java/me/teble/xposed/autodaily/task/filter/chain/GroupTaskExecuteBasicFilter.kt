package me.teble.xposed.autodaily.task.filter.chain

import me.teble.xposed.autodaily.task.filter.FilterChain
import me.teble.xposed.autodaily.task.filter.GroupTaskFilter
import me.teble.xposed.autodaily.task.filter.GroupTaskFilterChain
import me.teble.xposed.autodaily.task.model.Task
import me.teble.xposed.autodaily.task.request.enum.ReqType
import me.teble.xposed.autodaily.task.util.TaskUtil
import me.teble.xposed.autodaily.utils.LogUtil

class GroupTaskExecuteBasicFilter: GroupTaskFilter(
    TAG = "GroupTaskExecuteBasicFilter"
) {
    override fun doFilter(
        relayTaskMap: MutableMap<String, Task>,
        taskList: MutableList<Task>,
        env: MutableMap<String, Any>,
        chain: FilterChain
    ) {
        val groupTask = (chain as GroupTaskFilterChain).taskGroup
        val type = ReqType.getType(groupTask.type.split("|")[0])
        groupTask.preTasks.forEach {
            if (it.cron == "basic") {
                executeBasicTask(type, it, relayTaskMap, env)
            }
        }
        chain.doFilter(relayTaskMap, taskList, env)
    }

    private fun executeBasicTask(reqType: ReqType, task: Task, relayTaskMap: Map<String, Task>, env: MutableMap<String, Any>) {
        LogUtil.d("执行basic请求：${task.id}")
        TaskUtil.execute(reqType, task, relayTaskMap, env)
    }
}