package me.teble.xposed.autodaily.ksonpath

import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer


class JsonPath(path: String) {

    /**
     * Trim given path string and compile it on initialization
     */
    private val path: String = path.trim()
    val tokens: ImmutableList<Token> = PathCompiler.compile(this.path)

    /**
     * Read the value at path in given JSON string
     *
     * @return Given type if value in path exists, null otherwise
     */
    inline fun <reified T : Any> readFromJson(jsonString: String): T? {
        return parse(jsonString)?.let { readFromJson(it) }
    }

    /**
     * Read the value at path in given jackson JsonNode Object 
     *
     * @return Given type if value in path exists, null otherwise
     */
    inline fun <reified T : Any> readFromJson(json: JsonElement): T? {

        val lastValue = tokens.fold(initial = json) { valueAtPath: JsonElement?, nextToken: Token ->
            valueAtPath?.let { nextToken.read(it) }
        }

        return when (lastValue) {
            null, is JsonNull -> null
            else -> {
                try {
                    Json.decodeFromJsonElement(serializer(T::class.java), lastValue) as T?
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }

    /**
     * Check if a ArrayNode contains only primitive values (in this case, non-ObjectNode/ArrayNode).
     */
//    private fun containsOnlyPrimitives(arrayNode: ArrayNode) : Boolean {
//        arrayNode.forEach {
//            if (it.isObject || it.isArray) {
//                return false // fail fast
//            }
//        }
//        return true
//    }

//    private fun isSpecialChar(c: Char): Boolean {
//        return c == '"' || c == '\\' || c == '/' || c == 'b' || c == 'f' || c == 'n' || c == 'r' || c == 't'
//    }

    companion object {
        /**
         * Parse JSON string and return successful [JsonNode] or null otherwise
         *
         * @param jsonString JSON string to parse
         * @return instance of parsed jackson [JsonNode] object, or null
         */
        @JvmStatic
        fun parse(jsonString: String?): JsonElement? {
            return jsonString?.parse()
        }
    }
}

