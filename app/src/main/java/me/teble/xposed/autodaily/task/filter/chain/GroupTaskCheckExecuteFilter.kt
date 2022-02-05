package me.teble.xposed.autodaily.task.filter.chain

import cn.hutool.core.date.DateUtil
import cn.hutool.cron.pattern.CronPattern
import cn.hutool.cron.pattern.CronPatternUtil
import me.teble.xposed.autodaily.task.model.Task
import me.teble.xposed.autodaily.hook.config.Config.accountConfig
import me.teble.xposed.autodaily.task.filter.FilterChain
import me.teble.xposed.autodaily.task.filter.GroupTaskFilter
import me.teble.xposed.autodaily.task.filter.GroupTaskFilterChain
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.task.util.Const
import me.teble.xposed.autodaily.task.util.Const.LAST_EXEC_TIME
import me.teble.xposed.autodaily.task.util.Const.NEXT_SHOULD_EXEC_TIME
import me.teble.xposed.autodaily.task.util.format
import me.teble.xposed.autodaily.task.util.parseDate
import me.teble.xposed.autodaily.utils.LogUtil
import java.util.*

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
            if (checkExecuteTask(it)) {
                LogUtil.d(TAG, "task -> ${it.id} 将被执行")
                taskList.add(it)
            }
        }
        if (taskList.isEmpty()) {
            // 没有任何任务需要执行
            LogUtil.d(TAG, "${taskGroup.id}: 没有任何任务需要执行")
            return
        }
        chain.doFilter(relayTaskMap, taskList, env)
    }

    private fun checkExecuteTask(task: Task): Boolean {
        val enabled = accountConfig.getBoolean("${task.id}#${Const.ENABLE}", false)
        LogUtil.d(TAG, "task -> ${task.id} 当前enable状态：$enabled")
        if (!enabled) {
            return false
        }
        // 获取上次执行任务时间
        val lastExecTime = parseDate(accountConfig.getString("${task.id}#${LAST_EXEC_TIME}"))
        LogUtil.d(TAG, "task -> ${task.id} 上次执行时间：${lastExecTime?.format()}")
        // 不保证一定在有效时间内执行
        val now = Date()
        val nextShouldExecTime =
            parseDate(accountConfig.getString("${task.id}#${NEXT_SHOULD_EXEC_TIME}")) ?: let {
                val time = CronPatternUtil.nextDateAfter(CronPattern(task.cron), now, true)
                accountConfig.putString("${task.id}#${NEXT_SHOULD_EXEC_TIME}", time.format())
                time
            }
        lastExecTime ?: let {
            // 第一次执行任务，如果下次执行时间不在当天，则立即执行
            if (!DateUtil.isSameDay(now, nextShouldExecTime)) {
                return true
            }
        }
        if (nextShouldExecTime < now) {
            return true
        }
        // 否则更新全局下次执行时间
        ConfigUtil.saveAndCheckMostRecentExecTime(nextShouldExecTime)
        LogUtil.d(TAG, "task -> ${task.id} 下次理论执行时间：${nextShouldExecTime.format()}")
        return false
    }
}