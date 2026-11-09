package me.teble.xposed.autodaily.shizuku

import android.util.AtomicFile
import org.json.JSONObject
import java.io.File

/**
 * 持久化宿主唤醒计划，调用方须持有 UserService 的守护锁，保存成功后才确认计划同步。
 */
internal class DaemonScheduleStore(directory: File) {
    private val file = AtomicFile(File(directory, "wake-plans.json"))
    val plans = mutableMapOf<String, WakePlan>()

    init {
        if (file.baseFile.exists() || File(file.baseFile.path + ".bak").exists()) {
            val json = JSONObject(file.openRead().bufferedReader().use { it.readText() })
            WakeProtocol.hosts.forEach { pkg ->
                json.optJSONObject(pkg)?.let {
                    plans[pkg] = WakePlan(it.getLong("target"), it.getLong("checkAt"),
                        it.getLong("revision"), it.getInt("attempts"))
                }
            }
        }
    }

    fun save(pkg: String, plan: WakePlan) {
        val updated = plans.toMutableMap().apply { put(pkg, plan) }
        val json = JSONObject()
        updated.forEach { (name, item) -> json.put(name, JSONObject().put("target", item.target)
            .put("checkAt", item.checkAt).put("revision", item.revision).put("attempts", item.attempts)) }
        check(file.baseFile.parentFile!!.let { it.isDirectory || it.mkdirs() })
        val stream = file.startWrite()
        try {
            stream.write(json.toString().toByteArray())
            file.finishWrite(stream)
            plans[pkg] = plan
        } catch (e: Exception) { file.failWrite(stream); throw e }
    }
}
