package me.teble.xposed.autodaily.task.request.impl

import me.teble.xposed.autodaily.config.qqUserAgent
import me.teble.xposed.autodaily.hook.function.proxy.FunctionPool
import me.teble.xposed.autodaily.task.model.Task
import me.teble.xposed.autodaily.task.request.ITaskReqUtil
import me.teble.xposed.autodaily.task.request.model.TaskRequest
import me.teble.xposed.autodaily.task.request.model.TaskResponse
import me.teble.xposed.autodaily.task.util.EnvFormatUtil
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.toJsonString
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import java.io.IOException
import java.lang.Integer.min
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

object HttpTaskReqUtil : ITaskReqUtil {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .followRedirects(false)
        .connectionPool(ConnectionPool(32, 5, TimeUnit.SECONDS))
        .dispatcher(Dispatcher().apply {
            maxRequestsPerHost = 10
        })
        .build()

    object METHOD {
        private const val GET = "GET"
        private const val POST = "POST"
        private const val PUT = "PUT"
        private const val PATCH = "PATCH"
        private const val HEAD = "HEAD"
        private const val DELETE = "DELETE"
        private const val TRACE = "TRACE"
    }

    override fun create(
        task: Task,
        env: MutableMap<String, Any>
    ): List<TaskRequest> {
        val res = mutableListOf<TaskRequest>().apply {
            val evalUrls = EnvFormatUtil.formatList(task.reqUrl, task.domain, env)
            LogUtil.d("urls -> ${evalUrls.toJsonString()}")
            evalUrls.forEachIndexed { index, url ->
                env["req_url"] = url
                val headers = mutableMapOf<String, String>()
                task.reqHeaders?.entries?.forEach {
                    headers[it.key.lowercase()] = EnvFormatUtil.format(it.value, task.domain, env)
                }
                LogUtil.d("header 头构造完毕: $headers")
                var cookie: String? = null
                when (task.domain) {
                    null -> {}
                    // qqDomain
                    else -> {
                        cookie = getQDomainCookies(task.domain)
                    }
                }
                LogUtil.d("cookie 构造完毕: $cookie(${cookie?.length})")
                LogUtil.d("开始format data -> ${task.reqData}")
                val bodyList = task.reqData?.let {
                    EnvFormatUtil.formatList(it, task.domain, env)
                }
                LogUtil.d("body -> $bodyList")
                bodyList?.forEach {
                    val request = TaskRequest(url, task.reqMethod.uppercase(), headers, cookie, it)
                    add(request)
                } ?: let {
                    val request =
                        TaskRequest(url, task.reqMethod.uppercase(), headers, cookie, null)
                    add(request)
                }
                env.remove("req_url")
            }
        }
        return res
    }

    override fun executor(
        taskRequest: TaskRequest
    ): TaskResponse {
        taskRequest.let { req ->
            val request = Request.Builder().apply {
                url(req.url)
                req.headers?.entries?.forEach {
                    addHeader(it.key, it.value)
                }
                val existsUserAgent = req.headers?.keys?.stream()?.anyMatch {
                    it.lowercase() == "user-agent"
                } ?: false
                if (!existsUserAgent) {
                    addHeader("user-agent", qqUserAgent)
                }
                req.cookie?.let {
                    addHeader("Cookie", it)
                }
                if (req.data == null) {
                    method(req.method!!, null)
                } else {
                    addHeader("Content-Length", req.data.length.toString())
                    if (req.headers?.contains("Content-Type".lowercase()) == true) {
                        val mediaType = req.headers["Content-Type".lowercase()]!!
                        method(req.method!!, req.data.toRequestBody(mediaType.toMediaTypeOrNull()))
                    } else if (req.data.startsWith("{") && req.data.endsWith("}")) {
                        addHeader("Content-Type", "application/json")
                        method(
                            req.method!!,
                            req.data.toRequestBody("application/json".toMediaTypeOrNull())
                        )
                    } else {
                        addHeader("Content-Type", "application/x-www-form-urlencoded")
                        method(
                            req.method!!,
                            req.data.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())
                        )
                    }
                }
            }.build()
            LogUtil.d("开始执行请求")
            val response = client.newCall(request).execute()
            val responseBody: String
            if (response.body?.contentType().toString() == "text/xml") {
                val responseBytes = response.body?.bytes() ?: ByteArray(0)
                var string = String(responseBytes)
                if (string.contains("encoding=\"GBK\"")) {
                    // fix xml default encoding is GBK
                    string = String(responseBytes, Charset.forName("GBK"))
                }
                responseBody = string
            } else {
                responseBody = response.body?.string() ?: ""
            }
            val responseHeadersText = getHeadersText(response.headers.toMultimap())
            LogUtil.d(
                """request ------------------------------------>
                |   method: ${request.method}
                |   url: ${request.url}
                |   headers: ***
                |   body: ${request.body?.bodyString()}
                |   response <------------------------------------
                |   code: ${response.code}
                |   headers: ***
                |   body: ${responseBody.substring(0, min(responseBody.length, 1000))}
                """.trimMargin()
            )
            return TaskResponse(
                responseHeadersText,
                responseBody,
                response.code
            )
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

    private fun RequestBody.bodyString(): String {
        return try {
            val buffer = Buffer()
            writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            ""
        }
    }
}