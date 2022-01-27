package me.teble.xposed.autodaily.task.request

import me.teble.xposed.autodaily.task.request.enum.ReqType
import me.teble.xposed.autodaily.task.request.impl.FuncTaskReqUtil
import me.teble.xposed.autodaily.task.request.impl.HttpTaskReqUtil

object ReqFactory {

    fun getReq(reqType: ReqType): ITaskReqUtil {
        return when(reqType) {
            ReqType.HTTP -> HttpTaskReqUtil
            ReqType.XA_FUNC -> FuncTaskReqUtil
        }
    }
}