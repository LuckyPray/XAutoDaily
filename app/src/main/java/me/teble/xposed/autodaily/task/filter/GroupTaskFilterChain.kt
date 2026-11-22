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
import me.teble.xposed.autodaily.ui.recordError
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.TaskRunControl
import me.teble.xposed.autodaily.utils.TimeUtil
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.CancellationException
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
                if (task.errInfo.isBlockedOn(TimeUtil.getCNDate().formatDate())) {
                    LogUtil.i("任务${task.id}今日异常已达上限，跳过本次执行")
                    continue
                }
                try {
                    XANotification.setContent("正在执行任务${task.id}")
                    TaskUtil.execute(reqType, task, relayTaskMap, env)
                } catch (e: Throwable) {
                    TaskRunControl.ensureActive()
                    if (e is CancellationException || e is InterruptedException) throw e
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
                            val day = TimeUtil.getCNDate().formatDate()
                            if (task.recordError(day).isBlockedOn(day) && ConfUnit.enableTaskExceptionNotification) {
                                XANotification.notify("任务${task.id}执行异常，本次启动跳过执行")
                            }
                        }
                    }
                }
            }
        }
    }
}
