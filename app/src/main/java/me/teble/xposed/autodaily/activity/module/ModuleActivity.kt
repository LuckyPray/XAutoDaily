package me.teble.xposed.autodaily.activity.module

import android.os.Bundle
import android.view.Window
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Colors
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import me.teble.xposed.autodaily.hook.proxy.activity.BaseActivity
import me.teble.xposed.autodaily.ui.XAutoDailyApp
import me.teble.xposed.autodaily.utils.navigationBarLightOldMode
import me.teble.xposed.autodaily.utils.setNavigationBarTranslation
import me.teble.xposed.autodaily.utils.setStatusBarTranslation
import me.teble.xposed.autodaily.utils.statusBarLightOldMode

class ModuleActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatusBarTranslation()
        setNavigationBarTranslation()

        super.onCreate(savedInstanceState)
        window.apply {
            statusBarLightOldMode()
            navigationBarLightOldMode()
        }
        // 状态栏和导航栏沉浸
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7F7F7))) {
                XAutoDailyApp()
            }

        }

    }
}

fun colors(
    primary: Color = Color(0xFF409EFF),
    primaryVariant: Color = Color(0xFF2b7cd9),
    secondary: Color = Color(0xFF409EFF),
    secondaryVariant: Color = Color(0xFF2b7cd9),
    background: Color = Color.White,
    surface: Color = Color.White,
    error: Color = Color(0xFFB00020),
    onPrimary: Color = Color.White,
    onSecondary: Color = Color.Black,
    onBackground: Color = Color.Black,
    onSurface: Color = Color.Black,
    onError: Color = Color.White
): Colors = Colors(
    primary,
    primaryVariant,
    secondary,
    secondaryVariant,
    background,
    surface,
    error,
    onPrimary,
    onSecondary,
    onBackground,
    onSurface,
    onError,
    true
)