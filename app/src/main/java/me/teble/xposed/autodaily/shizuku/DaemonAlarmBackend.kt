package me.teble.xposed.autodaily.shizuku

import android.content.Context
import android.os.PowerManager
import android.util.Log
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * 管理特权闹钟辅助进程，调用方须持有守护锁；管道确认回包不获取该锁，避免相互等待。
 */
internal class DaemonAlarmBackend(context: Context, userId: Int, private val fired: (String, () -> Unit) -> Unit) {
    private val power = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    private val child = ProcessBuilder("/system/bin/app_process", "/system/bin", DaemonAlarmProcess::class.java.name, userId.toString())
        .apply { environment()["CLASSPATH"] = context.applicationInfo.sourceDir }.start()
    private val output = DataOutputStream(child.outputStream)
    private val ready = CompletableFuture<Int>()
    private val responses = ConcurrentHashMap<String, CompletableFuture<Unit>>()
    @Volatile
    private var closed = false
    @Volatile
    private var broken = false
    val alive get() = !closed && !broken && runCatching { child.exitValue(); false }.getOrDefault(true)
    val uid: Int

    init {
        Thread({
            child.errorStream.bufferedReader().useLines { lines -> lines.forEach { Log.w("XALog", "alarm helper: $it") } }
        }, "xa-alarm-stderr").apply { isDaemon = true; start() }
        Thread({
            try {
                val input = DataInputStream(child.inputStream)
                while (true) {
                    val kind = input.readUTF(); val token = input.readUTF(); val detail = input.readUTF()
                    when (kind) {
                        "READY" -> ready.complete(detail.toInt())
                        "OK" -> responses.remove(token)?.complete(Unit)
                        "ERROR" -> responses.remove(token)?.completeExceptionally(IllegalStateException(detail))
                        "FIRED" -> {
                            val lock = power.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "XAutoDaily:alarm-handoff")
                            lock.setReferenceCounted(false)
                            try {
                                lock.acquire(30_000)
                                fired(token) { if (lock.isHeld) lock.release() }
                            } catch (e: Exception) {
                                if (lock.isHeld) lock.release()
                                Log.e("XALog", "特权闹钟交接失败", e)
                            } finally { send("ACK", token, 0) }
                        }
                    }
                }
            } catch (e: Exception) {
                broken = true
                ready.completeExceptionally(e)
                responses.values.forEach { it.completeExceptionally(e) }
                responses.clear()
                if (!closed) Log.w("XALog", "特权闹钟通道断开，守护进程将重新登记计划", e)
            }
        }, "xa-alarm-events").apply { isDaemon = true; start() }
        try { uid = ready.get(10, TimeUnit.SECONDS) }
        catch (e: Exception) { close(); throw e }
    }

    private fun send(command: String, token: String, at: Long) = synchronized(output) {
        output.writeUTF(command); output.writeUTF(token); output.writeLong(at); output.flush()
    }
    private fun request(command: String, token: String, at: Long = 0) {
        val response = CompletableFuture<Unit>()
        responses[token] = response
        try { send(command, token, at); response.get(5, TimeUnit.SECONDS) }
        catch (e: Exception) { close(); throw e }
        finally { responses.remove(token, response) }
    }
    fun set(token: String, at: Long) = request("SET", token, at)
    fun remove(token: String) = request("REMOVE", token)
    fun close() {
        if (closed) return
        closed = true
        runCatching { send("STOP", "", 0) }
        runCatching { output.close() }
        // 辅助进程收到 STOP 后移除监听器闹钟，守护进程无需阻塞等待子进程退出。
    }
}
