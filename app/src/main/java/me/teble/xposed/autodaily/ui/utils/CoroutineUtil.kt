package me.teble.xposed.autodaily.ui.utils

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun CoroutineScope.safeLaunch(block: suspend CoroutineScope.() -> Unit): Job {
    return this.launch {
        try {
            block()
        } catch (ce: CancellationException) {
            // You can ignore or log this exception
        }
    }
}