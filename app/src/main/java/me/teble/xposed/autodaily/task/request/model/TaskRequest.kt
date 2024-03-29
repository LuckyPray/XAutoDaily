package me.teble.xposed.autodaily.task.request.model

import kotlinx.serialization.Serializable

@Serializable
data class TaskRequest(
    val url: String,
    val method: String?,
    val headers: Map<String, String>?,
    val cookie: String?,
    val data: String?,
)