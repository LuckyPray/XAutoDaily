package me.teble.xposed.autodaily.task.request.impl

import me.teble.xposed.autodaily.config.Constants
import me.teble.xposed.autodaily.hook.function.proxy.FunctionPool
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.currentUin
import me.teble.xposed.autodaily.task.model.Task
import me.teble.xposed.autodaily.task.request.ITaskReqUtil
import me.teble.xposed.autodaily.task.request.model.TaskRequest
import me.teble.xposed.autodaily.task.request.model.TaskResponse
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.task.util.EnvFormatUtil
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.toJsonString
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import java.io.IOException
import java.lang.Integer.min
import java.text.SimpleDateFormat
import java.util.*

object HttpTaskReqUtil : ITaskReqUtil {
    private const val TAG = "HttpTaskReqUtil"

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
            LogUtil.d(TAG, "urls -> ${evalUrls.toJsonString()}")
            evalUrls.forEach { url ->
                env["req_url"] = url
                val headers = mutableMapOf<String, String>()
                task.reqHeaders?.entries?.forEach {
                    headers[it.key] = EnvFormatUtil.format(it.value, task.domain, env)
                }
                LogUtil.d(TAG, "header 头构造完毕: $headers")
                var cookie: String? = null
                when (task.domain) {
                    null -> {}
                    "daily.huasteble.cn" -> {
                        val cd = Calendar.getInstance()
                        val sdf = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US)
                        sdf.timeZone = TimeZone.getTimeZone("GMT")
                        val timeStr = sdf.format(cd.time)
                        val signStr = "date: $timeStr\nuin: $currentUin"
                        val secretId = "AKIDl03byn0gmi1cpfgtknn3qESie9c7w6joyLef"
                        val sig: String = ConfigUtil.getTencentDigest(signStr)
                        val authorization = """
                                hmac id="$secretId", algorithm="hmac-sha1", headers="date uin", signature="$sig"
                                """.trimIndent()
                        headers["uin"] = "$currentUin"
                        headers["date"] = timeStr
                        headers["Authorization"] = authorization
                    }
                    // qqDomain
                    else -> {
                        cookie = getQDomainCookies(task.domain)
                    }
                }
                LogUtil.d(TAG, "cookie 构造完毕: ***(${cookie?.length})")
                LogUtil.d(TAG, "开始format data -> ${task.reqData}")
                val bodyList = task.reqData?.let {
                    EnvFormatUtil.formatList(it, task.domain, env)
                }
                LogUtil.d(TAG, "body -> $bodyList")
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
                    addHeader("user-agent", Constants.qqUserAgent)
                }
                req.cookie?.let {
                    addHeader("Cookie", it)
                }
                if (req.data == null) {
                    method(req.method!!, null)
                } else {
                    req.data.let {
                        addHeader("Content-Length", it.length.toString())
                        if (it.startsWith("{") && it.endsWith("}")) {
                            addHeader("Content-Type", "application/json")
                            method(
                                req.method!!,
                                it.toRequestBody("application/json".toMediaTypeOrNull())
                            )
                        } else {
                            addHeader("Content-Type", "application/x-www-form-urlencoded")
                            method(
                                req.method!!,
                                it.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())
                            )
                        }
                    }
                }
            }.build()
            LogUtil.d(TAG, "开始执行请求")
            val response = OkHttpClient().newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            val responseHeadersText = getHeadersText(response.headers.toMultimap())
            LogUtil.d(
                TAG, """request ------------------------------------>
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