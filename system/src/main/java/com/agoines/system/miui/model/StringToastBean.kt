package com.agoines.system.miui.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val json = Json {
    encodeDefaults = true
}

@Serializable
data class StringToastBean(val left: Left, val right: Right) {
    fun toJson(): String {
        return json.encodeToString(this)
    }
}
