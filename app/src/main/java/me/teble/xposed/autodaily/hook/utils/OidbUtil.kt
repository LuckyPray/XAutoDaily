package me.teble.xposed.autodaily.hook.utils

import com.tencent.mobileqq.pb.ByteStringMicro
import com.tencent.qphone.base.remote.ToServiceMsg
import me.teble.xposed.autodaily.hook.base.hostVersionName
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.currentUin
import mqq.observer.BusinessObserver
import tencent.im.oidb.oidb_sso.OIDBSSOPkg


object OidbUtil {

    val Boolean.int
        get() = if (this) 1 else 0
    fun makeOIDBPkg(cmdString: String, cmdFlag: Int, serviceType: Int, bodyBuffer: ByteArray): ToServiceMsg {
        return makeOIDBPkg(cmdString, cmdFlag, serviceType, bodyBuffer, 30000L)
    }

    fun makeOIDBPkg(cmdString: String, cmdFlag: Int, serviceType: Int, bodyBuffer: ByteArray, timeout: Long): ToServiceMsg {
        return makeOIDBPkg(cmdString, cmdFlag, serviceType, bodyBuffer, timeout, null, false)
    }

    fun makeOIDBPkg(cmdString: String, cmdFlag: Int, serviceType: Int, bodyBuffer: ByteArray, timeout: Long, businessObserver: BusinessObserver?, z: Boolean): ToServiceMsg {
        val oIDBSSOPkg = OIDBSSOPkg()
        oIDBSSOPkg.uint32_command.set(cmdFlag)
        oIDBSSOPkg.uint32_service_type.set(serviceType)
        oIDBSSOPkg.uint32_result.set(0)
        oIDBSSOPkg.str_client_version.set("android $hostVersionName")
        oIDBSSOPkg.bytes_bodybuffer.set(ByteStringMicro.copyFrom(bodyBuffer))
        val createToServiceMsg: ToServiceMsg = createToServiceMsg(cmdString)
        createToServiceMsg.putWupBuffer(oIDBSSOPkg.toByteArray())
        createToServiceMsg.timeout = timeout
//        addBusinessObserver(createToServiceMsg, businessObserver, z)
        return createToServiceMsg
    }
    fun createToServiceMsg(str: String?): ToServiceMsg {
        return ToServiceMsg("mobileqq.service", "$currentUin", str)
    }
}