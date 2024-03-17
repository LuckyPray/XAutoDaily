package me.teble.xposed.autodaily.activity.common

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
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
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.IUserService
import me.teble.xposed.autodaily.config.JUMP_ACTIVITY
import me.teble.xposed.autodaily.hook.JumpActivityHook
import me.teble.xposed.autodaily.hook.enums.QQTypeEnum
import me.teble.xposed.autodaily.shizuku.ShizukuApi
import me.teble.xposed.autodaily.shizuku.UserService
import rikka.shizuku.Shizuku

@Stable
class ModuleViewModel : ViewModel() {
    private var shizukuDaemonRunning by mutableStateOf(false)
    private var shizukuErrInfo by mutableStateOf("")


    val shizukuState by derivedStateOf {

        if (ShizukuApi.binderAvailable) {
            if (!ShizukuApi.isPermissionGranted) {
                ShizukuState.Warn(Shizuku.getVersion(), "点击此卡片进行授权")
            } else if (!shizukuDaemonRunning) {
                ShizukuState.Warn(
                    Shizuku.getVersion(),
                    if (shizukuErrInfo.isEmpty()) "守护进程未在运行，点击运行" else "守护进程启动失败: $shizukuErrInfo"
                )
            } else {
                ShizukuState.Activated(Shizuku.getVersion())
            }

        } else {
            ShizukuState.Error
        }
    }


    val snackbarHostState = SnackbarHostState()

    private val peekServiceJob = viewModelScope.launch(Main) {
            while (!shizukuDaemonRunning) {
                withContext(IO) {
                    shizukuDaemonRunning = peekUserService()
                    delay(1000)
                }

            }
            bindUserService()

    }

    /**
     * 用于与Shizuku服务的连接和断开
     */
    private var userServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            if (binder != null && binder.pingBinder()) {
                val service = IUserService.Stub.asInterface(binder)
                ShizukuApi.initBinder(binder)
                try {
                    if (service.isRunning) {
                        shizukuErrInfo = ""
                        shizukuDaemonRunning = true
                    }
                } catch (e: RemoteException) {
                    Log.e("XALog", e.stackTraceToString())
                    shizukuErrInfo = "守护进程连接失败"
                }
            } else {
                shizukuErrInfo = "invalid binder for $name received"
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            shizukuErrInfo = ""
            shizukuDaemonRunning = false
        }
    }


    private val userServiceArgs = Shizuku.UserServiceArgs(
        ComponentName(
            BuildConfig.APPLICATION_ID,
            UserService::class.java.name
        )
    )
        .daemon(true)
        .processNameSuffix("service")
        .debuggable(BuildConfig.DEBUG)
        .version(BuildConfig.VERSION_CODE)


    fun rebindUserService() {
        unbindUserService() && bindUserService()
    }

    private fun bindUserService(): Boolean {
        try {
            Shizuku.bindUserService(
                userServiceArgs,
                userServiceConnection
            )
            return true
        } catch (e: Throwable) {
            Log.e("XALog", e.stackTraceToString())
        }
        return false
    }

    private fun unbindUserService(): Boolean {
        try {
            Shizuku.unbindUserService(
                userServiceArgs,
                userServiceConnection, true
            )
            shizukuDaemonRunning = false
            return true
        } catch (e: Throwable) {
            Log.e("XALog", e.stackTraceToString())
        }
        return false
    }

    private fun peekUserService(): Boolean {
        try {
            return Shizuku.peekUserService(
                userServiceArgs,
                userServiceConnection
            ) == 0
        } catch (e: Throwable) {
            Log.e("XALog", e.stackTraceToString())
        }
        return false
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
        if (ShizukuApi.binderAvailable && !ShizukuApi.isPermissionGranted) {
            Shizuku.requestPermission(1101)

            viewModelScope.launch(IO) {
                while (!ShizukuApi.isPermissionGranted) {
                    ShizukuApi.checkSelfPermission()
                    delay(500)
                }
            }
            Log.d("TAG", "shizukuClickable: 被调用")
            return
        }
        viewModelScope.launch(IO) {
            if (!AppConfUnit.keepAlive) {
                showSnackbar("未启用保活，无需启动守护进程")
                return@launch
            }
            if (!shizukuDaemonRunning) {
                bindUserService()
                peekServiceJob.start()
                showSnackbar("正在启动守护进程，请稍后")
            } else {
                unbindUserService()
            }
        }
    }
}