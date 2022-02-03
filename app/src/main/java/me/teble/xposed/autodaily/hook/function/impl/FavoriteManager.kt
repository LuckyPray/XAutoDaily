package me.teble.xposed.autodaily.hook.function.impl

import cn.hutool.core.date.DateUtil
import com.tencent.qphone.base.remote.ToServiceMsg
import me.teble.xposed.autodaily.hook.FromServiceMsgHook
import me.teble.xposed.autodaily.hook.function.BaseFunction
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.currentUin
import me.teble.xposed.autodaily.task.model.VoterInfo
import me.teble.xposed.autodaily.utils.LogUtil
import java.util.*

open class FavoriteManager : BaseFunction(
    TAG = "FavoriteManager"
) {

    override fun init() {}

    open fun favorite(targetUin: Long, count: Int = 20) {
        LogUtil.d(TAG, "favorite -> $targetUin count: $count")
        val toServiceMsg: ToServiceMsg = ToServiceMsg(
            "mobileqq.service",
            "$currentUin", "VisitorSvc.ReqFavorite"
        ).apply {
            appSeq = -1
            timeout = 9999L
            extraData.putLong("selfUin", currentUin)
            extraData.putLong("targetUin", targetUin)
            extraData.putByteArray("vCookies", null)
            // 好友1，陌生人66 陌生人能突破非会员10次上限
            extraData.putInt("favoriteSource", 66)
            extraData.putInt("iCount", count)
            extraData.putInt("from", 0)
        }
        QApplicationUtil.appInterface.sendToService(toServiceMsg)
    }

    open fun syncGetVoterList(page: Int, pageSize: Int): List<VoterInfo>? {
        LogUtil.d(TAG, "正在获取点赞列表 page: $page, pageSize: $pageSize")
        val startTime = System.currentTimeMillis()
        val id = "syncGetVoterList"
        FromServiceMsgHook.resMap[id] = null
        getVoterList(page, pageSize)
        while (System.currentTimeMillis() - startTime < 10_000) {
            Thread.sleep(120)
            @Suppress("UNCHECKED_CAST")
            val tmp = FromServiceMsgHook.resMap[id] as List<VoterInfo>?
            tmp?.let {
                FromServiceMsgHook.resMap.remove(id)
                return it
            }
        }
        LogUtil.i(TAG, "尝试小程序登录，获取点赞列表超时")
        return null
    }

    open fun getAllYesterdayVoter(maxPage: Int): List<VoterInfo>? {
        LogUtil.d(TAG, "正在获取前${maxPage}页的点赞列表")
        val beginOfYesterday = DateUtil.beginOfDay(Date()).time / 1000 - 24 * 60 * 60
        val mutableList = mutableListOf<VoterInfo>()
        for (i in 1..maxPage) {
            val list = syncGetVoterList(i, 30)
            LogUtil.d(TAG, "i: $i, list -> $list")
            list?.let {
                if (it.isEmpty()) {
                    return mutableList
                }
                it.forEach {
                    if (it.lastTime < beginOfYesterday) {
                        return mutableList
                    }
                    mutableList.add(it)
                }
            }
        }
        return mutableList
    }

    private fun getVoterList(page: Int, pageSize: Int) {
        val toServiceMsg = ToServiceMsg(
            "mobileqq.service",
            "$currentUin", "VisitorSvc.ReqGetVoterList"
        ).apply {
            appSeq = -1
            timeout = 9999L
            extraData.putLong("selfUin", currentUin)
            extraData.putLong("targetUin", currentUin)
            extraData.putLong("nextMid", (page - 1L) * pageSize)
            extraData.putInt("pageSize", pageSize)
        }
        QApplicationUtil.appInterface.sendToService(toServiceMsg)
    }

}