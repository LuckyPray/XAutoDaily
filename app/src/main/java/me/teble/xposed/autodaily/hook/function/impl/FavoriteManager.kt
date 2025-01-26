package me.teble.xposed.autodaily.hook.function.impl

import cn.hutool.core.date.DateUtil
import cn.hutool.core.lang.reflect.MethodHandleUtil
import com.github.kyuubiran.ezxhelper.utils.isPublic
import com.github.kyuubiran.ezxhelper.utils.paramCount
import com.tencent.qphone.base.remote.FromServiceMsg
import com.tencent.qphone.base.remote.ToServiceMsg
import me.teble.xposed.autodaily.hook.base.load
import me.teble.xposed.autodaily.hook.function.base.BaseFunction
import me.teble.xposed.autodaily.hook.inject.ServletPool.favoriteServlet
import me.teble.xposed.autodaily.hook.inject.servlets.FavoriteServlet
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.appInterface
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.currentUin
import me.teble.xposed.autodaily.task.exception.TaskTimeoutException
import me.teble.xposed.autodaily.task.model.VoterInfo
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.TimeUtil
import me.teble.xposed.autodaily.utils.fieldValue
import me.teble.xposed.autodaily.utils.getFields
import me.teble.xposed.autodaily.utils.getMethods
import java.lang.reflect.Method
import java.util.Date

open class FavoriteManager : BaseFunction(
    TAG = "FavoriteManager"
) {

    lateinit var mRealHandlerReq: Method
    lateinit var mHandlerResponse: Method
    lateinit var mqqService: Any
    override fun init() {
        val cBaseService = load("com.tencent.mobileqq.service.MobileQQServiceBase")!!
        cBaseService.getMethods(false).forEach {
            if (it.returnType == Void.TYPE && it.isPublic) {
                val paramTypes = it.parameterTypes
                if (paramTypes.size >= 2
                    && paramTypes.first() == ToServiceMsg::class.java
                    && paramTypes.last() == Class::class.java) {
                    mRealHandlerReq = it
                    LogUtil.d(it.toString())
                }
            }
        }
        cBaseService.getMethods(false).forEach {
            if (it.returnType == Void.TYPE && it.isPublic && it.paramCount == 4) {
                val paramTypes = it.parameterTypes
                if (paramTypes[0] == Boolean::class.java
                    && paramTypes[1] == ToServiceMsg::class.java
                    && paramTypes[2] == FromServiceMsg::class.java) {
                    mHandlerResponse = it
                    LogUtil.d(it.toString())
                }
            }
        }
        appInterface.getFields(false).forEach {
            if (cBaseService.isAssignableFrom(it.type)) {
                LogUtil.d(it.name)
                it.isAccessible = true
                mqqService = it.get(appInterface)!!
            }
        }
        if (!this::mRealHandlerReq.isInitialized) {
            throw RuntimeException("初始化失败 -> mRealHandlerReq")
        }
        if (!this::mHandlerResponse.isInitialized) {
            throw RuntimeException("初始化失败 -> mHandlerResponse")
        }
        if (!this::mqqService.isInitialized) {
            throw RuntimeException("初始化失败 -> mqqService")
        }
    }

    private fun sendReq(toServiceMsg: ToServiceMsg) {
        if (mRealHandlerReq.parameterTypes.size == 2) {
            MethodHandleUtil.invokeSpecial<Unit>(mqqService, mRealHandlerReq, toServiceMsg, FavoriteServlet::class.java)
        } else {
            MethodHandleUtil.invokeSpecial<Unit>(mqqService, mRealHandlerReq, toServiceMsg, null, FavoriteServlet::class.java)
        }
    }

    private fun decodeResponse(toServiceMsg: ToServiceMsg, fromServiceMsg: FromServiceMsg) {
        MethodHandleUtil.invokeSpecial<Unit>(mqqService, mHandlerResponse, fromServiceMsg.isSuccess, toServiceMsg, fromServiceMsg, null)
    }

    private fun favorite(targetUin: Long, count: Int = 20): Int {
        val toServiceMsg: ToServiceMsg = ToServiceMsg(
            "mobileqq.service",
            "$currentUin", "VisitorSvc.ReqFavorite"
        ).apply {
            appSeq = favoriteServlet.generateSeq()
            timeout = 1000L
            extraData.putLong("selfUin", currentUin)
            extraData.putLong("targetUin", targetUin)
            extraData.putByteArray("vCookies", null)
            // 好友1，陌生人66 陌生人能突破非会员10次上限
            extraData.putInt("favoriteSource", 66)
            extraData.putInt("iCount", count)
            extraData.putInt("from", 0)
        }
        sendReq(toServiceMsg)
        return toServiceMsg.appSeq
    }

    open fun syncFavorite(targetUin: Long, count: Int = 20): Boolean {
        LogUtil.i("favorite -> $targetUin count: $count")
        val seq = favorite(targetUin, count)
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < 10_000) {
            Thread.sleep(120)
            val fromServiceMsg = favoriteServlet.getReceiveAndRemove(seq) ?: continue
            return fromServiceMsg.isSuccess
        }
        LogUtil.i("执行点赞 $targetUin count: $count 超时")
        throw TaskTimeoutException("执行点赞 $targetUin count: $count 超时")
    }

    open fun syncGetVoterList(page: Int, pageSize: Int): List<VoterInfo>? {
        LogUtil.i("正在获取点赞列表 page: $page, pageSize: $pageSize")
        val seq = getVoterList(page, pageSize)
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < 10_000) {
            Thread.sleep(120)
            val fromServiceMsg = favoriteServlet.getReceiveAndRemove(seq) ?: continue
            val toServiceMsg = fromServiceMsg.attributes[FromServiceMsg::class.java.simpleName] as ToServiceMsg
            decodeResponse(toServiceMsg, fromServiceMsg)
            val v = fromServiceMsg.getAttribute("result")
            val list = v?.fieldValue("vVoterInfos") as List<*>?
            LogUtil.d("list -> $list")
            return mutableListOf<VoterInfo>().apply {
                list?.forEach {
                    val lTime = it?.fieldValue("lTime") as Int
                    val lEctID = it.fieldValue("lEctID") as Long
                    val bTodayVotedCnt = it.fieldValue("bTodayVotedCnt") as Short
                    val bVoteCnt = it.fieldValue("bVoteCnt") as Short
                    val bAvailableCnt = it.fieldValue("bAvailableCnt") as Short
                    add(
                        VoterInfo(
                            lEctID, lTime, bVoteCnt, bTodayVotedCnt, bAvailableCnt
                        )
                    )
                }
            }
        }
        LogUtil.w("获取点赞列表超时")
        throw TaskTimeoutException("获取点赞列表超时")
    }

    open fun getAllVoter(maxPage: Int, maxDays: Int = 2): List<VoterInfo> {
        LogUtil.i("正在获取前${maxPage}页的点赞列表")
        val beginTime = DateUtil.beginOfDay(Date(TimeUtil.cnTimeMillis())).time / 1000 - 24 * 60 * 60 * maxDays
        val mutableList = mutableListOf<VoterInfo>()
        for (i in 1..maxPage) {
            val list = syncGetVoterList(i, 30)
            LogUtil.i("i: $i, list -> $list")
            list?.let {
                if (it.isEmpty()) {
                    return mutableList
                }
                it.forEach {
                    if (it.lastTime < beginTime) {
                        return mutableList
                    }
                    mutableList.add(it)
                }
            }
        }
        return mutableList
    }

    private fun getVoterList(page: Int, pageSize: Int): Int {
        val toServiceMsg = ToServiceMsg(
            "mobileqq.service",
            "$currentUin", "VisitorSvc.ReqGetVoterList"
        ).apply {
            appSeq = favoriteServlet.generateSeq()
            timeout = 10000L
            extraData.putLong("selfUin", currentUin)
            extraData.putLong("targetUin", currentUin)
            extraData.putLong("nextMid", (page - 1L) * pageSize)
            extraData.putInt("pageSize", pageSize)
        }
        sendReq(toServiceMsg)
        return toServiceMsg.appSeq
    }

}