package me.teble.xposed.autodaily.shizuku

import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.teble.xposed.autodaily.IUserService
import me.teble.xposed.autodaily.application.xaApp
import rikka.shizuku.Shizuku

object ShizukuApi {
    private val _binderAvailable = MutableStateFlow(false)
    var binderAvailable = _binderAvailable.asStateFlow()

    private val _serviceBinderAvailable = MutableStateFlow(false)
    private var serviceBinderAvailable = _serviceBinderAvailable.asStateFlow()

    private val _isPermissionGranted = MutableStateFlow(false)
    var isPermissionGranted = _isPermissionGranted.asStateFlow()
    private lateinit var service: IUserService

    fun initBinder(binder: IBinder) {
        if (serviceBinderAvailable.value && ShizukuApi::service.isInitialized) return
        service = IUserService.Stub.asInterface(binder)
        _serviceBinderAvailable.value = true
        binder.linkToDeath({
            _serviceBinderAvailable.value = false
            Log.w("XALog", "UserService binderDied")
        }, 0)
    }

    fun init() {
        Shizuku.addBinderReceivedListenerSticky {
            _binderAvailable.value = true
            _isPermissionGranted.value =
                Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        }
        Shizuku.addBinderDeadListener {
            _binderAvailable.value = false
            _isPermissionGranted.value = false
        }
    }


    fun checkSelfPermission() {
        _isPermissionGranted.value =
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
    }

    fun startService(packageName: String, className: String, args: Array<String>) {
        if (!isPermissionGranted.value) return
        val arg = args.joinToString(" ")
        val command =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) "start-foreground-service"
            else "startservice"
        shizukuShell("am $command -n $packageName/$className $arg")
    }

    fun setUntrustedTouchEvents(disabled: Boolean) {
        if (!isPermissionGranted.value) return
        shizukuShell("settings put global block_untrusted_touches ${if (disabled) 0 else 2}")
    }

    private fun shizukuShell(shellStr: String): String {
        if (serviceBinderAvailable.value && ShizukuApi::service.isInitialized) {
            return service.execShell(shellStr)
        }
        Toast.makeText(xaApp, "Shizuku is not available", Toast.LENGTH_SHORT).show()
        return ""
    }
}