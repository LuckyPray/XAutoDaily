package me.teble.xposed.autodaily.utils

import android.os.Bundle

@Suppress("DEPRECATION")
fun Bundle?.toMap(): Map<String, Any?>{
    if (this == null) return mapOf()
    val res = HashMap<String, Any?>()
    for (key in this.keySet()) {
        res[key] = this.get(key)
    }
    return res
}