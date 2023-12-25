package me.teble.xposed.autodaily.utils

import android.annotation.SuppressLint

@SuppressLint("PrivateApi")
fun isMiuiDevices(): Boolean {
    val miui = Class.forName("android.os.SystemProperties")
        .getDeclaredMethod("get", String::class.java, String::class.java)
        .invoke(null, "ro.miui.ui.version.code", "") as String
    return miui.isNotEmpty()
}