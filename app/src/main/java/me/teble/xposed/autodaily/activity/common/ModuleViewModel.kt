package me.teble.xposed.autodaily.activity.common

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.config.JUMP_ACTIVITY
import me.teble.xposed.autodaily.hook.JumpActivityHook
import me.teble.xposed.autodaily.hook.enums.QQTypeEnum
import me.teble.xposed.autodaily.shizuku.ShizukuApi
import me.teble.xposed.autodaily.shizuku.DaemonConnectionManager
import me.teble.xposed.autodaily.ui.ConfUnit
import rikka.shizuku.Shizuku

@Stable
class ModuleViewModel : ViewModel() {
    private val shizukuDaemonRunning get() = DaemonConnectionManager.running
    private val shizukuDaemonStarting get() = DaemonConnectionManager.starting
    private val shizukuErrInfo get() = DaemonConnectionManager.error

    val shizukuState by derivedStateOf {
        if (ShizukuApi.binderAvailable) {
            if (!ShizukuApi.isPermissionGranted) {
                ShizukuState.Warn(Shizuku.getVersion(), "点击此卡片进行授权")
            } else if (!shizukuDaemonRunning) {
                ShizukuState.Warn(
                    Shizuku.getVersion(),
                    when {
                        shizukuDaemonStarting -> "守护进程正在初始化"
                        shizukuErrInfo.isNotEmpty() -> "守护进程启动失败: $shizukuErrInfo"
                        else -> "守护进程未在运行，点击运行"
                    }
                )
            } else {
                ShizukuState.Activated(Shizuku.getVersion())
            }

        } else {
            ShizukuState.Error
        }
    }


    val snackbarHostState = SnackbarHostState()

    fun rebindUserService() {
        viewModelScope.launch(IO) {
            runCatching {
                DaemonConnectionManager.stop()
                DaemonConnectionManager.start()
            }.onFailure { showSnackbar("重启守护进程失败: ${it.message}") }
        }
    }

    /**
     * 打开对应应用的设置
     * @param context 上下文
     * @param type 对应的 QQ 类型
     */
    fun openHostSetting(context: Context, type: QQTypeEnum) {
        val intent = Intent().apply {
            component = ComponentName(type.packageName, JUMP_ACTIVITY)
            action = Intent.ACTION_VIEW
            putExtra(JumpActivityHook.JUMP_ACTION_CMD, "jump")
        }
        runCatching {
            Log.d("XALog", "启动 ${type.appName} 设置")
            context.startActivity(intent)
        }.onFailure {
            Log.e("XALog", "启动失败 ${type.appName} ", it)
            showSnackbar("启动失败，请确定 ${type.appName} 已安装并未被停用（冻结）")
        }
    }

    /**
     * 展示对应 Snackbar
     * @param text 文本内容
     */
    private fun showSnackbar(text: String) {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(text)
        }
    }

    fun shizukuClickable() {
        if (shizukuDaemonStarting) return
        if (ShizukuApi.binderAvailable && !ShizukuApi.isPermissionGranted) {
            Shizuku.requestPermission(1101)
            return
        }
        viewModelScope.launch(IO) {
            runCatching {
                if (shizukuDaemonRunning) DaemonConnectionManager.stop()
                else {
                    check(ConfUnit.keepAlive) { "请先在更多设置中启用任务守护" }
                    DaemonConnectionManager.start()
                }
            }.onFailure { showSnackbar(it.message ?: "守护进程操作失败") }
        }
    }
}
