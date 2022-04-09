package me.teble.xposed.autodaily.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Color.TRANSPARENT
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.*
import android.view.View
import android.view.Window
import android.view.WindowManager.LayoutParams.*

/**
 * 状态栏透明
 */
@Suppress("DEPRECATION")
fun Window.setStatusBarTranslation() {
    if (SDK_INT >= Q)
        this.isStatusBarContrastEnforced = false
    // 设置状态栏透明,暂时没有更好的办法解决透明问题
    this.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    this.clearFlags(FLAG_TRANSLUCENT_STATUS)
    this.statusBarColor = TRANSPARENT
}

/**
 * 导航栏透明
 */
@Suppress("DEPRECATION")
fun Window.setNavigationBarTranslation() {
    // 设置导航栏透明,暂时没有更好的办法解决透明问题
    this.addFlags(FLAG_TRANSLUCENT_NAVIGATION)
    // 防止透明以后高对比度
    if (SDK_INT >= Q)
        this.isNavigationBarContrastEnforced = false
    // 去除导航栏线段
    if (SDK_INT >= P)
        this.navigationBarDividerColor = TRANSPARENT
    this.navigationBarColor = TRANSPARENT
}

/**
 * 亮色导航栏
 * @param enable 默认为亮色
 */
@Suppress("DEPRECATION")
fun Activity.navigationBarMode(enable: Boolean = true) {
    if (SDK_INT >= O) {
        window.decorView.systemUiVisibility = if (enable)
            window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        else
            window.decorView.systemUiVisibility xor View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    }
}

@Suppress("DEPRECATION")
fun Dialog.navigationBarMode(enable: Boolean = true) {
    window?.let {
        if (SDK_INT >= O) {
            it.decorView.systemUiVisibility = if (enable)
                it.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            else
                it.decorView.systemUiVisibility xor View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }
}