package me.teble.xposed.autodaily.hook.utils

import android.os.Bundle
import java.util.*

object BundleUtil {
    fun getBundleMap(bundle: Bundle): Map<String, Any?> {
        val map = TreeMap<String, Any?>()
        bundle.keySet().forEach {
            map[it] = bundle[it]
        }
        return map
    }
}