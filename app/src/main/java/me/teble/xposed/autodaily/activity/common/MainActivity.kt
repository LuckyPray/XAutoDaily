package me.teble.xposed.autodaily.activity.common

import android.os.Bundle
import androidx.activity.ComponentActivity
import kotlin.math.expm1
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun isEnabled(): Boolean {
        sqrt(1.0)
        Math.random()
        expm1(0.001)
        return false
    }
}