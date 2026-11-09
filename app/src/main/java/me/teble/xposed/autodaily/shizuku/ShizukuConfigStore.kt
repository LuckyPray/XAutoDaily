package me.teble.xposed.autodaily.shizuku

import android.util.AtomicFile
import kotlinx.collections.immutable.persistentMapOf
import me.teble.xposed.autodaily.application.xaApp
import me.teble.xposed.autodaily.config.PACKAGE_NAME_QQ
import me.teble.xposed.autodaily.config.PACKAGE_NAME_TIM
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.utils.toJsonString
import java.io.File

/**
 * 保存完整的用户守护配置，不依赖 Shizuku 的临时连接状态。
 */
object ShizukuConfigStore {
    @Synchronized
    fun saveCurrent() {
        val conf = ShizukuConf(
            enableKeepAlive = ConfUnit.keepAlive && !DaemonConnectionManager.paused,
            alivePackages = persistentMapOf(
                PACKAGE_NAME_QQ to ConfUnit.qKeepAlive,
                PACKAGE_NAME_TIM to ConfUnit.timKeepAlive,
            ),
        )
        val externalDir = checkNotNull(xaApp.getExternalFilesDir(null)) {
            "外部配置目录不可用"
        }
        val dir = File(externalDir, "conf")
        check(dir.isDirectory || dir.mkdirs()) { "无法创建配置目录: $dir" }
        val file = AtomicFile(File(dir, "conf.json"))
        val stream = file.startWrite()
        try {
            stream.write(conf.toJsonString().toByteArray(Charsets.UTF_8))
            file.finishWrite(stream)
        } catch (e: Throwable) {
            file.failWrite(stream)
            throw e
        }
    }
}
