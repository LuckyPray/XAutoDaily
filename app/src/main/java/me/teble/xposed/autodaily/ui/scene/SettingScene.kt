package me.teble.xposed.autodaily.ui.scene

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.teble.xposed.autodaily.activity.module.MainThemeViewModel
import me.teble.xposed.autodaily.ui.composable.RoundedSnackbar
import me.teble.xposed.autodaily.ui.composable.SelectionItem
import me.teble.xposed.autodaily.ui.composable.SmallTitle
import me.teble.xposed.autodaily.ui.composable.SwitchInfoItem
import me.teble.xposed.autodaily.ui.composable.TextInfoItem
import me.teble.xposed.autodaily.ui.composable.TextItem
import me.teble.xposed.autodaily.ui.composable.TopBar
import me.teble.xposed.autodaily.ui.dialog.ThemeModelDialog
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScene(
    backClick: () -> Unit,
    onNavigateToSignState: () -> Unit,
    themeViewModel: MainThemeViewModel,
    viewmodel: SettingViewModel = viewModel()
) {

    val snackbarHostState = remember { SnackbarHostState() }
    // 展示对应 snackbarText
    LaunchedEffect(snackbarHostState) {
        viewmodel.snackbarText.collect {
            snackbarHostState.showSnackbar(it)
        }
    }

    Box {
        val theme by remember { themeViewModel.currentTheme }
        val isBlack by remember { themeViewModel.blackTheme }

        val showTaskToast by remember { viewmodel.showTaskToast }
        val usedThreadPool by remember { viewmodel.usedThreadPool }
        val taskNotification by remember { viewmodel.taskNotification }
        val taskExceptionNotification by remember { viewmodel.taskExceptionNotification }
        val logToXposed by remember { viewmodel.logToXposed }
        val debugLog by remember { viewmodel.debugLog }


        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val themeDialog by remember { viewmodel.themeDialog }

        LaunchedEffect(themeDialog) {
            if (themeDialog) {
                sheetState.expand()
            } else {
                sheetState.hide()
            }
        }

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { RoundedSnackbar(it) }
            },
            topBar = {
                TopBar(text = "设置", backClick = backClick)
            },
            containerColor = colors.colorBgLayout
        ) { contentPadding ->
            // Screen content
            Column(
                Modifier
                    .fillMaxSize()

                    .padding(contentPadding)
                    .padding(horizontal = 16.dp)
                    .clip(SmootherShape(12.dp))
                    .verticalScroll(rememberScrollState())
                    .defaultNavigationBarPadding()
            ) {

                EntryLayout(onNavigateToSignState)
                ConfigLayout(
                    showTaskToast = { showTaskToast },
                    usedThreadPool = { usedThreadPool },
                    taskNotification = { taskNotification },
                    taskExceptionNotification = { taskExceptionNotification },
                    logToXposed = { logToXposed },
                    debugLog = { debugLog },

                    updateShowTaskToast = viewmodel::updateShowTaskToast,
                    updateUsedThreadPool = viewmodel::updateUsedThreadPool,
                    updateTaskNotification = viewmodel::updateTaskNotification,
                    updateTaskExceptionNotification = viewmodel::updateTaskExceptionNotification,
                    updateLogToXposed = viewmodel::updateLogToXposed,
                    updateDebugLog = viewmodel::updateDebugLog,

                    showSnackbar = viewmodel::showSnackbar
                )
                CommonLayout(viewmodel::showThemeDialog)
                BackupLayout(showSnackbar = viewmodel::showSnackbar)
            }
        }


        ThemeModelDialog(
            enable = { themeDialog },
            sheetState = sheetState,
            targetTheme = { theme },
            targetBlack = { isBlack },
            onDismiss = viewmodel::dismissThemeDialog,
            onConfirm = themeViewModel::confirmTheme
        )
    }

}

@Composable
private fun EntryLayout(onNavigateToSignState: () -> Unit) {
    val backgroundColor = colors.colorBgContainer
    SmallTitle(
        title = "模块入口",
        modifier = Modifier
            .padding(bottom = 8.dp, start = 16.dp, top = 8.dp)
    )
    TextItem(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .drawBehind {
                drawRect(backgroundColor)
            },
        text = "签到状态",
        onClick = onNavigateToSignState
    )
}

@Composable
private fun ConfigLayout(
    showTaskToast: () -> Boolean,
    usedThreadPool: () -> Boolean,
    taskNotification: () -> Boolean,
    taskExceptionNotification: () -> Boolean,
    logToXposed: () -> Boolean,
    debugLog: () -> Boolean,

    updateShowTaskToast: (Boolean) -> Unit,
    updateUsedThreadPool: (Boolean) -> Unit,
    updateTaskNotification: (Boolean) -> Unit,
    updateTaskExceptionNotification: (Boolean) -> Unit,
    updateLogToXposed: (Boolean) -> Unit,
    updateDebugLog: (Boolean) -> Unit,

    showSnackbar: (String) -> Unit
) {
    SmallTitle(
        title = "模块配置",
        modifier = Modifier
            .padding(bottom = 8.dp, start = 16.dp, top = 24.dp)
    )

    val backgroundColor = colors.colorBgContainer
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .drawBehind {
                drawRect(backgroundColor)
            },
    ) {
        SwitchInfoItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            enable = showTaskToast,
            clickEnabled = { true },
            text = "签到提示",
            infoText = "执行完签到任务后弹出 Toast 提示",
            onClick = updateShowTaskToast
        )


        SwitchInfoItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            enable = usedThreadPool,
            clickEnabled = { true },
            text = "多线程执行",
            infoText = "启用线程池执行任务，加快任务执行速度",
            onClick = updateUsedThreadPool
        )



        SwitchInfoItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            enable = taskNotification,
            clickEnabled = { true },
            text = "启用签到通知",
            infoText = "任务执行时通知提醒，结束后自动销毁",
            onClick = updateTaskNotification
        )

        SwitchInfoItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            enable = taskExceptionNotification,
            clickEnabled = { true },
            text = "任务异常通知",
            infoText = "当任务执行异常时提示用户，一次性通知",
            onClick = updateTaskExceptionNotification
        )


        SwitchInfoItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            enable = logToXposed,
            clickEnabled = { true },
            text = "日志输出至 Xposed",
            infoText = "是否将日志信息输出至框架日志中（默认方式为输出至 logcat）",
            onClick = updateLogToXposed
        )

        SwitchInfoItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            enable = debugLog,
            clickEnabled = { true },
            text = "输出调试日志",
            infoText = "仅供调试使用",
            onClick = updateDebugLog
        )

        TextInfoItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            clickEnabled = { true },
            text = "日志导出",
            infoText = "保存日志文件打包导出至内部存储",
            onClick = {
                showSnackbar("请选择保存位置")
//                MainScope().launch(IO) {
//                            ToastUtil.send("请选择保存位置")
//
//                        }
            }
        )
    }
}

@Composable
private fun CommonLayout(showThemeDialog: () -> Unit) {
    val backgroundColor = colors.colorBgContainer
    SmallTitle(
        title = "模块配置",
        modifier = Modifier
            .padding(bottom = 8.dp, start = 16.dp, top = 24.dp)
    )

    SelectionItem(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .drawBehind {
                drawRect(backgroundColor)
            },
        text = "主题风格", clickEnabled = { true },
        onClick = showThemeDialog
    )
}

@Composable
private fun BackupLayout(showSnackbar: (String) -> Unit) {
    SmallTitle(
        title = "模块配置",
        modifier = Modifier
            .padding(bottom = 8.dp, start = 16.dp, top = 24.dp)
    )

    val colors = colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .drawBehind {
                drawRect(colors.colorBgContainer)
            },
    ) {
        TextInfoItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            clickEnabled = { true },
            text = "配置备份",
            infoText = "将配置文件打包导出至内部存储",
            onClick = {
                showSnackbar("请选择保存位置")
//                            ToastUtil.send("请选择保存位置")
//                            configBackupLauncher.launch("XAutoDaily_config_${LocalDateTime.now()}.zip")
            }
        )
        TextInfoItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            clickEnabled = { true },
            text = "配置恢复",
            infoText = "选择待恢复配置文件",
            onClick = {

            }
        )
    }
}