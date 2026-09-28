package me.teble.xposed.autodaily.utils

import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme

fun Int.toTheme(): XAutodailyTheme.Theme {
    return when (this) {
        0 -> XAutodailyTheme.Theme.Light
        1 -> XAutodailyTheme.Theme.Dark
        2 -> XAutodailyTheme.Theme.System
        else -> XAutodailyTheme.Theme.System
    }
}

fun XAutodailyTheme.Theme.toCode(): Int {
    return when (this) {
        XAutodailyTheme.Theme.Light -> 0
        XAutodailyTheme.Theme.Dark -> 1
        XAutodailyTheme.Theme.System -> 2
    }
}
