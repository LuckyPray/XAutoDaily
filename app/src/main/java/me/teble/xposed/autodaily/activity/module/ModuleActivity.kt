package me.teble.xposed.autodaily.activity.module

import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import com.google.accompanist.insets.ProvideWindowInsets
import me.teble.xposed.autodaily.hook.config.Config.accountConfig
import me.teble.xposed.autodaily.hook.proxy.activity.BaseActivity
import me.teble.xposed.autodaily.ui.Cache.currConf
import me.teble.xposed.autodaily.ui.XAutoDailyApp
import me.teble.xposed.autodaily.utils.navigationBarMode
import me.teble.xposed.autodaily.utils.setNavigationBarTranslation
import me.teble.xposed.autodaily.utils.setStatusBarTranslation

class ModuleActivity : BaseActivity() {
    companion object {
        const val TAG = "ModuleActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        this.window.setStatusBarTranslation()
        this.window.setNavigationBarTranslation()
        this.navigationBarMode(true)
        super.onCreate(savedInstanceState)
        // 状态栏和导航栏沉浸
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // 缓存当前账号配置类，避免在UI刷新时重复获取
        currConf = accountConfig
        val view = ComposeView(this).apply {
            // 设置布局为最大
            this.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            this.setContent {
                MaterialTheme {
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