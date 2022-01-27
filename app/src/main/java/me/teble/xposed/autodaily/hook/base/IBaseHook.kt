package me.teble.xposed.autodaily.hook.base

interface IBaseHook {
    fun init() {}
    val isCompatible: Boolean
        get() = TODO("HOOK ${this::class.java.canonicalName} 未设置触发条件")
    val enabled: Boolean
        get() = false
}