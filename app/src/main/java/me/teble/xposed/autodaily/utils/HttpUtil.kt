package me.teble.xposed.autodaily.utils

import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit


val client = OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .readTimeout(10, TimeUnit.SECONDS)
    .writeTimeout(10, TimeUnit.SECONDS)
    .connectionPool(ConnectionPool(32, 5, TimeUnit.SECONDS))
    .dispatcher(Dispatcher().apply {
        maxRequestsPerHost = 10
    })
    .build()

fun String.get(): String {
    val req = Request.Builder().url(this).build()
    return runCatching { client.newCall(req).execute().body?.string() }
        .onFailure { LogUtil.e(it) }
        .getOrNull() ?: ""
}