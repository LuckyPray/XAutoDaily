package me.teble.xposed.autodaily.utils

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

context(Activity)
fun setStatusBarTranslation() {
    window.setStatusBarTranslation()
}

/**
 * 设置状态栏透明
 */
@Suppress("DEPRECATION")
fun Window.setStatusBarTranslation() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        isStatusBarContrastEnforced = false
    // 设置状态栏透明,暂时没有更好的办法解决透明问题
    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    if (isMiuiDevices())
        addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    statusBarColor = Color.TRANSPARENT
}


context(Activity)
fun setNavigationBarTranslation() {
    window.setNavigationBarTranslation()
}

/**
 * 设置导航栏透明
 */
@Suppress("DEPRECATION")
fun Window.setNavigationBarTranslation() {
    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    if (isMiuiDevices())
        addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    // 防止透明以后高对比度
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        isNavigationBarContrastEnforced = false
    // 去除导航栏线段
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        navigationBarDividerColor = Color.TRANSPARENT
    navigationBarColor = Color.TRANSPARENT
}


context(Activity)
fun statusBarLightMode(enable: Boolean = true) {
    window.statusBarLightMode(enable)
}

/**
 * 设置亮色状态栏
 * @param enable 默认为亮色
 * https://qa.1r1g.com/sf/ask/4595541261/ 没有找到英文原文，
 * 这个更适合在页面有绘制的 View 的时候调用
 */
fun Window.statusBarLightMode(enable: Boolean = true) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        insetsController?.apply {
            setSystemBarsAppearance(
                if (enable) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }
    statusBarLightOldMode(enable)
}

/**
 * 设置亮色状态栏（对应旧版本）
 * @param enable 默认为亮色
 */
@Suppress("DEPRECATION")
fun Window.statusBarLightOldMode(enable: Boolean = true) {
    decorView.apply {
        systemUiVisibility = if (enable)
            systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        else
            systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    }
}

context(Activity)
fun navigationBarHide() {
    window.navigationBarHide()
}

fun Window.navigationBarHide() {
    val windowInsetsController = WindowCompat.getInsetsController(this, this.decorView)
    windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
}

context(Activity)
fun navigationBarShow() {
    window.navigationBarShow()
}

fun Window.navigationBarShow() {
    val windowInsetsController = WindowCompat.getInsetsController(this, this.decorView)
    windowInsetsController.show(WindowInsetsCompat.Type.navigationBars())
}


context(Activity)
fun navigationBarLightMode(enable: Boolean = true) {
    window.navigationBarLightMode(enable)
}

/**
 * 设置亮色导航栏
 * @param enable 默认为亮色
 * 同上
 */

fun Window.navigationBarLightMode(enable: Boolean = true) {
    setNavigationBarTranslation()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        insetsController?.apply {
            setSystemBarsAppearance(
                if (enable) WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS else 0,
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            )
        }
    navigationBarLightOldMode(enable)
}

/**
 * 设置亮色导航栏（对应旧版本）
 * @param enable 默认为亮色
 */

@Suppress("DEPRECATION")
fun Window.navigationBarLightOldMode(enable: Boolean = true) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        decorView.apply {
            systemUiVisibility = if (enable)
                systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            else
                systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
}