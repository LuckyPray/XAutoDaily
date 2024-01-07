package com.agoines.system.miui.model

import kotlinx.serialization.Serializable

@Serializable
data class TextParams(
    val text: String? = null,
    val textColor: Int = 0
)
