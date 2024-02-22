package me.teble.xposed.autodaily.activity.common

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.agoines.system.common.navigationBarLightOldMode
import com.agoines.system.common.setNavigationBarTranslation
import com.agoines.system.common.setStatusBarTranslation
import com.agoines.system.common.statusBarLightOldMode

class MainActivity : ComponentActivity() {
    private val viewmodel: ModuleViewModel by viewModels()

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
            MaterialTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                // 展示对应 snackbarText
                LaunchedEffect(snackbarHostState) {
                    viewmodel.snackbarText.collect {
                        snackbarHostState.showSnackbar(it)
                    }
                }
                Scaffold(
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    },
                ) { contentPadding ->
                    // Screen content
                    Box(modifier = Modifier.padding(contentPadding)) {
                        ModuleApp()
                    }

                }


            }

        }
    }
}