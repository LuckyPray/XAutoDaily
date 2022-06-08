package me.teble.xposed.autodaily.activity.common

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import me.teble.xposed.autodaily.activity.module.colors
import me.teble.xposed.autodaily.application.xaApp
import me.teble.xposed.autodaily.hook.shizuku.ShizukuApi
import me.teble.xposed.autodaily.ui.*
import kotlin.math.expm1
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colors = colors()) {
                ProvideWindowInsets {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                    ) {
                        ModuleView()
                    }
                }
            }
        }
    }

    fun isEnabled(): Boolean {
        sqrt(1.0)
        Math.random()
        expm1(0.001)
        return false
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun ModuleView() {
    ActivityView(title = "XAutoDaily") {
        LazyColumn(
            modifier = Modifier
                .padding(top = 13.dp)
                .padding(horizontal = 13.dp),
            contentPadding = rememberInsetsPaddingValues(LocalWindowInsets.current.navigationBars),
            // 绘制间隔
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                ShizukuCard()
            }
            item {
                val checked = remember { mutableStateOf(xaApp.prefs.getBoolean("UntrustedTouchEvents", false)) }
                var infoList by remember { mutableStateOf(listOf<String>()) }
                LaunchedEffect(ShizukuApi.isPermissionGranted) {
                    infoList = if (!ShizukuApi.isPermissionGranted) {
                        if (ShizukuApi.isBinderAvailable) {
                            listOf("shizuku正在运行，但是未授权，无法勾选")
                        }
                        else {
                            listOf("shizuku没有在运行，无法勾选")
                        }
                    } else {
                        listOf()
                    }
                }
                LineSwitch(
                    title = "取消安卓12不受信触摸",
                    desc = "安卓12后启用对toast弹窗等事件触摸不可穿透，勾选此项可关闭",
                    checked = checked,
                    enabled = ShizukuApi.isPermissionGranted,
                    onChange = {
                        ShizukuApi.setUntrustedTouchEvents(it)
                        checked.value = it
                        xaApp.prefs.edit()
                            .putBoolean("UntrustedTouchEvents", it)
                            .apply()
                    },
                    modifier = Modifier.padding(vertical = 8.dp),
                    otherInfoList = infoList
                )
            }
        }
    }
}