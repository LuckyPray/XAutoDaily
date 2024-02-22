package me.teble.xposed.autodaily.activity.common

sealed class ShisukuState {
    data object Error : ShisukuState()
    data class Warn(val version: Int, val info: String) : ShisukuState()
    data class Activated(val version: Int) : ShisukuState()
}

