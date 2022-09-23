package me.teble.xposed.autodaily.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.R
import me.teble.xposed.autodaily.config.ALIPAY_QRCODE
import me.teble.xposed.autodaily.config.GITHUB_RELEASE_URL
import me.teble.xposed.autodaily.config.PAN_URL
import me.teble.xposed.autodaily.hook.base.hostVersionCode
import me.teble.xposed.autodaily.hook.base.hostVersionName
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.task.util.ConfigUtil.fetchUpdateInfo
import me.teble.xposed.autodaily.task.util.ConfigUtil.getCurrentExecTaskNum
import me.teble.xposed.autodaily.task.util.ConfigUtil.loadSaveConf
import me.teble.xposed.autodaily.ui.XAutoDailyApp.Main
import me.teble.xposed.autodaily.ui.XAutoDailyApp.Other
import me.teble.xposed.autodaily.ui.XAutoDailyApp.Sign
import me.teble.xposed.autodaily.ui.utils.RippleCustomTheme
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.TaskExecutor.EXEC_TASK
import me.teble.xposed.autodaily.utils.TaskExecutor.handler
import me.teble.xposed.autodaily.utils.TimeUtil
import me.teble.xposed.autodaily.utils.openUrl
import java.util.concurrent.CompletableFuture.runAsync
import kotlin.concurrent.thread

@Composable
fun MainLayout(navController: NavHostController) {
    val notice = remember { mutableStateOf("") }
    val showUpdateDialog = remember { mutableStateOf(false) }
    val updateDialogText = remember { mutableStateOf("") }
    val lastClickTime = remember { mutableStateOf(0L) }
    LaunchedEffect(notice) {
        runAsync {
            val info = ConfUnit.versionInfoCache ?: fetchUpdateInfo()
            if (System.currentTimeMillis() - ConfUnit.lastFetchTime > 60 * 60 * 1000L) {
                fetchUpdateInfo()
            }
            info ?: ToastUtil.send("拉取公告失败")
            notice.value = info?.notice ?: ""
        }
    }
    if (showUpdateDialog.value) {
        UpdateDialog(
            title = "版本更新",
            text = updateDialogText.value,
            onGithub = {
                navController.context.startActivity(Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse(GITHUB_RELEASE_URL)
                })
            },
            onLanzou = {
                navController.context.startActivity(Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse(PAN_URL)
                })
            },
            onDismiss = {
                showUpdateDialog.value = false
            }
        )
    }

    ActivityView(title = "XAutoDaily") {
        LazyColumn(
            modifier = Modifier
                .padding(top = 13.dp)
                .padding(horizontal = 13.dp),
            contentPadding = WindowInsets.Companion.navigationBars.asPaddingValues(),
//            contentPadding = rememberInsetsPaddingValues(LocalWindowInsets.current.navigationBars),
            // 绘制间隔
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                BackgroundView()
            }
            item {
                Announcement(post = notice)
            }
            item {
                val globalSwitch = remember {
                    mutableStateOf(ConfUnit.globalEnable)
                }
                LineSwitch(
                    title = "总开关",
                    checked = globalSwitch,
                    desc = "关闭后一切任务都不会执行",
                    onChange = {
                        ConfUnit.globalEnable = it
                    },
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
            item {
                LineButton(
                    title = "签到配置",
                    desc = "在这里选择普通签到项目，以及进行相关的参数设置",
                    onClick = {
                        navController.navigate(Sign) {
                            popUpTo(Main)
                        }
                    },
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
            item {
                LineButton(
                    title = "其它",
                    desc = "模块配置、日志及备份",
                    onClick = {
                        navController.navigate(Other) {
                            popUpTo(Main)
                        }
                    },
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
            item {
                LineButton(
                    title = "自定义签到脚本",
                    desc = "敬请期待",
                    onClick = { ToastUtil.send("敬请期待") },
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
            if (BuildConfig.DEBUG) {
                item {
                    LineButton(
                        title = "获取测试版本配置(BETA通道)",
                        desc = "仅测试人员可见",
                        onClick = {
                            ToastUtil.send("")
                        },
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }
            }
            item {
                LineButton(
                    title = "前往项目地址",
                    otherInfoList = listOf(
                        "模块作者：韵の祈(teble@github.com)",
                        "特别鸣谢：KyuubiRan、MaiTungTM、cinit、Agoines",
                        "ps：我要好多好多小星星！"
                    ),
                    onClick = {
                        navController.context.startActivity(Intent().apply {
                            action = Intent.ACTION_VIEW
                            data = Uri.parse("https://github.com/teble/XAutoDaily")
                        })
                    },
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
            item {
                LineButton(
                    title = "点击加入tg频道",
                    otherInfoList = listOf(
                        "频道：@XAutoDaily",
                        "群组：@XAutoDailyChat",
                        "自备工具哦~",
                    ),
                    onClick = {
                        ToastUtil.send("正在跳转，请稍后")
                        navController.context.startActivity(Intent().apply {
                            action = Intent.ACTION_VIEW
                            data = Uri.parse("tg://resolve?domain=XAutoDaily")
                        })
                    },
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
            item {
                var moduleVersionName by remember { mutableStateOf("") }
                var moduleVersionCode by remember { mutableStateOf(0) }
                var qqVersionName by remember { mutableStateOf("") }
                var qqVersionCode by remember { mutableStateOf(0L) }
                var configVersion by remember { mutableStateOf(0) }
                LaunchedEffect(qqVersionName) {
                    moduleVersionName = BuildConfig.VERSION_NAME
                    moduleVersionCode = BuildConfig.VERSION_CODE
                    qqVersionName = hostVersionName
                    qqVersionCode = hostVersionCode
                    configVersion = loadSaveConf().version
                }
                LineButton(
                    title = "检测更新",
                    otherInfoList = listOf(
                        "当前模块版本：${moduleVersionName}(${moduleVersionCode})",
                        "当前宿主版本：${qqVersionName}(${qqVersionCode})",
                        "当前配置版本：${configVersion}"
                    ),
                    onClick = {
                        val time = TimeUtil.cnTimeMillis()
                        if (time - lastClickTime.value < 15_000) {
                            ToastUtil.send("不要频繁点击哦~")
                            return@LineButton
                        }
                        lastClickTime.value = time
                        thread {
                            ToastUtil.send("正在检测更新")
                            val res = ConfigUtil.checkUpdate(true)
                            if (res) {
                                showUpdateDialog.value = true
                                updateDialogText.value =
                                    ConfUnit.versionInfoCache?.updateLog?.joinToString("\n") ?: ""
                            }
                            configVersion = loadSaveConf().version
                        }
                    },
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
            item {
                LineButton(
                    title = "请吃作者辣条",
                    desc = "本模块完全免费开源，一切开发旨在学习，请勿用于非法用途。喜欢本模块的可以捐赠支持我，谢谢~~",
                    onClick = {
                        ToastUtil.send("正在跳转，请稍后")
                        val context = navController.context
                        try {
                            context.openUrl(
                                "alipayqr://platformapi/startapp?saId=10000007&clientVersion=" +
                                    "3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F$ALIPAY_QRCODE%3F_s%3Dweb-other"
                            )
                        } catch (e: Exception) {
                            LogUtil.e(e, "open alipay qr error: ")
                            context.openUrl("https://mobilecodec.alipay.com/client_download.htm?qrcode=$ALIPAY_QRCODE")
                        }
                    },
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
fun BackgroundView() {
    var lastClickTime by remember { mutableStateOf(0L) }
    val execTaskNum = remember { mutableStateOf(0) }
    LaunchedEffect(execTaskNum) {
        try {
            val num = getCurrentExecTaskNum()
            for (i in 1..num) {
                delay(15)
                execTaskNum.value++
            }
        } catch (e: Exception) {
            ToastUtil.send(e.stackTraceToString())
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(13.dp)),
        backgroundColor = Color.Unspecified
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_bc),
            contentScale = ContentScale.FillWidth,
            contentDescription = "",
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .size(115.dp)
                    .clip(CircleShape)
                    .background(Color(0x66FFFFFF))
                    .padding(11.dp)
                    .clip(CircleShape)
                    .background(Color(0x99FFFFFF))
                    .padding(11.dp)
                    .clip(CircleShape)
                    .background(Color(0xCCFFFFFF)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = execTaskNum.value.toString(),
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        fontSize = 32.sp,
                        color = Color(0xFF409EFF),
                    )
                    Text(
                        text = "今日执行", fontSize = 13.sp,
                        color = Color(0xFF409EFF),
                        modifier = Modifier.offset(y = (-5).dp)
                    )
                }
            }
            LineSpacer()
            CompositionLocalProvider(
                LocalRippleTheme provides RippleCustomTheme(
                    color = Color(
                        0xFF409EFF
                    )
                )
            ) {
                Button(
                    onClick = {
                        runAsync {
                            val currentTime = TimeUtil.cnTimeMillis()
                            if (currentTime - lastClickTime < 5000) {
                                ToastUtil.send("点那么快怎么不上天呢")
                                return@runAsync
                            }
                            lastClickTime = currentTime
                            handler.sendEmptyMessage(EXEC_TASK)
                        }
                    },
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.textButtonColors(backgroundColor = Color.White),
                    modifier = Modifier
                        .height(35.dp)
                ) {

                    Text(
                        text = "立即签到",
                        fontSize = 15.sp,
                        color = Color(0xFF409EFF),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
fun PreviewMainLayout() {
    MainLayout(rememberNavController())
}