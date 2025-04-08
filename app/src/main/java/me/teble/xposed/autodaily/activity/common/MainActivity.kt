package me.teble.xposed.autodaily.activity.common

import android.content.res.Configuration
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import com.agoines.system.common.navigationBarLightMode
import com.agoines.system.common.navigationBarLightOldMode
import com.agoines.system.common.setNavigationBarTranslation
import com.agoines.system.common.setStatusBarTranslation
import com.agoines.system.common.statusBarLightMode
import com.agoines.system.common.statusBarLightOldMode
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ModuleThemeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        setStatusBarTranslation()
        setNavigationBarTranslation()
        enableEdgeToEdge()
        when (XAutodailyTheme.getAppTheme()) {
            XAutodailyTheme.Theme.Light -> setSystemBarOldMode()
            XAutodailyTheme.Theme.Dark -> setSystemBarOldMode(false)
            XAutodailyTheme.Theme.System -> setSystemBarOldMode(!isNightMode())

        }
        super.onCreate(savedInstanceState)
        // 状态栏和导航栏沉浸
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.setDecorFitsSystemWindows(window, false)


        setContent {

            when (viewModel.currentTheme) {
                XAutodailyTheme.Theme.Light -> setSystemBarMode()
                XAutodailyTheme.Theme.Dark -> setSystemBarMode(false)
                XAutodailyTheme.Theme.System -> setSystemBarMode(!isNightMode())
            }

            XAutodailyTheme(
                isBlack = viewModel.blackTheme,
                colorTheme = viewModel.currentTheme
            ) {
                ModuleApp(viewModel)
            }

        }
    }

    private fun setSystemBarOldMode(enable: Boolean = true) {
        window.statusBarLightOldMode(enable)
        window.navigationBarLightOldMode(enable)
    }

    private fun setSystemBarMode(enable: Boolean = true) {
        window.statusBarLightMode(enable)
        window.navigationBarLightMode(enable)
    }

    /**
     * 判断当前是否是暗色模式
     * @return 当前是否是暗色模式
     */
    private fun isNightMode(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }
}