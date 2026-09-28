package me.teble.xposed.autodaily.task.filter

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import me.teble.xposed.autodaily.hook.notification.XANotification
import me.teble.xposed.autodaily.task.exception.TaskTimeoutException
import me.teble.xposed.autodaily.task.filter.chain.GroupTaskCheckExecuteFilter
import me.teble.xposed.autodaily.task.filter.chain.GroupTaskExecuteBasicFilter
import me.teble.xposed.autodaily.task.filter.chain.GroupTaskPreFilter
import me.teble.xposed.autodaily.task.filter.chain.GroupTaskRelayBuilderFilter
import me.teble.xposed.autodaily.task.model.Task
import me.teble.xposed.autodaily.task.model.TaskGroup
import me.teble.xposed.autodaily.task.request.enum.ReqType
import me.teble.xposed.autodaily.task.util.TaskUtil
import me.teble.xposed.autodaily.task.util.formatDate
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.ui.errInfo
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.TimeUtil
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.Date
import javax.net.ssl.SSLHandshakeException

class GroupTaskFilterChain(
    val taskGroup: TaskGroup,
    private var pos: Int = 0,
    private val filters: ImmutableList<GroupTaskFilter>
) : FilterChain {

    companion object {
        fun build(taskGroup: TaskGroup): FilterChain {
            val builder = persistentListOf<GroupTaskFilter>().builder().apply {
                add(GroupTaskCheckExecuteFilter())
                add(GroupTaskPreFilter())
                add(GroupTaskRelayBuilderFilter())
                add(GroupTaskExecuteBasicFilter())
            }
            val chain = GroupTaskFilterChain(taskGroup = taskGroup, filters = builder.build())
            return chain
        }
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
            val reqType = ReqType.getType(taskGroup.type.split("|").first())
            for (task in taskList) {
                val taskException = task.errInfo
                if (taskException.count >= 3) {
                    LogUtil.i("任务${task.id}执行错误次数超过3次，本次启动暂停执行")
                    continue
                }
                try {
                    XANotification.setContent("正在执行任务${task.id}")
                    TaskUtil.execute(reqType, task, relayTaskMap, env)
                } catch (e: Throwable) {
                    when (e) {
                        is ConnectException,
                        is SocketException,
                        is SocketTimeoutException,
                        is UnknownHostException,
                        is InterruptedIOException,
                        is SSLHandshakeException,
                        is TaskTimeoutException -> {
                            LogUtil.e(e, "执行任务 ${task.id} 出现网络异常，等待重试: ")
                        }
                        else -> {
                            LogUtil.e(e, "执行任务 ${task.id} 异常: ")
                            taskException.count++
                            taskException.dateStr = Date(TimeUtil.localTimeMillis()).formatDate()
                            task.errInfo = taskException
                            if (taskException.count >= 3 && ConfUnit.enableTaskExceptionNotification) {
                                XANotification.notify("任务${task.id}执行异常，本次启动跳过执行")
                            }
                        }
                    }
                }
            }
        }
    }
}