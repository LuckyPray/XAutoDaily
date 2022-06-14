package me.teble.xposed.autodaily.ksonpath

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer

inline fun <reified T : Any> JsonElement.read(jsonpath: String): T? {
    return JsonPath(jsonpath).readFromJson(this)
}

inline fun <reified T : Any> JsonElement.read(jsonpath: JsonPath): T? {
    return jsonpath.readFromJson(this)
}

internal fun JsonElement?.isNotNullOrMissing(): Boolean {
    return this != null && this !is JsonNull
}

internal fun JsonArray.children(): List<JsonElement> {
    return map { it }
}

internal fun JsonObject.getValueIfNotNullOrMissing(key: String): JsonElement? {
    val value = get(key)
    return if (value.isNotNullOrMissing()) {
        value
    } else null
}

internal fun JsonArray.getValueIfNotNullOrMissing(index: Int): JsonElement? {
    val value = get(index)
    return if (value.isNotNullOrMissing()) {
        value
    } else null
}

@OptIn(ExperimentalSerializationApi::class)
internal fun Any?.toJsonElement(): JsonElement = when (this) {
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

internal fun Any?.toJsonString(): String = Json.encodeToString(this.toJsonElement())

@OptIn(ExperimentalSerializationApi::class)
private val json = Json {
    explicitNulls = false
    ignoreUnknownKeys = true
}

internal inline fun <reified T> String.parse(): T {
    return json.decodeFromString(this)
}