package me.teble.xposed.autodaily.activity.common

sealed interface ShizukuState {
    data object Error : ShizukuState
    data class Warn(val version: Int, val info: String) : ShizukuState
    data class Activated(val version: Int) : ShizukuState
}

