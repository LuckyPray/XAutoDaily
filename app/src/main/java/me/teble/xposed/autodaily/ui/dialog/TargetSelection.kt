package me.teble.xposed.autodaily.ui.dialog

internal data class SelectionTarget(
    val id: String,
    val title: String,
    val keywords: String = title,
    val description: String = id
)

internal fun parseSelectedIds(value: String): List<String> =
    value.split(",").map { it.trim() }.filter { it.isNotEmpty() }.distinct()

internal fun includeSavedSelectionTargets(
    targets: List<SelectionTarget>,
    savedIds: List<String>
): List<SelectionTarget> {
    val knownIds = targets.map { it.id }.toSet()
    return targets + savedIds.distinct().filterNot { it in knownIds }.map {
        SelectionTarget(it, it, description = "未在当前列表中找到")
    }
}

internal fun filterSelectionTargets(
    targets: List<SelectionTarget>,
    query: String
): List<SelectionTarget> {
    val keyword = query.trim()
    return targets.filter {
        it.id.contains(keyword, ignoreCase = true) ||
            it.title.contains(keyword, ignoreCase = true) ||
            it.keywords.contains(keyword, ignoreCase = true)
    }
}

/** Bulk actions affect the visible results, preserving IDs outside the current search. */
internal fun updateTargetSelection(
    selected: List<String>,
    visibleIds: List<String>,
    invert: Boolean
): List<String> = selected.toMutableSet().apply {
    visibleIds.distinct().forEach { id ->
        if (invert && id in this) remove(id) else add(id)
    }
}.toList()

internal const val TASK_ENV_SELECTION_RESULT = "taskEnvSelection"
