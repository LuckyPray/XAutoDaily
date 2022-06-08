package me.teble.xposed.autodaily.hook.shizuku

import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import rikka.shizuku.Shizuku
import java.io.BufferedReader
import java.io.InputStreamReader

object ShizukuApi {
    var isBinderAvailable = false
    var isPermissionGranted by mutableStateOf(false)

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

    fun startService(packageName: String, className: String) {
        shizukuShell("am startservice -n $packageName/$className")
    }

    fun setUntrustedTouchEvents(disabled: Boolean): String {
        return shizukuShell("settings put global block_untrusted_touches ${if(disabled) 0 else 2}")
    }

    @Suppress("DEPRECATION")
    private fun shizukuShell(shellStr: String): String {
        val inputStream =  Shizuku.newProcess(mutableListOf<String>().apply {
            addAll(shellStr.split(" "))
        }.toTypedArray(), arrayOf(), "/").inputStream
        val inputStreamReader = InputStreamReader(inputStream)
        val bufferReader = BufferedReader(inputStreamReader)
        return bufferReader.readText()
    }
}