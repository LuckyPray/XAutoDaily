package me.teble.xposed.autodaily.activity.module

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.WindowCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import me.teble.xposed.autodaily.hook.proxy.activity.BaseActivity
import me.teble.xposed.autodaily.ui.XAutoDailyApp

class ModuleActivity : BaseActivity(), CoroutineScope by MainScope() {

    override fun onDestroy() {
        super.onDestroy()
        this.cancel()
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        systemBars.isDarkColorStatusBars = false
        systemBars.isDarkColorNavigationBars = true
        // 状态栏和导航栏沉浸
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MaterialTheme(colors = colors()) {
                Scaffold {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                    ) {
                        XAutoDailyApp()
                    }
                }
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