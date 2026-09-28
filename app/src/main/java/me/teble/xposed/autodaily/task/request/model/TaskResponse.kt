@file:SuppressLint("UnsafeOptInUsageError")
package me.teble.xposed.autodaily.task.request.model

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@Serializable
data class TaskResponse(
    val headersText: String?,
    val body: String,
    val code: Int,
)