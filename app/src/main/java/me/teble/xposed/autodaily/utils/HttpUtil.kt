package me.teble.xposed.autodaily.utils

import okhttp3.OkHttpClient
import okhttp3.Request

fun String.get(): String {
    val req = Request.Builder().url(this).build()
    return runCatching { OkHttpClient().newCall(req).execute().body?.string() }
        .onFailure { LogUtil.e(it) }
        .getOrNull() ?: ""
}