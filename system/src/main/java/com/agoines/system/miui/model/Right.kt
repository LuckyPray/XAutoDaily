package com.agoines.system.miui.model

import kotlinx.serialization.Serializable

@Serializable
data class Right(
    var iconParams: IconParams? = null,
    var textParams: TextParams? = null
)
