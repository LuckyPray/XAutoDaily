package me.teble.xposed.autodaily.activity.common

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.agoines.system.common.navigationBarLightOldMode
import com.agoines.system.common.setNavigationBarTranslation
import com.agoines.system.common.setStatusBarTranslation
import com.agoines.system.common.statusBarLightOldMode
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme

class MainActivity : ComponentActivity() {

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
            XAutodailyTheme {
                ModuleApp()
            }

        }
    }
}