package me.teble.xposed.autodaily.shizuku

import android.os.Bundle
import java.time.LocalDateTime
import java.util.ArrayDeque

/**
 * 记录守护实际观察到的会话交接阶段，广播返回成功不等同于宿主已收到。
 */
internal class HostHandshakeStatus {
    private val values = Bundle()
    private val events = ArrayDeque<String>()

    fun offer(identity: HostProcess, offerId: String) {
        values.putInt("hostPid", identity.pid)
        values.putInt("hostUid", identity.uid)
        values.putString("offerId", offerId)
        values.putString("stage", "offered")
        values.remove("error")
        values.remove("broadcastResult")
        values.remove("sessionId")
        event("向宿主发送交接 pid=${identity.pid} offer=$offerId")
    }

    fun isCurrent(offerId: String): Boolean = values.getString("offerId") == offerId

    fun rejected(offerId: String, reason: String) {
        event("拒绝旧交接 offer=$offerId: $reason")
    }

    fun broadcast(result: Int) {
        values.putInt("broadcastResult", result)
        event("广播系统返回码=$result")
        if (result != 0) failure("系统未接受交接广播，返回码=$result")
    }

    fun received(offerId: String, version: Int, hostInfo: Bundle) {
        values.putString("stage", "received")
        values.putInt("hostVersionCode", hostInfo.getInt("hostVersionCode"))
        values.putString("hostVersionName", hostInfo.getString("hostVersionName"))
        event("宿主已回连 offer=$offerId protocol=$version")
    }

    fun connected(sessionId: String) {
        values.putString("stage", "connected")
        values.putString("sessionId", sessionId)
        values.remove("error")
        event("会话已建立 session=$sessionId")
    }

    fun ready(sessionId: String) {
        values.putString("stage", "ready")
        event("宿主已就绪 session=$sessionId")
    }

    fun closed(sessionId: String, reason: String) {
        values.putString("stage", "closed")
        values.putString("error", reason.take(512))
        event("会话已关闭 session=$sessionId: $reason")
    }

    fun failure(reason: String) {
        values.putString("stage", "failed")
        values.putString("error", reason.take(512))
        event("交接失败: $reason")
    }

    private fun event(message: String) {
        if (events.size >= 24) events.removeFirst()
        events.addLast("${LocalDateTime.now()} ${message.take(512)}")
    }

    fun snapshot(): Bundle = Bundle(values).apply { putString("events", events.joinToString("\n")) }
}
