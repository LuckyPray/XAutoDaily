package me.teble.xposed.autodaily.shizuku

import java.io.File

/**
 * 保存宿主进程身份，结合启动时刻校验进程，避免将复用的 PID 误认为原进程。
 */
internal data class HostProcess(val pid: Int, val uid: Int, val name: String, val startTicks: Long) {
    fun isAlive(): Boolean = read(pid) == this

    companion object {
        fun read(pid: Int): HostProcess? = runCatching {
            require(pid > 0)
            val dir = File("/proc/$pid")
            val name = File(dir, "cmdline").readText().substringBefore('\u0000')
            require(name in WakeProtocol.hosts || WakeProtocol.hosts.any { name == "$it:MSF" })
            val uid = File(dir, "status").useLines { lines ->
                lines.first { it.startsWith("Uid:") }.split(Regex("\\s+"))[1].toInt()
            }
            val stat = File(dir, "stat").readText().substringAfterLast(") ").split(' ')
            HostProcess(pid, uid, name, stat[19].toLong())
        }.getOrNull()

        fun find(packageName: String, uid: Int): Set<HostProcess> = File("/proc").listFiles().orEmpty()
            .asSequence().mapNotNull { it.name.toIntOrNull()?.let(::read) }
            .filter { it.uid == uid && (it.name == packageName || it.name == "$packageName:MSF") }.toSet()
    }
}
