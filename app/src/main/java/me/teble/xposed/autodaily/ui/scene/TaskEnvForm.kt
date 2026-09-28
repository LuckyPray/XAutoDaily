package me.teble.xposed.autodaily.ui.scene

import me.teble.xposed.autodaily.task.model.TaskEnv
import me.teble.xposed.autodaily.ui.dialog.parseSelectedIds
import java.math.BigInteger

internal fun normalizeTaskEnvValue(env: TaskEnv, value: String): String =
    when (env.type) {
        "friend", "group", "guild" -> parseSelectedIds(value).joinToString(",")
        else -> value
    }

internal fun taskEnvValueError(env: TaskEnv, value: String): String? {
    if (!env.isEditable) return null
    val normalized = normalizeTaskEnvValue(env, value)
    if (normalized.isEmpty()) {
        return if (env.default.isEmpty()) "必填项不能为空" else null
    }
    if (env.type == "num") {
        if (!normalized.all { it in '0'..'9' }) return "请输入非负整数"
        val number = normalized.toBigInteger()
        if (env.limit > 0 && number > BigInteger.valueOf(env.limit.toLong())) {
            return "数值不能大于 ${env.limit}"
        }
    }
    if (env.limit <= 0) return null
    return when (env.type) {
        "string" -> if (normalized.length > env.limit) "最多输入 ${env.limit} 个字符" else null
        "randString" -> if (normalized.split("|").any { it.length > env.limit }) {
            "每段文字最多输入 ${env.limit} 个字符"
        } else null
        "list", "friend", "group", "guild" -> if (normalized.split(",").size > env.limit) {
            "最多填写 ${env.limit} 项"
        } else null
        else -> null
    }
}

/** Validate the entire form before writing any field; an empty override restores its default. */
internal fun saveTaskEnvValues(
    envs: List<TaskEnv>,
    values: Map<String, String>,
    writeValue: (String, String?) -> Unit
): Map<String, String> {
    val editable = envs.filter { it.isEditable }
    val errors = editable.mapNotNull { env ->
        taskEnvValueError(env, values[env.name].orEmpty())?.let { env.name to it }
    }.toMap()
    if (errors.isNotEmpty()) return errors
    editable.forEach { env ->
        writeValue(env.name, normalizeTaskEnvValue(env, values[env.name].orEmpty()).ifEmpty { null })
    }
    return emptyMap()
}

internal fun taskTime(
    envs: List<TaskEnv>,
    values: Map<String, String>,
    defaults: Map<String, String>
): Pair<Int, Int>? {
    if (!listOf("hour", "minute").all { name -> envs.any { it.name == name && it.isEditable } }) {
        return null
    }
    val hour = values["hour"].orEmpty().ifEmpty { defaults["hour"].orEmpty() }.toIntOrNull()
    val minute = values["minute"].orEmpty().ifEmpty { defaults["minute"].orEmpty() }.toIntOrNull()
    return if (hour != null && minute != null && hour in 0..23 && minute in 0..59) {
        hour to minute
    } else null
}

internal fun applyTaskEnvSelection(
    envs: List<TaskEnv>,
    values: Map<String, String>,
    result: List<String>
): Map<String, String> {
    if (result.size != 3) return values
    val (name, type, value) = result
    val env = envs.firstOrNull {
        it.isEditable && it.name == name && it.type == type &&
            type in listOf("friend", "group", "guild")
    } ?: return values
    return values + (name to normalizeTaskEnvValue(env, value))
}
