package me.teble.xposed.autodaily.hook.bridge

import cn.hutool.core.lang.reflect.MethodHandleUtil
import com.github.kyuubiran.ezxhelper.utils.isPublic
import com.tencent.common.app.AppInterface
import com.tencent.qphone.base.remote.FromServiceMsg
import com.tencent.qphone.base.remote.ToServiceMsg
import me.teble.xposed.autodaily.hook.base.load
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.getFields
import me.teble.xposed.autodaily.utils.getMethods
import mqq.app.MSFServlet
import java.lang.reflect.Method

class MobileQQServiceBridge private constructor(
    private val service: Any,
    private val requestMethod: Method,
    private val responseMethod: Method,
) {

    companion object {
        private val serviceClass by lazy {
            load("com.tencent.mobileqq.service.MobileQQServiceBase")!!
        }

        private val realHandleRequestMethod by lazy {
            serviceClass.getMethods(false).singleOrNull {
                val params = it.parameterTypes
                it.isPublic
                        && it.returnType == Void.TYPE
                        && params.size in 2..3
                        && params.first() == ToServiceMsg::class.java
                        && params.last() == Class::class.java
            }?.also {
                LogUtil.d("realHandleRequestMethod -> $it")
            } ?: throw RuntimeException("初始化失败 -> realHandleRequestMethod")
        }

        private val handleResponseMethod by lazy {
            serviceClass.getMethods(false).singleOrNull {
                val params = it.parameterTypes
                it.isPublic
                        && it.returnType == Void.TYPE
                        && params.size == 4
                        && params[0] == Boolean::class.java
                        && params[1] == ToServiceMsg::class.java
                        && params[2] == FromServiceMsg::class.java
            }?.also {
                LogUtil.d("handleResponseMethod -> $it")
            } ?: throw RuntimeException("初始化失败 -> handleResponseMethod")
        }

        fun resolve(appInterface: AppInterface): MobileQQServiceBridge {
            val serviceInstance = appInterface.getFields(false)
                .filter { serviceClass.isAssignableFrom(it.type) }
                .firstNotNullOfOrNull {
                    LogUtil.d("mQQService -> $it")
                    it.isAccessible = true
                    it.get(appInterface)
                } ?: appInterface.getMethods(false) // qq >= 9.2.70
                .filter {
                    it.isPublic
                            && it.returnType == serviceClass
                            && it.name == "getMobileQQService"
                            && it.parameterTypes.isEmpty()
                }
                .firstNotNullOfOrNull {
                    LogUtil.d("mQQService -> $it")
                    it.isAccessible = true
                    it.invoke(appInterface)
                }
            ?: throw RuntimeException("初始化失败 -> serviceInstance")

            return MobileQQServiceBridge(serviceInstance, realHandleRequestMethod, handleResponseMethod)
        }
    }

    fun sendReq(toServiceMsg: ToServiceMsg, servletClass: Class<out MSFServlet>) {
        toServiceMsg.extraData.putBoolean("req_pb_protocol_flag", true) // qq <= 8.9.8
        toServiceMsg.attributes["req_pb_protocol_flag"] = true // qq >= 8.9.10
        if (requestMethod.parameterTypes.size == 2) {
            MethodHandleUtil.invokeSpecial<Unit>(service, requestMethod, toServiceMsg, servletClass)
        } else {
            MethodHandleUtil.invokeSpecial<Unit>(service, requestMethod, toServiceMsg, null, servletClass)
        }
    }

    fun decodeResp(toServiceMsg: ToServiceMsg, fromServiceMsg: FromServiceMsg) {
        MethodHandleUtil.invokeSpecial<Unit>(service, responseMethod, fromServiceMsg.isSuccess, toServiceMsg, fromServiceMsg, null)
    }
}
