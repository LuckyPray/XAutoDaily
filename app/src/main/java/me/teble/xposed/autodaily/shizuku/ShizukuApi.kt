package me.teble.xposed.autodaily.shizuku

import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import me.teble.xposed.autodaily.IUserService
import me.teble.xposed.autodaily.application.xaApp
import rikka.shizuku.Shizuku

object ShizukuApi {

    val binderAvailable = mutableStateOf(false)

    private val serviceBinderAvailable = mutableStateOf(false)

    val isPermissionGranted = mutableStateOf(false)
    private lateinit var service: IUserService

    fun initBinder(binder: IBinder) {
        if (serviceBinderAvailable.value && ShizukuApi::service.isInitialized) return
        service = IUserService.Stub.asInterface(binder)
        serviceBinderAvailable.value = true
        binder.linkToDeath({
            serviceBinderAvailable.value = false
            Log.w("XALog", "UserService binderDied")
        }, 0)
    }

    fun init() {
        Shizuku.addBinderReceivedListenerSticky {
            binderAvailable.value = true
            isPermissionGranted.value =
                Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        }
        Shizuku.addBinderDeadListener {
            binderAvailable.value = false
            isPermissionGranted.value = false
        }
    }


    fun checkSelfPermission() {
        isPermissionGranted.value =
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