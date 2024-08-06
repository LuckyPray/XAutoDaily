package me.teble.xposed.autodaily.hook.function.impl

import me.teble.xposed.autodaily.hook.function.base.BaseFunction
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil
import mqq.manager.TicketManager

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
    }

    private val ticketManagerMap = mutableMapOf<String, TicketManager>()
    private val uin: String get() = "${QApplicationUtil.currentUin}"

    override fun init() {
        getTicketManager()
    }

    private fun getTicketManager(): TicketManager {
        if (ticketManagerMap.containsKey(uin)) {
            return ticketManagerMap.getValue(uin)
        }
        val manager = QApplicationUtil.appRuntime.getManager(2) as TicketManager
        ticketManagerMap[uin] = manager
        return manager
    }

    open fun getSkey(): String? {
        return getTicketManager().getSkey(uin)
    }

    open fun getSuperkey(): String? {
        return getTicketManager().getSuperkey(uin)
    }

    open fun getPt4Token(domain: String?): String? {
        return getTicketManager().getPt4Token(uin, domain)
    }

    open fun getPskey(domain: String): String? {
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