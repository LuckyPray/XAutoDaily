package me.teble.xposed.autodaily.ksonpath

import kotlinx.serialization.json.*

internal data class ArrayAccessorToken(val index: Int) : Token {
    override fun read(json: JsonElement): JsonElement? {
//        println("ArrayAccessorToken: json -> $json")
        return read(json, index)
    }

    companion object {
        fun read(json: JsonElement, index: Int): JsonElement? {
            return when (json) {
                is JsonArray -> {
                    // get the value at index directly
                    readValueAtIndex(json, index)
                }
                is JsonPrimitive -> {
                    if (json.isString) {
                        val str = json.content
                        if (index < 0) {
                            val indexFromLast = str.length + index
                            if (indexFromLast >= 0 && indexFromLast < str.length) {
                                return JsonPrimitive(str[indexFromLast].toString())
                            } else null
                        } else if (index < str.length) {
                            JsonPrimitive(str[index].toString())
                        } else null
                    } else null
                }
                else -> null
            }
        }

        private fun readValueAtIndex(arrayNode: JsonArray, index: Int): JsonElement? {
            if (index < 0) {
                val indexFromLast = arrayNode.size + index
                if (indexFromLast >= 0) {
                    return arrayNode.getValueIfNotNullOrMissing(indexFromLast)
                }
            }
            return arrayNode.getValueIfNotNullOrMissing(index)
        }
    }
}

internal data class MultiArrayAccessorToken(val indices: List<Int>) : Token {
    override fun read(json: JsonElement): JsonElement? {
//        println("MultiArrayAccessorToken: json -> $json")
        return buildJsonArray {
            indices.forEach {
                ArrayAccessorToken.read(json, it)?.let {
                    if (it.isNotNullOrMissing()) {
                        add(it)
                    }
                }
            }
        }
    }
}

internal data class ArrayLengthBasedRangeAccessorToken(
    val startIndex: Int,
    val endIndex: Int? = null,
    val offsetFromEnd: Int = 0
) : Token {
    override fun read(json: JsonElement): JsonElement? {
//        println("ArrayLengthBasedRangeAccessorToken: json -> $json")
        val token = when (json) {
            is JsonArray -> toMultiArrayAccessorToken(json)
            else -> null
        }
        return token?.read(json) ?: JsonArray(emptyList())
    }

    fun toMultiArrayAccessorToken(json: JsonArray): MultiArrayAccessorToken? {
        val size = json.size
        val start = if (startIndex < 0) {
            val start = size + startIndex
            if (start < 0) 0 else start
        } else startIndex

        val endInclusive = if (endIndex != null) {
            endIndex - 1
        } else size + offsetFromEnd - 1

        return if (start in 0..endInclusive) {
            MultiArrayAccessorToken(IntRange(start, endInclusive).toList())
        } else null
    }
}

internal data class ObjectAccessorToken(val key: String) : Token {
    override fun read(json: JsonElement): JsonElement? {
//        println("ObjectAccessorToken: json -> $json")
        return read(json, key)
    }

    companion object {
        fun read(json: JsonElement, key: String): JsonElement? {
            return when (json) {
                is JsonObject -> json.getValueIfNotNullOrMissing(key)
                is JsonArray -> {
                    buildJsonArray {
                        json.forEach {
                            if (it is JsonObject) {
                                it.getValueIfNotNullOrMissing(key)?.let { add(it) }
                            }
                        }
                    }
                }
                else -> null
            }
        }
    }
}

internal data class MultiObjectAccessorToken(val keys: List<String>) : Token {
    override fun read(json: JsonElement): JsonElement? {
//        println("MultiObjectAccessorToken: json -> $json")
        if (json !is JsonObject) {
            throw IllegalArgumentException("JsonElement cannot be cast JsonObject")
        }
        return buildJsonArray {
            keys.forEach {
                json.getValueIfNotNullOrMissing(it)?.let { add(it) }
            }
        }
    }
}

internal data class DeepScanObjectAccessorToken(val targetKeys: List<String>) : Token {
    override fun read(json: JsonElement): JsonElement? {
//        println("DeepScanObjectAccessorToken: json -> $json")
        return buildJsonArray {
            scan(json, this)
        }
    }

    private fun scan(node: JsonElement, result: JsonArrayBuilder) {
        when (node) {
            is JsonObject -> {
                targetKeys.forEach { key ->
                    ObjectAccessorToken.read(node, key)?.let {
                        if (it.isNotNullOrMissing()) {
                            result.add(it)
                        }
                    }
                }
                node.forEach { _, element ->
                    scan(element, result)
                }
            }
            is JsonArray -> {
                node.forEach {
                    if (it.isNotNullOrMissing()) {
                        scan(it, result)
                    }
                }
            }
            else -> {}
        }
    }
}

internal data class DeepScanArrayAccessorToken(val indices: List<Int>) : Token {
    override fun read(json: JsonElement): JsonElement? {
//        println("DeepScanArrayAccessorToken: json -> $json")
        return buildJsonArray {
            scan(json, this)
        }
    }

    private fun scan(node: JsonElement, result: JsonArrayBuilder) {
        when (node) {
            is JsonObject -> {
                // traverse all key/value pairs and recursively scan underlying objects/arrays
                node.forEach { _, element ->
                    if (element.isNotNullOrMissing()) {
                        scan(element, result)
                    }
                }
            }
            is JsonArray -> {
                // first add all requested indices to our results
                indices.forEach { index ->
                    ArrayAccessorToken(index).read(node)?.let {
                        if (it.isNotNullOrMissing()) {
                            result.add(it)
                        }
                    }
                }

                // now recursively scan underlying objects/arrays
                node.forEach {
                    if (it.isNotNullOrMissing()) {
                        scan(it, result)
                    }
                }
            }
            else -> {}
        }
    }
}

internal data class DeepScanLengthBasedArrayAccessorToken(
    val startIndex: Int,
    val endIndex: Int? = null,
    val offsetFromEnd: Int = 0
) : Token {
    override fun read(json: JsonElement): JsonElement? {
//        println("DeepScanLengthBasedArrayAccessorToken: json -> $json")
        return buildJsonArray {
            scan(json, this)
        }
    }

    private fun scan(node: JsonElement, result: JsonArrayBuilder) {
        when (node) {
            is JsonObject -> {
                node.forEach { _, element ->
                    if (element.isNotNullOrMissing()) {
                        scan(element, result)
                    }
                }
            }
            is JsonArray -> {
                ArrayLengthBasedRangeAccessorToken(startIndex, endIndex, offsetFromEnd)
                    .read(node)?.let { resultNode ->
                        val resultArray = resultNode as? JsonArray
                        resultArray?.forEach { result.add(it) }
                    }

                node.forEach {
                    if (it.isNotNullOrMissing()) {
                        scan(it, result)
                    }
                }
            }
            else -> {}
        }
    }
}

internal class WildcardToken : Token {
    override fun read(json: JsonElement): JsonElement? {
//        println("WildcardToken: json -> $json")
        return buildJsonArray {
            when (json) {
                is JsonObject -> {
                    json.forEach { _, element ->
                        if (element.isNotNullOrMissing()) {
                            add(element)
                        }
                    }
                }
                is JsonArray -> {
                    json.forEach { node ->
                        add(node)
                    }
                }
                else -> add(json)
            }
        }
    }
}

internal class DeepScanWildcardToken : Token {
    override fun read(json: JsonElement): JsonElement? {
//        println("DeepScanWildcardToken: json -> $json")
        return buildJsonArray {
            scan(json, this)
        }
    }

    private fun scan(node: JsonElement, result: JsonArrayBuilder) {
        WildcardToken().read(node)?.let {
            if (it is JsonArray) {
                it.forEach {
                    if (it.isNotNullOrMissing()) {
                        result.add(it)
                    }
                }
            }
        }
        when (node) {
            is JsonObject -> {
                node.forEach { _, element ->
                    if (element.isNotNullOrMissing()) {
                        scan(element, result)
                    }
                }
            }
            is JsonArray -> {
                // now recursively scan underlying objects/arrays
                node.forEach {
                    if (it.isNotNullOrMissing()) {
                        scan(it, result)
                    }
                }
            }
            else -> {}
        }
    }
}

interface Token {
    fun read(json: JsonElement): JsonElement?
}