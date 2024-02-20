package me.teble.xposed.autodaily.activity.common

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.IUserService
import me.teble.xposed.autodaily.application.xaApp
import me.teble.xposed.autodaily.config.JUMP_ACTIVITY
import me.teble.xposed.autodaily.data.KeepAlive
import me.teble.xposed.autodaily.data.QKeepAlive
import me.teble.xposed.autodaily.data.TimKeepAlive
import me.teble.xposed.autodaily.data.UntrustedTouchEvents
import me.teble.xposed.autodaily.data.dataStore
import me.teble.xposed.autodaily.hook.JumpActivityHook
import me.teble.xposed.autodaily.hook.enums.QQTypeEnum
import me.teble.xposed.autodaily.shizuku.ShizukuApi
import me.teble.xposed.autodaily.shizuku.UserService
import rikka.shizuku.Shizuku

class MainViewModel(private val dataStore: DataStore<Preferences> = xaApp.dataStore) :
    ViewModel() {
    private val _shizukuDaemonRunning = MutableStateFlow(false)
    val shizukuDaemonRunning = _shizukuDaemonRunning.asStateFlow()
    private val _shizukuErrInfo = MutableStateFlow("")
    val shizukuErrInfo = _shizukuErrInfo.asStateFlow()

    val keepAlive = dataStore.data.map {
        it[KeepAlive] ?: false
    }

    val qqKeepAlive = dataStore.data.map {
        it[QKeepAlive] ?: false
    }

    val timKeepAlive = dataStore.data.map {
        it[TimKeepAlive] ?: false
    }

    val untrustedTouchEvents = dataStore.data.map {
        it[UntrustedTouchEvents] ?: false
    }
    private val _snackbarText = MutableSharedFlow<String>()
    val snackbarText = _snackbarText.asSharedFlow()

    private val peekServiceJob = viewModelScope.launch(IO) {
//        while (!_shizukuDaemonRunning.value) {
//            _shizukuDaemonRunning.value = peekUserService()
//            delay(1000)
//        }
//        bindUserService()
    }

    fun updateKeepAliveChecked(bool: Boolean) {
        viewModelScope.launch(IO) {
            dataStore.edit {
                it[KeepAlive] = bool
            }
        }

    }

    fun updateQQKeepAlive(bool: Boolean) {
        viewModelScope.launch(IO) {
            dataStore.edit {
                it[QKeepAlive] = bool
            }
        }
    }


    fun updateTimKeepAlive(bool: Boolean) {
        viewModelScope.launch(IO) {
            dataStore.edit {
                it[TimKeepAlive] = bool
            }
        }
    }

    fun updateUntrustedTouchEvents(bool: Boolean) {
        viewModelScope.launch(IO) {
            dataStore.edit {
                it[UntrustedTouchEvents] = bool
            }
        }
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
                        _shizukuErrInfo.value = ""
                        _shizukuDaemonRunning.value = true
                    }
                } catch (e: RemoteException) {
                    Log.e("XALog", e.stackTraceToString())
                    _shizukuErrInfo.value = "守护进程连接失败"
                }
            } else {
                _shizukuErrInfo.value = "invalid binder for $name received"
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            _shizukuErrInfo.value = ""
            _shizukuDaemonRunning.value = false
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
            _shizukuDaemonRunning.value = false
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
            sendToastText("启动失败，请确定 ${type.appName} 已安装并未被停用（冻结）")
        }
    }

    private fun sendToastText(text: String) {
        viewModelScope.launch {
            _snackbarText.emit(text)
        }
    }

    fun shizukuClickable() {
        if (ShizukuApi.binderAvailable && !ShizukuApi.isPermissionGranted) {
            Shizuku.requestPermission(1101)

            viewModelScope.launch(IO) {
                while (!ShizukuApi.isPermissionGranted) {
                    ShizukuApi.isPermissionGranted =
                        Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
                    delay(500)
                }
            }
            Log.d("TAG", "shizukuClickable: 被调用")
            return
        }
        viewModelScope.launch(IO) {
            if (!keepAlive.first()) {
                sendToastText("未启用保活，无需启动守护进程")
                return@launch
            }
            if (!shizukuDaemonRunning.value) {
                bindUserService()
                peekServiceJob.start()
                sendToastText("正在启动守护进程，请稍后")
            } else {
                unbindUserService()
            }
        }
    }
}