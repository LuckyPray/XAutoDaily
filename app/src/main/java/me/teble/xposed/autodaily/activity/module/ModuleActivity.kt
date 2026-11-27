package me.teble.xposed.autodaily.activity.module

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.Window
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.agoines.system.common.navigationBarLightMode
import com.agoines.system.common.setNavigationBarTranslation
import com.agoines.system.common.setStatusBarTranslation
import com.agoines.system.common.statusBarLightMode
import me.teble.xposed.autodaily.activity.common.ThemeViewModel
import me.teble.xposed.autodaily.hook.proxy.activity.BaseActivity
import me.teble.xposed.autodaily.hook.proxy.activity.injectRes
import me.teble.xposed.autodaily.ui.XAutoDailyApp
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme
import java.lang.ref.WeakReference

class ModuleActivity : BaseActivity() {
    companion object {
        var composeViewContext = WeakReference<Context>(null)
    }

    private val viewModel: ThemeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        setStatusBarTranslation()
        setNavigationBarTranslation()
        enableEdgeToEdge()
        setSystemBarMode(!XAutodailyTheme.getCurrentTheme().isDark(resources.configuration))

        super.onCreate(savedInstanceState)
        // 状态栏和导航栏沉浸
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {

            composeViewContext = WeakReference(LocalContext.current)
            val lightSystemBars = !viewModel.currentTheme.isDark(LocalConfiguration.current)
            SideEffect { setSystemBarMode(lightSystemBars) }

            XAutodailyTheme(
                isBlack = viewModel.blackTheme,
                colorTheme = viewModel.currentTheme
            ) {
                XAutoDailyApp(viewModel)
            }

        }
    }

    private fun setSystemBarMode(enable: Boolean) {
        window.statusBarLightMode(enable)
        window.navigationBarLightMode(enable)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        injectActivityResources()
        super.onConfigurationChanged(newConfig)
    }

    override fun onResume() {
        injectActivityResources()
        super.onResume()
    }

    private fun injectActivityResources() {
        val activityResources = resources
        injectRes(activityResources)
        composeViewContext.get()?.resources?.let { composeResources ->
            if (composeResources !== activityResources) injectRes(composeResources)
        }
    }
}
