package me.teble.xposed.autodaily.utils

import android.graphics.Color.TRANSPARENT
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import android.os.Build.VERSION_CODES.R
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
    if (SDK_INT >= R)
        this.isNavigationBarContrastEnforced = false
    // 设置导航栏透明,暂时没有更好的办法解决透明问题
    this.addFlags(FLAG_TRANSLUCENT_NAVIGATION)
    this.navigationBarColor = TRANSPARENT
}