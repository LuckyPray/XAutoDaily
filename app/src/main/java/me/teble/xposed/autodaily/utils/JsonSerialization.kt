package me.teble.xposed.autodaily.utils

import cn.hutool.json.JSONUtil

/**
 * @author teble
 * @date 2021/6/9 18:07
 */
fun Any?.toJsonString(): String = JSONUtil.toJsonStr(this)

inline fun <reified T> String.parse(): T {
    return JSONUtil.toBean(this, T::class.java)
}
