package me.teble.xposed.autodaily.task.filter

import function.task.module.Task
import function.task.module.TaskGroup
import me.teble.xposed.autodaily.task.filter.chain.GroupTaskCheckExecuteFilter
import me.teble.xposed.autodaily.task.filter.chain.GroupTaskExecuteBasicFilter
import me.teble.xposed.autodaily.task.filter.chain.GroupTaskPreFilter
import me.teble.xposed.autodaily.task.filter.chain.GroupTaskRelayBuilderFilter
import me.teble.xposed.autodaily.task.request.enum.ReqType
import me.teble.xposed.autodaily.task.util.TaskUtil
import me.teble.xposed.autodaily.utils.LogUtil

class GroupTaskFilterChain(
    val taskGroup: TaskGroup
) : FilterChain {

    private var pos = 0
    private val filters: MutableList<GroupTaskFilter> = ArrayList()

    companion object {
        fun build(taskGroup: TaskGroup): FilterChain {
            val chain = GroupTaskFilterChain(taskGroup)
            chain.add(GroupTaskCheckExecuteFilter())
            chain.add(GroupTaskPreFilter())
            chain.add(GroupTaskRelayBuilderFilter())
            chain.add(GroupTaskExecuteBasicFilter())
            return chain
        }
    }

    fun add(groupTaskFilter: GroupTaskFilter) {
        filters.add(groupTaskFilter)
    }

    override fun doFilter(
        relayTaskMap: MutableMap<String, Task>,
        taskList: MutableList<Task>,
        env: MutableMap<String, Any>
    ) {
        if (pos < filters.size) {
            val filter = filters[pos++]
            LogUtil.d("GroupTaskFilterChain", "当前filter -> ${filter::class.java.simpleName}")
            filter.doFilter(relayTaskMap, taskList, env, this)
        } else {
            val reqType = ReqType.getType(taskGroup.type.split("|")[0])
            for (task in taskList) {
                try {
                    TaskUtil.execute(reqType, task, relayTaskMap, env.toMutableMap())
                } catch (e: Exception) {
                    LogUtil.e(e)
                }
            }
        }
    }
}