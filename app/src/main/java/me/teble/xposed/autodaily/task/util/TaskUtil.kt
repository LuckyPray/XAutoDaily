package me.teble.xposed.autodaily.task.util

import cn.hutool.core.util.ReUtil
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import me.teble.xposed.autodaily.hook.config.Config.accountConfig
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.ksonpath.read
import me.teble.xposed.autodaily.task.cron.pattent.CronPattern
import me.teble.xposed.autodaily.task.cron.pattent.CronPatternUtil
import me.teble.xposed.autodaily.task.model.MsgExtract
import me.teble.xposed.autodaily.task.model.Task
import me.teble.xposed.autodaily.task.request.ReqFactory
import me.teble.xposed.autodaily.task.request.enum.ReqType
import me.teble.xposed.autodaily.task.request.model.TaskResponse
import me.teble.xposed.autodaily.task.util.Const.ENV_VARIABLE
import me.teble.xposed.autodaily.task.util.EnvFormatUtil.format
import me.teble.xposed.autodaily.task.util.EnvFormatUtil.formatList
import me.teble.xposed.autodaily.ui.*
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.TimeUtil
import me.teble.xposed.autodaily.utils.parse
import me.teble.xposed.autodaily.utils.toJsonString
import java.util.*

object TaskUtil {

    fun execute(
        reqType: ReqType,
        task: Task,
        relayMap: Map<String, Task>,
        env: MutableMap<String, Any>
    ): Boolean {
        LogUtil.i("开始执行任务：${task.id}")
        val relays: List<Task> = task.relay?.let {
            mutableListOf<Task>().apply {
                it.split("|").forEach {
                    add(relayMap[it] ?: throw RuntimeException("未知的依赖任务：${it}"))
                }
            }
        } ?: emptyList()
        generateEnv(task, env)
        val repeatNum = format(task.repeat, null, env).toInt()
        LogUtil.d("重复请求次数 -> $repeatNum")
        val currDateStr = TimeUtil.getCNDate().formatDate()
        var lastMsg = "任务没有执行"
        var status = task.taskExecStatus
        if (status.lastExecDate != currDateStr) {
            status = TaskStatus(0, 0, 0, currDateStr)
        } else {
            LogUtil.i("检测到任务中断，可能是执行过程中异常退出导致，从上次位置（index: ${status.reqCount}）执行继续执行")
        }
        for (cnt in 0 until repeatNum) {
            // 执行依赖任务
            relays.forEach {
                LogUtil.i("开始执行依赖任务列表: ${task.relay}")
                // 依赖任务失败，退出执行
                if (!execute(reqType, it, relayMap, env)) {
                    // TODO 多次失败，任务禁用
                    return false
                }
            }
            val taskReqUtil = ReqFactory.getReq(reqType)
            val requests = taskReqUtil.create(task, env)
            status.reqCount += requests.size
            val startIdx = if (task.isBasic || task.isRelayTask) 0 else status.lastExecIdx
            for (i in startIdx until requests.size) {
                Thread.sleep((task.delay * 1000).toLong())
                val response = taskReqUtil.executor(requests[i])
                val result = handleCallback(response, task, env)
                lastMsg = result.msg
                if (result.success) {
                    status.successCount++
                } else {
                    LogUtil.i("任务【${task.id}】执行失败: $lastMsg")
                }
                status.lastExecIdx++
                if (task.isCronTask) {
                    task.taskExecStatus = status
                }
            }
        }
        // 正常cron任务，需要计算下次执行时间
        if (task.cron != null && task.cron != "basic") {
            val now = TimeUtil.getCNDate()
            task.lastExecTime = now.format()
            val realCron = format(task.cron, null, env)
            val nextTime = CronPatternUtil.nextDateAfter(
                TimeZone.getTimeZone("GMT+8"),
                CronPattern(realCron),
                Date(TimeUtil.cnTimeMillis() + 1),
                true)!!
            task.nextShouldExecTime = Date(nextTime.time + TimeUtil.offsetTime).format()
            task.lastExecMsg =
                if (status.reqCount == 1) {
                    if (status.successCount == 1) {
                        if (ConfUnit.showTaskToast) {
                            ToastUtil.send("任务【${task.id}】 $lastMsg")
                        }
                        lastMsg
                    } else {
                        if (ConfUnit.showTaskToast) {
                            ToastUtil.send("任务【${task.id}】 $lastMsg")
                        }
                        lastMsg
                    }
                } else {
                    LogUtil.i(
                        "任务【${task.id}】执行完毕，成功${status.successCount}个，失败${status.reqCount - status.successCount}个"
                    )
                    if (ConfUnit.showTaskToast) {
                        ToastUtil.send("任务【${task.id}】执行完毕，成功${status.successCount}个，失败${status.reqCount - status.successCount}个")
                    }
                    "执行成功${status.successCount}个，失败${status.reqCount - status.successCount}个"
                }
        }
        // 未配置断言器允许失败，防止不重要的依赖任务中断任务流程
        return task.callback.assert == null || status.successCount > 0
    }

    private fun handleCallback(
        response: TaskResponse,
        task: Task,
        env: MutableMap<String, Any>
    ): CallbackResult {
        val callback = task.callback
        var data: String? = response.body.trim()
        callback.dataRegex?.let {
            data = ReUtil.getGroup1(callback.dataRegex, data)
            LogUtil.d("handleCallback -> 正则处理后data为: $data")
        }
        // 是否合理
        data ?: throw RuntimeException("响应为空")
        extract(data!!, response.headersText, callback.extracts, env)
        val success: Boolean = if (callback.assert == null) {
            response.code == 200
        } else {
            format(callback.assert.key, env) == format(callback.assert.value, env)
        }
        var resultMsg = buildString {
            if (success) {
                append("执行成功")
                callback.sucMsg?.let {
                    val msg = format(it, env)
                    if (msg.isNotEmpty()) {
                        append(": $msg")
                    }
                }
            } else {
                append("执行失败")
                callback.errMsg?.let {
                    val msg = format(it, env)
                    if (msg.isNotEmpty()) {
                        append(": $msg")
                    }
                }
            }
        }
        callback.replaces?.forEach {
            resultMsg = ReUtil.replaceAll(resultMsg, it.match, it.replacement)
        }
        LogUtil.i(
            """handleCallback -> 
            |   success: $success
            |   msg: $resultMsg
        """.trimMargin()
        )
        return CallbackResult(success = success, msg = resultMsg)
    }

    private fun extract(
        data: String,
        headersText: String?,
        extracts: List<MsgExtract>?,
        env: MutableMap<String, Any>
    ) {
        var jsonNode: JsonElement? = null
        if (data.startsWith("{") && data.endsWith("}")) {
            jsonNode = data.parse()
        }
        extracts?.forEach {
            LogUtil.d("开始提取变量 -> ${it.toJsonString()}")
            var res: Any
            if (it.match.startsWith("$")) {
                try {
                    res = jsonNode!!.read<JsonElement>(it.match) ?: ""
                    // fix JsonString.toString to be quote
                    when {
                        res is JsonPrimitive && res.isString -> res = res.content
                        res is JsonArray -> res = res.map { (it as JsonPrimitive).content }
                    }
                    LogUtil.d(
                        "JsonPath从 body 提取变量: ${it.match}, ${it.key} = ${res.toJsonString()}"
                    )
                } catch (e: Exception) {
                    LogUtil.e(e)
                    throw e
                }
            } else {
                res = ReUtil.getGroup1(it.match, if (it.from == "headers") headersText else data)
                    ?: ""
                LogUtil.d("正则从 ${it.from} 提取变量: ${it.match}, ${it.key} = $res")
            }
            env["this"] = res
            if (res is List<*>) {
                if (!env.containsKey(it.key)) {
                    env[it.key] = formatList(it.value, env)
                } else {
                    env[it.key] = (env[it.key] as List<*>).plus(formatList(it.value, env))
                }
            } else {
                env[it.key] = format(it.value, env)
            }
            env.remove("this")
        }
    }

    private fun generateEnv(task: Task, env: MutableMap<String, Any>) {
        // 将用户自定义变量的值放进环境变量
        task.envs?.forEach {
            val confValue = getConfEnv(task.id, it.name)
            // 变量默认值为空表示强制要求用户填写，否则抛出异常
            if (it.default.isEmpty()) {
                if (confValue == null || confValue.isEmpty()) {
                    ToastUtil.send("任务【${task.id}】的变量${it.name}不能为空", true)
                    throw RuntimeException("任务【${task.id}】的变量${it.name}不能为空")
                }
            }
            when (it.type) {
                "num" -> {
                    env[it.name] = confValue ?: it.default
                }
                "string" -> {
                    env[it.name] = confValue ?: it.default
                }
                "randString" -> {
                    env[it.name] = (confValue ?: it.default).split("|").random()
                }
                "list", "friend", "group" -> {
                    val value = confValue ?: it.default
                    env[it.name] = value.split(",").toMutableList()
                }
                else -> throw RuntimeException("未处理的变量类型：${it.type}")
            }
            LogUtil.d("开始获取用户自定义变量：${it.name}, 用户自定义值为：${confValue}, 默认值：${it.default}")
        }
    }

    fun getRealCron(task: Task): String {
        val env = mutableMapOf<String, Any>()
        task.envs?.forEach {
            val confValue = getConfEnv(task.id, it.name)
            when(it.name) {
                "hour", "minute" -> {
                    env[it.name] = confValue ?: it.default
                }
            }
        }
        return format(task.cron!!, null, env)
    }

    private fun getConfEnv(taskId: String, key: String): String? {
        return accountConfig.getString("$taskId#$ENV_VARIABLE#$key")
    }

    data class CallbackResult(
        val success: Boolean,
        val msg: String
    )
}