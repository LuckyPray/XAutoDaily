package me.teble.xposed.autodaily.task.request.impl

import me.teble.xposed.autodaily.hook.function.BaseFunction
import me.teble.xposed.autodaily.hook.function.proxy.FunctionPool.favoriteManager
import me.teble.xposed.autodaily.hook.function.proxy.FunctionPool.groupSignInManager
import me.teble.xposed.autodaily.hook.function.proxy.FunctionPool.publicAccountManager
import me.teble.xposed.autodaily.hook.function.proxy.FunctionPool.sendMessageManager
import me.teble.xposed.autodaily.task.model.Task
import me.teble.xposed.autodaily.task.request.ITaskReqUtil
import me.teble.xposed.autodaily.task.request.model.TaskRequest
import me.teble.xposed.autodaily.task.request.model.TaskResponse
import me.teble.xposed.autodaily.task.util.EnvFormatUtil
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.toJsonString
import java.lang.Integer.min

object FuncTaskReqUtil : ITaskReqUtil {
    private const val TAG = "FuncTaskReq"

    override fun create(task: Task, env: MutableMap<String, Any>): List<TaskRequest> {
        return mutableListOf<TaskRequest>().apply {
            val evalUrls = EnvFormatUtil.formatList(task.reqUrl, task.domain, env)
            LogUtil.d("urls -> ${evalUrls.toJsonString()}")
            evalUrls.forEach {
                add(TaskRequest(it, null, null, null, null))
            }
        }
    }

    override fun executor(taskRequest: TaskRequest): TaskResponse {
        val url = taskRequest.url
        val paramMap = splitParam(url)
        LogUtil.d("paramMap -> $paramMap")
        val manager: BaseFunction
        LogUtil.d("--------$url-------")
        when {
            url.startsWith("xa://FavoriteManager/favoriteAllYesterdayVoter") -> {
                LogUtil.d("--------favoriteAllYesterdayVoter-------")
                val maxPage = paramMap["maxPage"]!!.toInt()
                LogUtil.d("maxPage: $maxPage")
                manager = favoriteManager
                val res = favoriteManager.getAllYesterdayVoter(maxPage)
                LogUtil.d("昨日点赞列表人数: ${res?.size}")
                res ?: throw RuntimeException("获取点赞列表失败")
                var favoriteCnt = 0
                res.forEach {
                    LogUtil.d(it.toString())
                    val cnt = it.availableCnt
                    val uin = it.uin
                    if (it.availableCnt > 0) {
                        Thread.sleep(1000)
                        favoriteManager.favorite(uin, min(cnt.toInt(), 20))
                        favoriteCnt++
                    }
                }
            }
            url.startsWith("xa://FavoriteManager/favorite") -> {
                val uin = paramMap["uin"]!!.toLong()
                manager = favoriteManager
                favoriteManager.favorite(uin, 20)
            }
            url.startsWith("xa://SendMessageManager/sendMessage/friend") -> {
                val uin = paramMap["uin"]!!
                val data = paramMap["msg"]!!
                manager = sendMessageManager
                sendMessageManager.sendMessage(uin, data, false)
            }
            url.startsWith("xa://SendMessageManager/sendMessage/group") -> {
                val uin = paramMap["uin"]!!
                val data = paramMap["msg"]!!
                manager = sendMessageManager
                sendMessageManager.sendMessage(uin, data, true)
            }
            url.startsWith("xa://GroupSignInManager/signIn") -> {
                val groupUin = paramMap["uin"]!!
                manager = groupSignInManager
                groupSignInManager.signIn(groupUin)
            }
            url.startsWith("xa://PublicAccountManager/vipPublicAccountSignIn") -> {
                manager = publicAccountManager
                publicAccountManager.vipPublicAccountSignIn()
            }
            else -> return TaskResponse(null, "不支持的功能", 500)
        }
        if (!manager.isInit) {
            return TaskResponse(null, "功能版本不适配，详情请看日志", 201)
        }
        return TaskResponse(null, "执行成功", 200)
    }

    private fun splitParam(url: String): Map<String, String> {
        return mutableMapOf<String, String>().apply {
            if (url.indexOf('?') == -1) {
                return@apply
            }
            val query = url.split("?").last()
            query.split("&").forEach {
                val arr = it.split("=")
                put(arr[0], arr[1])
            }
        }
    }
}