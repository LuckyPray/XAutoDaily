package me.teble.xposed.autodaily.ui.icon

import androidx.compose.ui.graphics.vector.ImageVector
import me.teble.xposed.autodaily.ui.icon.icons.About
import me.teble.xposed.autodaily.ui.icon.icons.Activated
import me.teble.xposed.autodaily.ui.icon.icons.Back
import me.teble.xposed.autodaily.ui.icon.icons.ChevronDown
import me.teble.xposed.autodaily.ui.icon.icons.ChevronRight
import me.teble.xposed.autodaily.ui.icon.icons.Close
import me.teble.xposed.autodaily.ui.icon.icons.Configuration
import me.teble.xposed.autodaily.ui.icon.icons.Edit
import me.teble.xposed.autodaily.ui.icon.icons.Error
import me.teble.xposed.autodaily.ui.icon.icons.Info
import me.teble.xposed.autodaily.ui.icon.icons.More
import me.teble.xposed.autodaily.ui.icon.icons.Notice
import me.teble.xposed.autodaily.ui.icon.icons.QQ
import me.teble.xposed.autodaily.ui.icon.icons.Save
import me.teble.xposed.autodaily.ui.icon.icons.Script
import me.teble.xposed.autodaily.ui.icon.icons.Search
import me.teble.xposed.autodaily.ui.icon.icons.SelectNormal
import me.teble.xposed.autodaily.ui.icon.icons.SelectSelectd
import me.teble.xposed.autodaily.ui.icon.icons.Setting
import me.teble.xposed.autodaily.ui.icon.icons.TIM
import me.teble.xposed.autodaily.ui.icon.icons.Warn
import me.teble.xposed.autodaily.ui.icon.icons.XAutoDaily
import me.teble.xposed.autodaily.ui.icon.icons.XAutoDailyRound
import kotlin.collections.List as ____KtList

public object Icons

private var __AllIcons: ____KtList<ImageVector>? = null

public val Icons.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
      __AllIcons = listOf(
          Configuration, About, Setting, Notice, ChevronRight, Info, TIM, XAutoDaily,
          QQ, More, Activated, XAutoDailyRound, Error, Warn, Script, Back, SelectNormal,
          SelectSelectd, ChevronDown, Close, Search, Save, Edit
      )
    return __AllIcons!!
  }
