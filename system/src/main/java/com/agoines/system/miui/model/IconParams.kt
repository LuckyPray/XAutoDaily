package com.agoines.system.miui.model

import kotlinx.serialization.Serializable

@Serializable
data class IconParams(
    var category: String? = null,
    var iconFormat: String? = null,
    var iconResName: String? = null,
    var iconType: Int = 0
)