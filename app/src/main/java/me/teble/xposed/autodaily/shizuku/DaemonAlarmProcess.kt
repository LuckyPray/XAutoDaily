package me.teble.xposed.autodaily.shizuku

import android.app.AlarmManager
import android.os.Binder
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import android.os.Process
import android.os.SystemClock
import android.system.Os
import android.util.Log
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.EOFException
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

/**
 * UserService 的私有闹钟辅助进程，仅通过管道接收控制指令。
 *
 * ADB 和 root 模式均由辅助进程的主线程以 shell 身份操作闹钟，
 * 身份切换不影响 UserService 及其 Binder 线程的原有权限。
 */
object DaemonAlarmProcess {
    private val output = DataOutputStream(FileOutputStream(FileDescriptor.out))
    private val acknowledgments = ConcurrentHashMap<String, CountDownLatch>()
    private fun event(kind: String, token: String = "", detail: String = "") = synchronized(output) {
        output.writeUTF(kind); output.writeUTF(token); output.writeUTF(detail); output.flush()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        var graceful = false
        val userId = args.single().toInt()
        try {
            if (Process.myUid() == 0) { Os.setgid(2000); Os.setuid(2000) }
            check(Process.myUid() == 2000)
            val alarmBinder = Class.forName("android.os.ServiceManager").getMethod("getService", String::class.java)
                .invoke(null, "alarm") as IBinder
            val api = Class.forName("android.app.IAlarmManager")
            val alarm = Class.forName("android.app.IAlarmManager\$Stub").getMethod("asInterface", IBinder::class.java)
                .invoke(null, alarmBinder)
            val listenerClass = Class.forName("android.app.IAlarmListener")
            val set = api.methods.first { it.name == "set" && it.parameterTypes.size == 11 }
            val remove = api.methods.first { it.name == "remove" && it.parameterTypes.size == 2 }
            val listeners = mutableMapOf<String, Any>()
            val input = DataInputStream(FileInputStream(FileDescriptor.`in`))
            event("READY", detail = Process.myUid().toString())
            try {
                while (true) {
                    val command = input.readUTF()
                    val token = input.readUTF()
                    val at = input.readLong()
                    if (command == "STOP") { graceful = true; break }
                    if (command == "ACK") {
                        listeners.remove(token)
                        acknowledgments.remove(token)?.countDown()
                        continue
                    }
                    try {
                        listeners.remove(token)?.let { remove.invoke(alarm, null, it) }
                        if (command == "SET") {
                            val binder = Listener(token)
                            val listener = Proxy.newProxyInstance(listenerClass.classLoader, arrayOf(listenerClass)) { proxy, method, params ->
                                when (method.name) {
                                    "asBinder" -> binder
                                    "hashCode" -> System.identityHashCode(proxy)
                                    "equals" -> proxy === params?.firstOrNull()
                                    "toString" -> "XAutoDailyAlarm:$token"
                                    else -> null
                                }
                            }
                            // 使用核心 UID 且不指定 WorkSource，由 AMS 选择不受休眠限制的投递方式。
                            // 监听器闹钟无需应用 PendingIntent，也不依赖模块的精确闹钟权限。
                            set.invoke(alarm, "com.android.shell", AlarmManager.RTC_WAKEUP, at,
                                0L, 0L, 8, null, listener, "XAutoDaily:$token", null, null)
                            listeners[token] = listener
                        } else require(command == "REMOVE")
                        event("OK", token)
                    } catch (e: Exception) { event("ERROR", token, (e.cause ?: e).toString().take(2_000)) }
                }
            } catch (_: EOFException) {
                // 父进程已退出；监听器闹钟依赖当前进程存活，清理后在下方尝试恢复守护。
            } finally {
                listeners.values.forEach { runCatching { remove.invoke(alarm, null, it) } }
            }
        } catch (e: Throwable) { Log.e("XALog", "特权闹钟辅助进程退出", e) }
        if (!graceful) {
            // 特权 getType 调用可在宿主启动模块被拦截时尝试拉起模块。
            // 仅进行一次有超时限制的恢复尝试；用户主动发送 STOP 时不重启守护。
            runCatching { ProviderWakeController(userId).recoverModule() }
                .onFailure { Log.w("XALog", "恢复守护入口失败", it) }
        }
        exitProcess(0)
    }

    private class Listener(private val token: String) : Binder(), IInterface {
        init { attachInterface(this, "android.app.IAlarmListener") }
        override fun asBinder(): IBinder = this
        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            if (code != FIRST_CALL_TRANSACTION) return super.onTransact(code, data, reply, flags)
            data.enforceInterface("android.app.IAlarmListener")
            val completion = data.readStrongBinder()
            val ack = CountDownLatch(1)
            acknowledgments[token] = ack
            try {
                event("FIRED", token, SystemClock.elapsedRealtime().toString())
                // 等待父进程取得有时限的 CPU 唤醒锁后，再释放 AMS 的投递唤醒锁。
                ack.await(5, TimeUnit.SECONDS)
            } finally {
                acknowledgments.remove(token)
                runCatching {
                    val completed = Class.forName("android.app.IAlarmCompleteListener\$Stub")
                        .getMethod("asInterface", IBinder::class.java).invoke(null, completion)
                    Class.forName("android.app.IAlarmCompleteListener").getMethod("alarmComplete", IBinder::class.java)
                        .invoke(completed, this)
                }.onFailure { Log.w("XALog", "闹钟完成通知失败", it) }
            }
            return true
        }
    }
}
