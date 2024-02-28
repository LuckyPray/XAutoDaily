package me.teble.xposed.autodaily.activity.common

import android.content.res.Configuration
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.agoines.system.common.navigationBarLightMode
import com.agoines.system.common.navigationBarLightOldMode
import com.agoines.system.common.setNavigationBarTranslation
import com.agoines.system.common.setStatusBarTranslation
import com.agoines.system.common.statusBarLightMode
import com.agoines.system.common.statusBarLightOldMode
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ModuleThemeViewModel by viewModels()
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


        lifecycleScope.launch {
            viewModel.theme.collect {
                when (it) {
                    XAutodailyTheme.Theme.Light -> {
                        window.statusBarLightMode()
                        window.navigationBarLightMode()
                    }

                    XAutodailyTheme.Theme.Dark -> {
                        window.statusBarLightMode(false)
                        window.navigationBarLightMode(false)
                    }

                    XAutodailyTheme.Theme.System -> {
                        window.statusBarLightMode(!isNightMode())
                        window.navigationBarLightMode(!isNightMode())
                    }
                }
            }
        }


        setContent {
            val theme by viewModel.theme.collectAsStateWithLifecycle(XAutodailyTheme.Theme.Light)
            val isBlack by viewModel.blackTheme.collectAsStateWithLifecycle(false)
            XAutodailyTheme(isBlack = isBlack, colorTheme = theme) {
                ModuleApp()
            }

        }
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