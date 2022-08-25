package me.teble.xposed.autodaily.task.filter.chain

import me.teble.xposed.autodaily.task.filter.FilterChain
import me.teble.xposed.autodaily.task.filter.GroupTaskFilter
import me.teble.xposed.autodaily.task.filter.GroupTaskFilterChain
import me.teble.xposed.autodaily.task.model.Task
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.ui.errInfo
import me.teble.xposed.autodaily.utils.LogUtil

class GroupTaskCheckExecuteFilter : GroupTaskFilter(
    TAG = "GroupTaskCheckExecuteFilter"
) {

    override fun doFilter(
        relayTaskMap: MutableMap<String, Task>,
        taskList: MutableList<Task>,
        env: MutableMap<String, Any>,
        chain: FilterChain
    ) {
        val taskGroup = (chain as GroupTaskFilterChain).taskGroup
        taskGroup.tasks.forEach {
            if (ConfigUtil.checkExecuteTask(it) && it.errInfo.count < 3) {
                LogUtil.d("task -> ${it.id} 将被执行")
                taskList.add(it)
            }
        }
        if (taskList.isEmpty()) {
            // 没有任何任务需要执行
            LogUtil.d("${taskGroup.id}: 没有任何任务需要执行")
            return
        }
        chain.doFilter(relayTaskMap, taskList, env)
    }
}