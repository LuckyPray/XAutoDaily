package me.teble.xposed.autodaily.activity.module

import android.content.res.Configuration
import android.os.Bundle
import android.view.Window
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import com.agoines.system.common.navigationBarLightMode
import com.agoines.system.common.navigationBarLightOldMode
import com.agoines.system.common.setNavigationBarTranslation
import com.agoines.system.common.setStatusBarTranslation
import com.agoines.system.common.statusBarLightMode
import com.agoines.system.common.statusBarLightOldMode
import me.teble.xposed.autodaily.hook.proxy.activity.BaseActivity
import me.teble.xposed.autodaily.ui.XAutoDailyApp
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme

class ModuleActivity : BaseActivity() {

    private val viewModel: MainThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatusBarTranslation()
        setNavigationBarTranslation()

        when (XAutodailyTheme.getCurrentTheme()) {
            XAutodailyTheme.Theme.Light -> {
                window.statusBarLightOldMode()
                window.navigationBarLightOldMode()
            }

            XAutodailyTheme.Theme.Dark -> {
                window.statusBarLightOldMode(false)
                window.navigationBarLightOldMode(false)
            }

            XAutodailyTheme.Theme.System -> {
                window.statusBarLightOldMode(!isNightMode())
                window.navigationBarLightOldMode(!isNightMode())
            }
        }


        super.onCreate(savedInstanceState)
        // 状态栏和导航栏沉浸
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.setDecorFitsSystemWindows(window, false)




        setContent {
            val theme by remember { viewModel.currentTheme }
            val isBlack by remember { viewModel.blackTheme }

            when (theme) {
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

            XAutodailyTheme(isBlack = isBlack, colorTheme = theme) {
                XAutoDailyApp(viewModel)
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