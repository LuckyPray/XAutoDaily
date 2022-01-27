package me.teble.xposed.autodaily.activity.common

import android.os.Bundle
import me.teble.xposed.autodaily.hook.proxy.activity.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun isEnabled(): Boolean {
        Math.sqrt(1.0)
        Math.random()
        Math.expm1(0.001)
        return false
    }
}