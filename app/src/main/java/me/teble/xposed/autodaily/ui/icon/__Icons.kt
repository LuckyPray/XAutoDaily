package me.teble.xposed.autodaily.ui.icon

import androidx.compose.ui.graphics.vector.ImageVector
import me.teble.xposed.autodaily.ui.icon.icons.About
import me.teble.xposed.autodaily.ui.icon.icons.Configuration
import me.teble.xposed.autodaily.ui.icon.icons.Script
import me.teble.xposed.autodaily.ui.icon.icons.Setting
import kotlin.collections.List as ____KtList

public object Icons

private var __AllIcons: ____KtList<ImageVector>? = null

public val Icons.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(Configuration, About, Setting, Script)
    return __AllIcons!!
  }
