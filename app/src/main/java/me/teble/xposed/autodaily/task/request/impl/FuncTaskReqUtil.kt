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
        var message = ""
        var resultCode = 200
        when {
            url.startsWith("xa://FavoriteManager/favoriteAllVoter") -> {
                LogUtil.d("--------favoriteAllVoter-------")
                val maxPage = paramMap["maxPage"]!!.toInt()
                val maxDays = paramMap["maxDays"]!!.toInt()
                LogUtil.d("maxPage: $maxPage, maxDays: $maxDays")
                manager = favoriteManager
                val res = favoriteManager.getAllVoter(maxPage)
                LogUtil.d("满足要求点赞列表人数: ${res.size}")
                var favoriteCnt = 0
                res.forEach {
                    LogUtil.d(it.toString())
                    val cnt = it.availableCnt
                    val uin = it.uin
                    if (it.availableCnt > 0) {
                        Thread.sleep(1000)
                        if (favoriteManager.syncFavorite(uin, min(cnt.toInt(), 20))) {
                            favoriteCnt++
                        }
                    }
                }
                message = "点赞成功: ${favoriteCnt}个, 失败: ${res.size - favoriteCnt}个"
            }
            url.startsWith("xa://FavoriteManager/favorite") -> {
                val uin = paramMap["uin"]!!.toLong()
                manager = favoriteManager
                if (!favoriteManager.syncFavorite(uin, 20)) {
                    message = "执行失败"
                    resultCode = -1
                }
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
                groupSignInManager.syncSignIn(groupUin)
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
        return TaskResponse(null, if (message.isNotEmpty()) "执行成功" else message, resultCode)
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