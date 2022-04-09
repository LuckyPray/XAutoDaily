package me.teble.xposed.autodaily.activity.module

import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import com.google.accompanist.insets.ProvideWindowInsets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import me.teble.xposed.autodaily.hook.proxy.activity.BaseActivity
import me.teble.xposed.autodaily.ui.XAutoDailyApp
import me.teble.xposed.autodaily.utils.navigationBarMode
import me.teble.xposed.autodaily.utils.setNavigationBarTranslation
import me.teble.xposed.autodaily.utils.setStatusBarTranslation

class ModuleActivity : BaseActivity(), CoroutineScope by MainScope() {
    companion object {
        const val TAG = "ModuleActivity"
    }

    override fun onDestroy() {
        super.onDestroy()
        this.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        this.window.setStatusBarTranslation()
        this.window.setNavigationBarTranslation()
        this.navigationBarMode(true)
        super.onCreate(savedInstanceState)
        // 状态栏和导航栏沉浸
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val view = ComposeView(this).apply {
            // 设置布局为最大
            this.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            this.setContent {
                MaterialTheme(colors = colors()) {
                    ProvideWindowInsets {
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
            // 保证点击事件不击穿
            setOnClickListener {}
        }
        view.let {
            ViewTreeLifecycleOwner.set(it, this)
            ViewTreeViewModelStoreOwner.set(it, this)
            ViewTreeSavedStateRegistryOwner.set(it, this)
        }
        setContentView(view)
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