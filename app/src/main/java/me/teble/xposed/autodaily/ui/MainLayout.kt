package me.teble.xposed.autodaily.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import kotlinx.coroutines.delay
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.R
import me.teble.xposed.autodaily.config.Constants
import me.teble.xposed.autodaily.hook.CoreServiceHook.Companion.EXEC_TASK
import me.teble.xposed.autodaily.hook.CoreServiceHook.Companion.handler
import me.teble.xposed.autodaily.hook.config.Config.accountConfig
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.task.util.ConfigUtil.fetchUpdateInfo
import me.teble.xposed.autodaily.task.util.ConfigUtil.getCurrentExecTaskNum
import me.teble.xposed.autodaily.task.util.Const.GLOBAL_ENABLE
import me.teble.xposed.autodaily.ui.XAutoDailyApp.Main
import me.teble.xposed.autodaily.ui.XAutoDailyApp.Sign
import kotlin.concurrent.thread

@ExperimentalFoundationApi
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainLayout(navController: NavHostController) {
    val notice = remember { mutableStateOf("") }
    val showUpdateDialog = remember { mutableStateOf(false) }
    val updateDialogText = remember { mutableStateOf("") }
    val lastClickTime = remember { mutableStateOf(0L) }
    LaunchedEffect(notice) {
        thread {
            val info = Cache.versionInfoCache ?: fetchUpdateInfo()
            if (System.currentTimeMillis() - Cache.lastFetchTime > 60 * 60 * 1000L)
                fetchUpdateInfo()
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
                    data = Uri.parse(Constants.GITHUB_RELEASE_URL)
                })
            },
            onLanzou = {
                navController.context.startActivity(Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse(Constants.PAN_URL)
                })
            },
            onDismiss = {
                showUpdateDialog.value = false
            }
        )
    }
    ActivityView(navController = navController) {
        LazyColumn(
            modifier = Modifier.padding(13.dp)
        ) {
            item {
                BackgroundView(/*scrollUpState = scrollUpState, viewOffset = maxOffsetY*/)
                LineSpacer()
            }
            stickyHeader {
                Announcement(post = notice)
            }
            item {
                LineSpacer()
                LineSwitch(
                    title = "总开关",
                    checked = mutableStateOf(
                        accountConfig.getBoolean(
                            GLOBAL_ENABLE,
                            false
                        )
                    ),
                    desc = "关闭后一切任务都不会执行",
                    onChange = {
                        accountConfig.putBoolean(GLOBAL_ENABLE, it)
                    },
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
            item {
                LineSpacer()
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
//            item {
//                LineSpacer()
//                LineButton(
//                    title = "模块设置",
//                    desc = "模块相关的功能配置",
//                    onClick = {
//                        navController.navigate(Setting) {
//                            popUpTo(Main)
//                        }
//                    },
//                    modifier = Modifier.padding(vertical = 8.dp),
//                )
//            }
            item {
                LineSpacer()
                LineButton(
                    title = "自定义签到脚本",
                    desc = "敬请期待",
                    onClick = { ToastUtil.send("敬请期待") },
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
//            item {
//                LineSpacer()
//                LineButton(
//                    title = "获取测试版本配置(BETA通道)",
//                    desc = "仅测试人员可见",
//                    onClick = {
//                        showFriendsDialog.value = true
//                    },
//                    modifier = Modifier.padding(vertical = 8.dp),
//                )
//            }
            item {
                LineSpacer()
                LineButton(
                    title = "前往项目地址",
                    otherInfoList = listOf(
                        "模块作者：韵の祈(teble@github.com)",
                        "特别鸣谢：KyuubiRan、MaiTungTM",
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
                LineSpacer()
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
                LineSpacer()
                LineButton(
                    title = "检测更新",
                    otherInfoList = listOf(
                        "当前模块版本：${BuildConfig.VERSION_NAME}",
                        "当前宿主版本：${Cache.qqVersionName}(${Cache.qqVersionCode})",
                        "当前配置版本：${Cache.configVer}"
                    ),
                    onClick = {
                        val time = System.currentTimeMillis()
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
                                    Cache.versionInfoCache!!.updateLog.joinToString("\n")
                            }
                        }
                    },
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
            item {
                LineSpacer()
                LineButton(
                    title = "请吃作者辣条",
                    desc = "本模块完全免费开源，一切开发旨在学习，请勿用于非法用途。习欢本模块的可以捐赠支持我，谢谢~~",
                    onClick = {
                        ToastUtil.send("正在跳转，请稍后")
                        val qrCode = Constants.ALIPAY_QRCODE
                        val intent = Intent.parseUri(
                            "intent://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F{qrCode}%3F_s%3Dweb-other&_t=1472443966571#Intent;scheme=alipayqr;package=com.eg.android.AlipayGphone;end"
                                .replace("{qrCode}", qrCode),
                            0
                        )
                        navController.context.startActivity(intent)
                    },
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
fun BackgroundView() {
    val lastClickTime = remember { mutableStateOf(0L) }
    val execTaskNum = remember { mutableStateOf(0) }
    LaunchedEffect(execTaskNum) {
        val num = getCurrentExecTaskNum()
        for (i in 1..num) {
            delay(20)
            execTaskNum.value++
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
            Button(
                onClick = {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastClickTime.value < 5000) {
                        ToastUtil.send("点那么快怎么不上天呢")
                        return@Button
                    }
                    lastClickTime.value = currentTime
                    handler.sendEmptyMessage(EXEC_TASK)
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


@ExperimentalFoundationApi
@Preview
@Composable
fun PreviewMainLayout() {
    MainLayout(rememberNavController())
}