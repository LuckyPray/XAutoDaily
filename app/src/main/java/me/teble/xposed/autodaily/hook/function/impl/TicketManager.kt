package me.teble.xposed.autodaily.hook.function.impl

import me.teble.xposed.autodaily.hook.base.hostClassLoader
import me.teble.xposed.autodaily.hook.base.hostVersionCode
import me.teble.xposed.autodaily.hook.base.load
import me.teble.xposed.autodaily.hook.base.loadAs
import me.teble.xposed.autodaily.hook.function.base.BaseFunction
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.getFields
import me.teble.xposed.autodaily.utils.getMethods
import mqq.app.api.IRuntimeService
import mqq.manager.TicketManager
import java.lang.reflect.Proxy
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

open class TicketManager : BaseFunction(
    TAG = "TicketManager"
) {

    companion object {

        private val a2DomainList: Set<String> = setOf(
            "aq.qq.com", "myun.tenpay.com", "wepay.tenpay.com", "ti.qq.com", "weloan.tenpay.com"
        )

        private val pskeyDomainList: Set<String> = setOf(
            "buluo.qq.com", "nbgift.qq.com", "wenwen.sogou.com", "qzone.com", "quan.qq.com",
            "qcloud.com", "igame.qq.com", "docs.qq.com", "mobile.qq.com", "im.qq.com",
            "qidian.qq.com", "mail.qq.com", "fudao.qq.com", "ti.qq.com", "docx.qq.com",
            "vip.qq.com", "baike.sogo.com", "m.tencent.com", "yuanchuang.qq.com", "qzone.qq.com",
            "baike.sogou.com", "wenwen.sogo.com", "qun.qq.com", "gamecenter.qq.com", "y.qq.com",
            "tingting.qq.com", "chong.qq.com", "now.qq.com", "id.qq.com", "yundong.qq.com",
            "tim.qq.com", "kuyoo.com", "kg.qq.com", "qianbao.qq.com", "books.qq.com", "q.qq.com",
            "wa.qq.com", "ke.qq.com", "huayang.qq.com", "tenpay.com", "weishi.qq.com", "mma.qq.com",
            "qqyc.qq.com", "game.qq.com", "openmobile.qq.com", "jiankang.qq.com", "qiye.qq.com"
        )

        private val pt4TokenDomainList: Set<String> = setOf(
            "gamecenter.qq.com", "imgcache.qq.com", "hall.qq.com", "vip.qq.com", "haoma.qq.com",
            "ivac.qq.com"
        )

        private val skeyDomainList: Set<String> = setOf(
            "banfei.zhongan.com", "tos.cn", "qzone.com", "wanggou.com", "weishi.com",
            "www.tencentwm.com", "qq.com", "500zhongcai.com", "weiyun.com", "m.tencent.com",
            "tenpay.com", "paipai.com"
        )

        private val newTicketVersion = 9054 // v9.1.52 最后一个存在 getSuperKey 的正式版本
        private val newPskeyVersion = 5282 // 不确定是哪个版本开始有的 这里直接切换 v9.0.0 为新的获取方法
    }

    private val ticketManagerMap = mutableMapOf<String, TicketManager>()
    private val uin: String get() = "${QApplicationUtil.currentUin}"

    // 9.1.55 彻底砍掉了 getSuperKey，需要通过 "com.tencent.mobileqq.thirdsig.api.IThirdSigService" 获取
    private var thirdSigService: Any? = null
    private var pskeyManager: Any? = null

    override fun init() {
        getTicketManager()
        if (hostVersionCode > newTicketVersion) {
            thirdSigService = QApplicationUtil.appRuntime
                .getRuntimeService(loadAs("com.tencent.mobileqq.thirdsig.api.IThirdSigService"), "all")
        }
        runCatching {
            val psKeyManagerCls = load("com.tencent.mobileqq.pskey.api.IPskeyManager") ?: return
            pskeyManager = QApplicationUtil.appRuntime
                .getRuntimeService(psKeyManagerCls as Class<IRuntimeService>, "all")
        }.onFailure {
            LogUtil.w("pskeyManager get fail")
        }
    }


    private fun getTicketManager(): TicketManager {
        if (ticketManagerMap.containsKey(uin)) {
            return ticketManagerMap[uin]!!
        }
        val manager = QApplicationUtil.appRuntime.getManager(2) as TicketManager
        ticketManagerMap[uin] = manager
        return manager
    }

    open fun getSkey(): String? {
        return getTicketManager().getSkey(uin)
    }

    open fun getSuperkey(): String? {
        thirdSigService?.let { service ->
            LogUtil.d("getSuperKey use thirdSigService: ${service.javaClass}")
            val countDownLatch = CountDownLatch(1)
            var superKey: String? = null
            try {
                val getSuperKeyMethod = service.getMethods(false).first {
                    it.name == "getSuperKey"
                }
                val callbackClass = getSuperKeyMethod.parameterTypes.last()

                val callback = Proxy.newProxyInstance(hostClassLoader, arrayOf(callbackClass)) { _, method, args ->
                    runCatching {
                        if (args.size == 2) { // onFail
                            LogUtil.d("getSuperKey fail, code: ${args[0]}, msg: ${args[1]}")
                        } else { // onSuccess
                            val thirdSigInfo = args[0]
                            val fields = thirdSigInfo.getFields(false)
                                .filter { it.type == ByteArray::class.java }
                            val sigField = fields.sortedBy { it.name }.first()
                            sigField.isAccessible = true
                            val sig = sigField.get(thirdSigInfo)
                            superKey = String(sig as ByteArray)
                            LogUtil.d("getSuperKey success: $superKey")
                        }
                        countDownLatch.countDown()
                    }.onFailure {
                        LogUtil.e(it, "new getSuperKey")
                    }
                }
                getSuperKeyMethod.invoke(service, QApplicationUtil.currentUin, 16, callback)

                countDownLatch.await(15000L, TimeUnit.MILLISECONDS)
            } catch (_: Throwable) {
            }
            superKey?.let { return it }
        }
        // QQ 9.1.55+ 已移除 TicketManager.getSuperkey(String)，编译期绑定的直调会 NoSuchMethodError，
        // 改为反射调用，方法不存在时返回 null
        return runCatching {
            val manager = getTicketManager()
            val method = manager.javaClass.getMethod("getSuperkey", String::class.java)
            method.isAccessible = true
            method.invoke(manager, uin) as? String
        }.onFailure {
            LogUtil.w("TicketManager.getSuperkey 反射调用失败：${it.javaClass.simpleName}: ${it.message}")
        }.getOrNull()
    }

    open fun getPt4Token(domain: String?): String? {
        return getTicketManager().getPt4Token(uin, domain)
    }

    open fun getPskey(domain: String): String? {
        pskeyManager?.let { manager ->
            LogUtil.d("getPskey use pskeyManager: ${manager.javaClass}")
            val countDownLatch = CountDownLatch(1)
            var psKey: String? = null
            try {
                val getPskeyMethod = manager.getMethods(false).first {
                    it.name == "getPskey"
                }
                val callbackClass = getPskeyMethod.parameterTypes.last()

                val callback = Proxy.newProxyInstance(hostClassLoader, arrayOf(callbackClass)) { _, method, args ->
                    runCatching {
                        if (args.size == 2) { // onFail
                            LogUtil.d("getPskey fail, code: ${args[0]}, msg: ${args[1]}")
                        } else { // onSuccess
                            val map = args[0] as Map<String, String>
                            psKey = map[domain]
                            LogUtil.d("getPskey success: $psKey")
                        }
                        countDownLatch.countDown()
                    }.onFailure {
                        LogUtil.e(it, "new getPskey")
                    }
                }
                getPskeyMethod.invoke(manager, arrayOf(domain), callback)

                countDownLatch.await(15000L, TimeUnit.MILLISECONDS)
            } catch (_: Throwable) {
            }
            psKey?.let { return it }
        }
        return getTicketManager().getPskey(uin, domain)
    }

    open fun getStweb(): String? {
        return getTicketManager().getStweb(uin)
    }

    open fun getCookies(domain: String): String {
        var uin = uin
        val skey = getSkey()
        val superkey = getSuperkey()
        val pt4Token = getPt4Token(domain)
        val p_skey = getPskey(domain)
        val cookiesMap: MutableMap<String, String> = HashMap()
        val p_uin = StringBuilder().append('o')
        for (i in 0 until 10 - uin.length) {
            p_uin.append('0')
        }
        p_uin.append(uin)
        uin = p_uin.toString()
        cookiesMap["uin"] = uin
        cookiesMap["p_uin"] = uin
        if (skey != null) {
            cookiesMap["skey"] = skey
        }
        if (superkey != null) {
            cookiesMap["superkey"] = superkey
        }
        if (pt4Token != null) {
            cookiesMap["pt4Token"] = pt4Token
        }
        if (p_skey != null) {
            cookiesMap["p_skey"] = p_skey
        }
        return buildString {
            cookiesMap.forEach { k, v ->
                append("$k=$v; ")
            }
        }.removeSuffix("; ")
    }
}