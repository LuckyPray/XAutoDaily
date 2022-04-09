package me.teble.xposed.autodaily.task.filter

import me.teble.xposed.autodaily.task.filter.chain.GroupTaskCheckExecuteFilter
import me.teble.xposed.autodaily.task.filter.chain.GroupTaskExecuteBasicFilter
import me.teble.xposed.autodaily.task.filter.chain.GroupTaskPreFilter
import me.teble.xposed.autodaily.task.filter.chain.GroupTaskRelayBuilderFilter
import me.teble.xposed.autodaily.task.model.Task
import me.teble.xposed.autodaily.task.model.TaskGroup
import me.teble.xposed.autodaily.task.request.enum.ReqType
import me.teble.xposed.autodaily.task.util.TaskUtil
import me.teble.xposed.autodaily.task.util.formatDate
import me.teble.xposed.autodaily.ui.taskExceptionFlag
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.TimeUtil
import java.util.*

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
            LogUtil.d("当前filter -> ${filter.TAG}")
            filter.doFilter(relayTaskMap, taskList, env, this)
        } else {
            val reqType = ReqType.getType(taskGroup.type.split("|")[0])
            val currentDate = Date(TimeUtil.currentTimeMillis()).formatDate()
            for (task in taskList) {
                var errCount = 0
                var date = ""
                task.taskExceptionFlag.let {
                    it?.let {
                        try {
                            val arr = it.split("|")
                            date = arr[0]
                            errCount = arr[1].toInt()
                        } catch (e: Throwable) {}
                    }
                }
                if (errCount >= 3 && date == currentDate) {
                    LogUtil.d("任务${task.id}今日执行错误次数超过3次，跳过执行")
                    continue
                }
                try {
                    // 进行异常计数，超过一定次数，当天不再执行该任务
                    TaskUtil.execute(reqType, task, relayTaskMap, env.toMutableMap())
                } catch (e: Throwable) {
                    LogUtil.e(e, "执行任务${task.id}异常: ")
                    task.taskExceptionFlag = "$currentDate|${++errCount}"
                }
            }
        }
    }
}