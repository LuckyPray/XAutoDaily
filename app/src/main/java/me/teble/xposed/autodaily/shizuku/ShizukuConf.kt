package me.teble.xposed.autodaily.shizuku

import kotlinx.serialization.Serializable

@Serializable
data class ShizukuConf(
    val enableKeepAlive: Boolean,
    val alivePackages: Map<String, Boolean>
)