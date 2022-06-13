package me.teble.xposed.autodaily.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer

/**
 * @author teble
 * @date 2021/6/9 18:07
 */
@OptIn(ExperimentalSerializationApi::class)
fun Any?.toJsonElement(): JsonElement = when (this) {
    null -> JsonNull
    is JsonElement -> this
    is Number -> JsonPrimitive(this)
    is Boolean -> JsonPrimitive(this)
    is String -> JsonPrimitive(this)
    is Array<*> -> JsonArray(this.map { it.toJsonElement() })
    is List<*> -> JsonArray(this.map { it.toJsonElement() })
    is Map<*, *> -> JsonObject(this.map { it.key.toString() to it.value.toJsonElement() }.toMap())
    else -> Json.encodeToJsonElement(serializer(this::class.java), this)
}

fun Any?.toJsonString(): String = Json.encodeToString(this.toJsonElement())

@OptIn(ExperimentalSerializationApi::class)
private val json = Json {
    explicitNulls = false
    ignoreUnknownKeys = true
}

internal inline fun <reified T> String.parse(): T {
    return json.decodeFromString(this)
}