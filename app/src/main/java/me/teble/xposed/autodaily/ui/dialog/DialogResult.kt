package me.teble.xposed.autodaily.ui.dialog

sealed interface DialogResult {
    data object Confirm : DialogResult

    data object Cancel : DialogResult

    data object Dismiss : DialogResult
}