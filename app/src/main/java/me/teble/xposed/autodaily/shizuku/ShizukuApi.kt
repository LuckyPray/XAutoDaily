package me.teble.xposed.autodaily.shizuku

import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.teble.xposed.autodaily.IUserService
import me.teble.xposed.autodaily.application.xaApp
import rikka.shizuku.Shizuku

object ShizukuApi {
    var isBinderAvailable = false
    var isPermissionGranted by mutableStateOf(false)
    private lateinit var service: IUserService

    fun initBinder(binder: IBinder) {
        if (isBinderAvailable && ShizukuApi::service.isInitialized) return
        service = IUserService.Stub.asInterface(binder)
        isBinderAvailable = true
        binder.linkToDeath({
            isBinderAvailable = false
            Log.w("XALog", "UserService binderDied")
        }, 0)
    }

    fun init() {
        Shizuku.addBinderReceivedListenerSticky {
            isBinderAvailable = true
            isPermissionGranted = Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        }
        Shizuku.addBinderDeadListener {
            isBinderAvailable = false
            isPermissionGranted = false
        }
    }

    fun startService(packageName: String, className: String, args: Array<String>) {
        if (!isPermissionGranted) return
        val arg = args.joinToString(" ")
        val command =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) "start-foreground-service"
            else "startservice"
        shizukuShell("am $command -n $packageName/$className $arg")
    }

    fun setUntrustedTouchEvents(disabled: Boolean) {
        if (!isPermissionGranted) return
        shizukuShell("settings put global block_untrusted_touches ${if (disabled) 0 else 2}")
    }

    private fun shizukuShell(shellStr: String): String {
        if (isBinderAvailable && ShizukuApi::service.isInitialized) {
            return service.execShell(shellStr)
        }
        Toast.makeText(xaApp, "Shizuku is not available", Toast.LENGTH_SHORT).show()
        return ""
    }
}