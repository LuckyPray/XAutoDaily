package me.teble.xposed.autodaily.shizuku

import android.content.pm.PackageManager
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.teble.xposed.autodaily.IUserService
import me.teble.xposed.autodaily.application.xaApp
import rikka.shizuku.Shizuku

object ShizukuApi {

    var binderAvailable by mutableStateOf(false)

    private var serviceBinderAvailable by mutableStateOf(false)

    var isPermissionGranted by mutableStateOf(false)
    private lateinit var service: IUserService

    fun initBinder(binder: IBinder) {
        if (serviceBinderAvailable && ShizukuApi::service.isInitialized && service.asBinder() == binder) return
        service = IUserService.Stub.asInterface(binder)
        serviceBinderAvailable = true
        try {
            binder.linkToDeath({
                if (ShizukuApi::service.isInitialized && service.asBinder() == binder) {
                    serviceBinderAvailable = false
                    Log.w("XALog", "UserService binderDied")
                }
            }, 0)
        } catch (e: RemoteException) {
            serviceBinderAvailable = false
            throw e
        }
    }

    fun clearUserServiceBinder() {
        serviceBinderAvailable = false
    }

    fun init() {
        Shizuku.addBinderReceivedListenerSticky {
            binderAvailable = true
            isPermissionGranted =
                Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
            if (isPermissionGranted) DaemonConnectionManager.recover()
        }
        Shizuku.addBinderDeadListener {
            binderAvailable = false
            isPermissionGranted = false
            serviceBinderAvailable = false
            DaemonConnectionManager.disconnected()
        }
        Shizuku.addRequestPermissionResultListener { _, result ->
            isPermissionGranted = result == PackageManager.PERMISSION_GRANTED
            if (isPermissionGranted) DaemonConnectionManager.configChanged()
        }
    }


    fun checkSelfPermission() {
        isPermissionGranted =
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
    }

    fun setUntrustedTouchEvents(disabled: Boolean) {
        if (!isPermissionGranted) return
        shizukuShell("settings put global block_untrusted_touches ${if (disabled) 0 else 2}")
    }

    private fun shizukuShell(shellStr: String): String {
        if (serviceBinderAvailable && ShizukuApi::service.isInitialized) {
            return service.execShell(shellStr)
        }
        Toast.makeText(xaApp, "Shizuku is not available", Toast.LENGTH_SHORT).show()
        return ""
    }
}
