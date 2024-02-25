package me.teble.xposed.autodaily.ui.theme

import com.dokar.sheets.DialogSheetBehaviors
import com.dokar.sheets.SheetBehaviors

const val DisabledFontAlpha = 0.68f
const val CardDisabledAlpha = 0.60f
const val DisabledAlpha = 0.38f
const val DefaultAlpha = 1f

val DefaultSheetBehaviors = SheetBehaviors(
    collapseOnBackPress = true,
    collapseOnClickOutside = true,
    extendsIntoStatusBar = true,
    extendsIntoNavigationBar = true
)

val DefaultDialogSheetBehaviors = DialogSheetBehaviors(
    collapseOnBackPress = true,
    collapseOnClickOutside = true,
    extendsIntoStatusBar = true,
    extendsIntoNavigationBar = true,

    lightStatusBar = true,
    lightNavigationBar = true
)
