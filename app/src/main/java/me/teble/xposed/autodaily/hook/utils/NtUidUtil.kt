package me.teble.xposed.autodaily.hook.utils

import com.tencent.mobileqq.qroute.QRoute
import me.teble.xposed.autodaily.hook.base.loadAs
import me.teble.xposed.autodaily.utils.invokeAs

object NtUidUtil {
    private val relationNTUinAndUidApi by lazy {
        QRoute.api(loadAs("com.tencent.relation.common.api.IRelationNTUinAndUidApi"))
    }

    @JvmStatic
    fun getUidFromUin(uin: String): String {
        return relationNTUinAndUidApi.invokeAs("getUidFromUin", uin)!!
    }

    @JvmStatic
    fun getUinFromUid(uid: String): String {
        return relationNTUinAndUidApi.invokeAs("getUinFromUid", uid)!!
    }
}