package me.teble.xposed.autodaily.ui.scene

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import me.teble.xposed.autodaily.ui.composable.RoundedSnackbar
import me.teble.xposed.autodaily.ui.composable.SelectionItem
import me.teble.xposed.autodaily.ui.composable.SmallTitle
import me.teble.xposed.autodaily.ui.composable.SwitchInfoItem
import me.teble.xposed.autodaily.ui.composable.TextInfoItem
import me.teble.xposed.autodaily.ui.composable.TextItem
import me.teble.xposed.autodaily.ui.composable.TopBar
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.layout.verticalScrollPadding

@Composable
fun SettingScene(navController: NavController, viewmodel: SettingViewModel = viewModel()) {

    val snackbarHostState = remember { SnackbarHostState() }
    // 展示对应 snackbarText
    LaunchedEffect(snackbarHostState) {
        viewmodel.snackbarText.collect {
            snackbarHostState.showSnackbar(it)
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { RoundedSnackbar(it) }
        },
        topBar = {
            TopBar(text = "设置", backClick = {
                navController.popBackStack()
            })
        },
        backgroundColor = Color(0xFFF7F7F7)
    ) { contentPadding ->
        // Screen content
        Column(
            Modifier
                .fillMaxSize()

                .padding(contentPadding)
                .padding(horizontal = 16.dp)
                .clip(SmootherShape(12.dp))
                .verticalScroll(rememberScrollState())
                .verticalScrollPadding()
        ) {

            EntryLayout()
            ConfigLayout()
            CommonLayout()
            BackupLayout()
        }
    }
}

@Composable
private fun EntryLayout() {
    SmallTitle(
        title = "模块入口",
        modifier = Modifier
            .padding(bottom = 8.dp, start = 16.dp, top = 16.dp)
    )
    TextItem(
        modifier = Modifier

            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .background(color = Color(0xffffffff)),
        text = "签到状态", clickEnabled = true,
        onClick = {

        })
}

@Composable
private fun ConfigLayout(viewmodel: SettingViewModel = viewModel()) {
    SmallTitle(
        title = "模块配置",
        modifier = Modifier
            .padding(bottom = 8.dp, start = 16.dp, top = 24.dp)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .background(color = Color(0xffffffff)),
    ) {

        val showTaskToast by viewmodel.showTaskToast.collectAsState()
        SwitchInfoItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            enable = showTaskToast,
            clickEnabled = true,
            text = "签到提示",
            infoText = "执行完签到任务后弹出 Toast 提示",
            onClick = {
                viewmodel.updateShowTaskToast(it)
            }
        )


        val usedThreadPool by viewmodel.usedThreadPool.collectAsState()
        SwitchInfoItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            enable = usedThreadPool,
            clickEnabled = true,
            text = "多线程执行",
            infoText = "启用线程池执行任务，加快任务执行速度",
            onClick = {
                viewmodel.updateUsedThreadPool(it)
            }
        )

        val taskNotification by viewmodel.taskNotification.collectAsState()


        SwitchInfoItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            enable = taskNotification,
            clickEnabled = true,
            text = "启用签到通知",
            infoText = "任务执行时通知提醒，结束后自动销毁",
            onClick = {
                viewmodel.updateTaskNotification(it)
            }
        )

        val taskExceptionNotification by viewmodel.taskExceptionNotification.collectAsState()
        SwitchInfoItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            enable = taskExceptionNotification,
            clickEnabled = true,
            text = "任务异常通知",
            infoText = "当任务执行异常时提示用户，一次性通知",
            onClick = {
                viewmodel.updateTaskExceptionNotification(it)
            }
        )

        val logToXposed by viewmodel.logToXposed.collectAsState()

        SwitchInfoItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            enable = logToXposed,
            clickEnabled = true,
            text = "日志输出至 Xposed",
            infoText = "是否将日志信息输出至框架日志中（默认方式为输出至 logcat）",
            onClick = {
                viewmodel.updateLogToXposed(it)
            }
        )
        val debugLog by viewmodel.debugLog.collectAsState()

        SwitchInfoItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            enable = debugLog,
            clickEnabled = true,
            text = "输出调试日志",
            infoText = "仅供调试使用",
            onClick = {
                viewmodel.updateDebugLog(it)
            }
        )

        TextInfoItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            clickEnabled = true,
            text = "日志导出",
            infoText = "保存日志文件打包导出至内部存储",
            onClick = {
                viewmodel.showSnackbar("请选择保存位置")
//                MainScope().launch(IO) {
//                            ToastUtil.send("请选择保存位置")
//
//                        }
            }
        )
    }
}

@Composable
private fun CommonLayout() {
    SmallTitle(
        title = "模块配置",
        modifier = Modifier
            .padding(bottom = 8.dp, start = 16.dp, top = 24.dp)
    )

    SelectionItem(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .background(color = Color(0xffffffff)),
        text = "主题风格", clickEnabled = true,
        onClick = {

        })
}

@Composable
private fun BackupLayout(viewmodel: SettingViewModel = viewModel()) {
    SmallTitle(
        title = "模块配置",
        modifier = Modifier
            .padding(bottom = 8.dp, start = 16.dp, top = 24.dp)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .background(color = Color(0xffffffff)),
    ) {
        TextInfoItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            clickEnabled = true,
            text = "配置备份",
            infoText = "将配置文件打包导出至内部存储",
            onClick = {
                viewmodel.showSnackbar("请选择保存位置")
//                            ToastUtil.send("请选择保存位置")
//                            configBackupLauncher.launch("XAutoDaily_config_${LocalDateTime.now()}.zip")
            }
        )
        TextInfoItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            clickEnabled = true,
            text = "配置恢复",
            infoText = "选择待恢复配置文件",
            onClick = {

            }
        )
    }
}