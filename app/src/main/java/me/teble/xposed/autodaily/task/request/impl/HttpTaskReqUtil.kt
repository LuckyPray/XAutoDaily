package me.teble.xposed.autodaily.task.request.impl

import cn.hutool.http.HttpUtil
import cn.hutool.http.Method
import function.task.module.Task
import me.teble.xposed.autodaily.hook.function.proxy.FunctionPool
import me.teble.xposed.autodaily.task.request.ITaskReqUtil
import me.teble.xposed.autodaily.task.request.model.TaskRequest
import me.teble.xposed.autodaily.task.request.model.TaskResponse
import me.teble.xposed.autodaily.task.util.EnvFormatUtil
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.fieldValueAs
import me.teble.xposed.autodaily.utils.toJsonString

object HttpTaskReqUtil : ITaskReqUtil {
    private const val TAG = "HttpTaskReqUtil"

    private val METHOD_MAP = mutableMapOf(
        "get" to Method.GET,
        "post" to Method.POST,
        "put" to Method.PUT,
        "patch" to Method.PATCH,
        "head" to Method.HEAD,
        "delete" to Method.DELETE,
        "trace" to Method.TRACE,
        "connect" to Method.CONNECT,
    )

    override fun create(
        task: Task,
        env: MutableMap<String, Any>
    ): List<TaskRequest> {
        val res = mutableListOf<TaskRequest>().apply {
            val evalUrls = EnvFormatUtil.formatList(task.reqUrl, task.qDomain, env)
            LogUtil.d(TAG, "urls -> ${evalUrls.toJsonString()}")
            evalUrls.forEach {
                env["req_url"] = it
                val headers = mutableMapOf<String, String>()
                task.reqHeaders?.entries?.forEach {
                    headers[it.key] = EnvFormatUtil.format(it.value, task.qDomain, env)
                }
                LogUtil.d(TAG, "header 头构造完毕: $headers")
                val cookie = task.qDomain?.let {
                    getQDomainCookies(it)
                } ?: ""
                LogUtil.d(TAG, "cookie 构造完毕: $cookie")
                LogUtil.d(TAG, "开始format data -> ${task.reqData}")
                val body = task.reqData?.let { EnvFormatUtil.format(it, task.qDomain, env) }
                LogUtil.d(TAG, "body -> $body")
                val request = TaskRequest(it, task.reqMethod, headers, cookie, body)
                add(request)
                env.remove("req_url")
            }
        }
        return res
    }

    override fun executor(
        taskRequest: TaskRequest
    ): TaskResponse {
        taskRequest.let { req ->
            var request = HttpUtil.createRequest(METHOD_MAP[req.method], req.url)
            req.headers?.entries?.forEach { entry ->
                LogUtil.d(TAG, "put header: key -> ${entry.key}, value -> ${entry.value}")
                request = request.header(entry.key, entry.value)
            }
            req.cookie?.let {
                LogUtil.d(TAG, "put cookie -> $it")
                request.header("cookie", it, false)
            }
            req.data?.let {
                LogUtil.d(TAG, "put data -> $it")
                request.body(it)
            }
            LogUtil.d(TAG, "开始执行请求")
            LogUtil.d(
                TAG, """request ------------------------------------>
                |   method: ${request.method}
                |   url: ${request.url}
                |   headers: ${request.headers()}
                |   body: ${request.fieldValueAs<String>("body")}
                """.trimMargin()
            )
            request.execute().let { response ->
                LogUtil.d(
                    TAG, """response <------------------------------------
                    |   code: ${response.status}
                    |   body: ${response.body()}
                    """.trimMargin()
                )
                return TaskResponse(getHeadersText(response.headers()), response.body(), response.status)
            }
        }
    }

    private fun getHeadersText(headers: Map<String, List<String>>): String {
        return buildString {
            headers.entries.forEach {
                append(it.key).append(": ").append(it.value.joinToString(";")).append("\n")
            }
        }
    }

    private fun getQDomainCookies(qDomain: String): String {
        return FunctionPool.ticketManager.getCookies(qDomain)
    }
}