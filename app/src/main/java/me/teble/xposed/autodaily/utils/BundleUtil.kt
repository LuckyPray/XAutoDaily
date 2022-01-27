package me.teble.xposed.autodaily.utils

import android.os.Bundle

fun Bundle.getExtras(): Map<String, Any?>{
    val res = HashMap<String, Any?>()
    for (key in this.keySet()) {
        res[key] = this.get(key)
    }
    return res
}