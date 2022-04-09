package me.teble.xposed.autodaily.hook.function.impl

import me.teble.xposed.autodaily.hook.base.Initiator.load
import me.teble.xposed.autodaily.hook.function.BaseFunction
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil
import me.teble.xposed.autodaily.task.model.TroopInfo
import me.teble.xposed.autodaily.utils.field
import me.teble.xposed.autodaily.utils.fieldValue
import me.teble.xposed.autodaily.utils.getMethods
import mqq.manager.Manager
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*

open class TroopManager : BaseFunction(
    TAG = "TroopManager"
) {
    lateinit var troopManager: Manager
    lateinit var cTroopInfo: Class<*>

    override fun init() {
        cTroopInfo = load("com.tencent.mobileqq.data.troop.TroopInfo")
            ?: load("com.tencent.mobileqq.data.TroopInfo")
                ?: throw RuntimeException("类加载失败 -> TroopInfo")
        load("Lcom/tencent/mobileqq/app/QQManagerFactory;")?.let {
            val troopMgrId = it.fieldValue("TROOP_MANAGER") as Int
            troopManager = QApplicationUtil.appRuntime.getManager(troopMgrId)
            return
        }
        troopManager = QApplicationUtil.appRuntime.getManager(51).let {
            return@let if (it::class.java.name.endsWith("TroopManager")) {
                it
            } else {
                QApplicationUtil.appRuntime.getManager(52)
            }
        }
    }

    open fun getTroopInfoList(): List<TroopInfo>? {
        var m0a: Method? = null
        var m0b: Method? = null
        troopManager.getMethods().forEach { m ->
            if (m.returnType == ArrayList::class.java && Modifier.isPublic(m.modifiers)
                && m.parameterTypes.isEmpty()
            ) {
                if (m.name == "a") {
                    m0a = m
                    return@forEach
                } else {
                    if (m0a == null) {
                        m0a = m
                    } else {
                        m0b = m
                        return@forEach
                    }
                }
            }
        }
        val tx = if (m0b == null) {
            m0a!!.invoke(troopManager) as ArrayList<*>
        } else {
            (if (strcmp(m0a!!.name, m0b!!.name) > 0) m0b else m0a)!!
                .invoke(troopManager) as ArrayList<*>
        }
        val troopInfoList = LinkedList<TroopInfo>()
        val fTroopName = cTroopInfo.field("troopname")!!
        val fTroopUin = cTroopInfo.field("troopuin")!!
        cTroopInfo
        tx.forEach {
            troopInfoList.add(
                TroopInfo(
                    fTroopName.get(it) as String,
                    fTroopUin.get(it) as String
                )
            )
        }
        return troopInfoList
    }

    private fun strcmp(stra: String, strb: String): Int {
        val len = Math.min(stra.length, strb.length)
        for (i in 0 until len) {
            val a = stra[i]
            val b = strb[i]
            if (a != b) {
                return a - b
            }
        }
        return stra.length - strb.length
    }

//    open fun getTroopInfo(troopUin: String) {
//        val troopInfo = troopManager.invoke("b", cTroopInfo, troopUin)
//        return troopInfo
//    }
}