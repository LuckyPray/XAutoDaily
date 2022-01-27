package me.teble.xposed.autodaily.activity.module

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import me.teble.xposed.autodaily.hook.proxy.activity.BaseActivity
import me.teble.xposed.autodaily.ui.XAutoDailyApp

class ModuleActivity : BaseActivity() {
    companion object {
        const val TAG = "ModuleActivity"
    }

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 透明状态栏 & 导航栏
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        setContent {
            Box(
                Modifier
                    .fillMaxSize()
                    .clickable(
                        // 防止击穿
                        onClick = {},
                        // 去掉点击水波纹
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                    .background(colors.background)
            ) {
                XAutoDailyApp()
            }
        }
    }
}