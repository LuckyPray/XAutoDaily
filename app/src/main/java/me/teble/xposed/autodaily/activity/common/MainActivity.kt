package me.teble.xposed.autodaily.activity.common

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.activity.module.colors
import me.teble.xposed.autodaily.application.xaApp
import me.teble.xposed.autodaily.config.DataMigrationService
import me.teble.xposed.autodaily.config.PACKAGE_NAME_QQ
import me.teble.xposed.autodaily.config.PACKAGE_NAME_TIM
import me.teble.xposed.autodaily.hook.enums.QQTypeEnum
import me.teble.xposed.autodaily.shizuku.ShizukuApi
import me.teble.xposed.autodaily.shizuku.ShizukuConf
import me.teble.xposed.autodaily.ui.LineCheckBox
import me.teble.xposed.autodaily.ui.LineSwitch
import me.teble.xposed.autodaily.utils.TaskExecutor.CORE_SERVICE_FLAG
import me.teble.xposed.autodaily.utils.TaskExecutor.CORE_SERVICE_TOAST_FLAG
import me.teble.xposed.autodaily.utils.toJsonString
import rikka.shizuku.Shizuku
import java.io.File
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewmodel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch(Main) {
            repeatOnLifecycle(Lifecycle.State.CREATED){
                viewmodel.toastText.collect{
                    Toast.makeText(application, it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        setContent {
            MaterialTheme(colors = colors()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray)
                ) {
                    ModuleView()
                }
            }
        }
    }
}

@Composable
fun ShizukuCard(
    viewmodel : MainViewModel = viewModel()
) {

    val shizukuDaemonRunning by viewmodel.shizukuDaemonRunning.collectAsStateWithLifecycle()
    val shizukuErrInfo by viewmodel.shizukuErrInfo.collectAsStateWithLifecycle()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White, shape = RoundedCornerShape(6.dp))
            .clickable {
                viewmodel.shizukuClickable()
            }
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (ShizukuApi.binderAvailable && !ShizukuApi.isPermissionGranted) {
            Icon(Icons.Outlined.CheckCircle, "Shizuku 服务正在运行")
            Column(Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                Text(
                    text = "Shizuku 服务正在运行（未授权）",
                    fontFamily = FontFamily.Serif,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Shizuku Api Version: ${Shizuku.getVersion()}")
                Text(text = "点击此卡片进行授权", color = Color.Red)
            }
        } else if (ShizukuApi.isPermissionGranted) {
            Icon(Icons.Outlined.CheckCircle, "Shizuku 服务正在运行")
            Column(Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                Text(
                    text = "Shizuku 服务正在运行",
                    fontFamily = FontFamily.Serif,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Shizuku Api Version: ${Shizuku.getVersion()}")
                if (!shizukuDaemonRunning) {
                    if (shizukuErrInfo.isNotEmpty()) {
                        Text(text = "守护进程启动失败: $shizukuErrInfo", color = Color.Red)
                    } else {
                        Text(text = "守护进程未在运行，点击运行", color = Color.Red)
                    }
                } else {
                    Text(text = "守护进程正在运行，点击停止运行", color = Color.Green)
                }
            }
        } else {
            Icon(Icons.Outlined.Warning, "Shizuku 服务未在运行")
            Column(Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                Text(
                    text = "Shizuku 服务未在运行",
                    fontFamily = FontFamily.Serif,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "部分功能无法运行")
            }
        }
    }
}


@Composable
private fun SettingCard(
    viewmodel : MainViewModel = viewModel()
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White, shape = RoundedCornerShape(6.dp))
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.Settings, "设置")
        Column(Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
            Text(
                text = "模块设置",
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "请在 QQ/TIM 内进行操作")
            Text(text = "APP 侧滑 > 设置 > XAutoDaily")
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                val context = LocalContext.current
                Text(text = "QQ",
                    color = Color(0xFF409EFF),
                    modifier = Modifier.clickable {
                        viewmodel.openHostSetting(context, QQTypeEnum.QQ)
                    }
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(text = "TIM",
                    color = Color(0xFF409EFF),
                    modifier = Modifier.clickable {
                        viewmodel.openHostSetting(context, QQTypeEnum.TIM)
                    }
                )
            }
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun ModuleView(
    viewmodel : MainViewModel = viewModel()
) {

        var infoList by remember { mutableStateOf(listOf<String>()) }
        LaunchedEffect(ShizukuApi.isPermissionGranted) {
            infoList = if (!ShizukuApi.isPermissionGranted) {
                if (ShizukuApi.binderAvailable) {
                    listOf("shizuku正在运行，但是未授权，无法勾选")
                } else {
                    listOf("shizuku没有在运行，无法勾选")
                }
            } else {
                listOf()
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(10.dp)
    ) {

    }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .padding(10.dp),
            // 绘制间隔
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                ShizukuCard()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                item {
                    val checked by viewmodel.untrustedTouchEvents.collectAsStateWithLifecycle(false)
                    LineSwitch(
                        title = "取消安卓12不受信触摸",
                        desc = "安卓12后启用对toast弹窗等事件触摸不可穿透，勾选此项可关闭",
                        checked = checked,
                        enabled =true,
                        onChange = {
                            ShizukuApi.setUntrustedTouchEvents(it)
                            viewmodel.updateUntrustedTouchEvents(it)
                        },
                        modifier = Modifier
                            .background(color = Color.White, shape = RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        otherInfoList = infoList
                    )
                }
            }
            item {
                val keepAliveChecked by viewmodel.keepAlive.collectAsStateWithLifecycle(false)
                val qqKeepAlive by viewmodel.qqKeepAlive.collectAsStateWithLifecycle(false)
                val timKeepAlive by viewmodel.timKeepAlive.collectAsStateWithLifecycle(false)
                LaunchedEffect(qqKeepAlive, timKeepAlive, keepAliveChecked) {
                    val confDir = File(xaApp.getExternalFilesDir(null), "conf")
                    if (confDir.isFile) {
                        confDir.delete()
                    }
                    if (!confDir.exists()) {
                        confDir.mkdirs()
                    }
                    val confFile = File(confDir, "conf.json")
                    val conf = ShizukuConf(keepAliveChecked,
                        mutableMapOf<String, Boolean>().apply {
                            put(PACKAGE_NAME_QQ, qqKeepAlive)
                            put(PACKAGE_NAME_TIM, timKeepAlive)
                        })
                    val confString = conf.toJsonString()
                    Log.d("XALog", confString)
                    confFile.writeText(confString)
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.White, shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    LineSwitch(
                        checked = keepAliveChecked,
                        title = "是否启用shizuku保活机制",
                        desc = "通过shizuku运行一个service，当监测到qq/tim被杀死后重新拉起进程",
                        enabled = ShizukuApi.isPermissionGranted,
                        onChange = {
                            viewmodel.updateKeepAliveChecked(!it)
                        },
                        otherInfoList = infoList
                    )
                    if (keepAliveChecked) {
                        if (xaApp.qPackageState.containsKey(PACKAGE_NAME_QQ)) {
                            LineCheckBox(
                                checked = qqKeepAlive,
                                title = "启用qq保活",
                                desc = "单击此处尝试后台唤醒qq，如果模块hook生效将会显示一个气泡",
                                enabled = ShizukuApi.isPermissionGranted,
                                onClick = {
                                    ShizukuApi.startService(PACKAGE_NAME_QQ, DataMigrationService,
                                        arrayOf(
                                            "-e", CORE_SERVICE_FLAG, "$",
                                            "-e", CORE_SERVICE_TOAST_FLAG, "$"
                                        ))
                                },
                                onChange = {

                                    viewmodel.updateQQKeepAlive(it)
                                }
                            )
                        }
                        if (xaApp.qPackageState.containsKey(PACKAGE_NAME_TIM)) {
                            LineCheckBox(
                                checked = timKeepAlive,
                                title = "启用tim保活",
                                desc = "单击此处尝试后台唤醒tim，如果模块hook生效将会显示一个气泡",
                                enabled = ShizukuApi.isPermissionGranted,
                                onClick = {
                                    ShizukuApi.startService(PACKAGE_NAME_TIM, DataMigrationService,
                                        arrayOf(
                                            "-e", CORE_SERVICE_FLAG, "$",
                                            "-e", CORE_SERVICE_TOAST_FLAG, "$"
                                        ))
                                },
                                onChange = {
                                    viewmodel.updateTimKeepAlive(it)
                                }
                            )
                        }
                    }
                }
            }
            item {
                SettingCard()
            }
        }
}