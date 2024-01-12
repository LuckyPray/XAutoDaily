package me.teble.xposed.autodaily.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.hook.base.hostContext
import me.teble.xposed.autodaily.hook.config.Config
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.utils.FileUtil
import me.teble.xposed.autodaily.utils.LogUtil
import java.io.File
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture.runAsync
import kotlin.system.exitProcess


@Composable
fun OtherLayout(navController: NavHostController) {
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    val restoreLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            fileUri = uri
        }
    val logSaveLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri: Uri? ->
            uri?.let {
                runAsync {
                    val context = hostContext
                    val contentResolver =
                        context.contentResolver
                    try {
                        contentResolver.openFileDescriptor(uri, "wt").use { zipFd ->
                            zipFd?.let {
                                FileUtil.saveLogs(zipFd)
                            }
                        }
                        ToastUtil.send("导出成功")
                    } catch (e: Throwable) {
                        LogUtil.e(e, "save log failed")
                    }
                }
            }
        }
    val configBackupLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri: Uri? ->
            uri?.let {
                runAsync {
                    val context = hostContext
                    val contentResolver = context.contentResolver
                    try {
                        contentResolver.openFileDescriptor(uri, "wt").use { zipFd ->
                            zipFd?.let {
                                FileUtil.backupConfig(zipFd)
                            }
                        }
                        ToastUtil.send("备份成功")
                    } catch (e: Throwable) {
                        LogUtil.e(e, "save log failed")
                    }
                }
            }
        }
    var showRestoreRestartDialog by remember { mutableStateOf(false) }
    if (showRestoreRestartDialog) {
        ConfirmDialog(
            title = "恢复完成",
            text = "恢复完成，配置需要重启应用才能生效，是否现在重启应用？",
            onConfirmText = "现在重启",
            onConfirm = {
                exitProcess(0)
            },
            onDismissText = "稍后重启",
            onDismiss = {
                showRestoreRestartDialog = false
            }
        )
    }
    LaunchedEffect(fileUri) {
        launch(IO) {
            fileUri?.let {
                val backupTmpFile = File(Config.mmkvDir, "tmp.zip")
                FileUtil.saveFile(it, backupTmpFile)
                FileUtil.restoreBackupConfig(backupTmpFile, Config.mmkvDir)
                backupTmpFile.delete()
                fileUri = null
                showRestoreRestartDialog = true
            }
        }
    }
    ActivityView(title = "其它") {
        LazyColumn(
            modifier = Modifier
                .padding(top = 13.dp)
                .padding(horizontal = 13.dp),
            contentPadding = WindowInsets.Companion.navigationBars.asPaddingValues(),
            // 绘制间隔
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
//            item {
//                val checked = remember { mutableStateOf(ConfUnit.showTaskToast) }
//                LineSwitch(
//                    title = "签到提示",
//                    desc = "执行完签到任务是否提示",
//                    checked = checked,
//                    onChange = {
//                        ConfUnit.showTaskToast = it
//                    },
//                    modifier = Modifier.padding(vertical = 8.dp),
//                )
//            }
//            item {
//                val checked = remember { mutableStateOf(ConfUnit.usedThreadPool) }
//                LineSwitch(
//                    title = "多线程执行",
//                    desc = "是否启用线程池执行任务，加快任务执行速度",
//                    checked = checked,
//                    onChange = {
//                        ConfUnit.usedThreadPool = it
//                    },
//                    modifier = Modifier.padding(vertical = 8.dp),
//                )
//            }
//            item {
//                val checked = remember { mutableStateOf(ConfUnit.enableTaskNotification) }
//                LineSwitch(
//                    title = "启用签到通知",
//                    desc = "是否启用签到通知，任务执行时通知提醒，结束后自动销毁",
//                    checked = checked,
//                    onChange = {
//                        ConfUnit.enableTaskNotification = it
//                    },
//                    modifier = Modifier.padding(vertical = 8.dp),
//                )
//            }
//            item {
//                val checked = remember { mutableStateOf(ConfUnit.enableTaskNotification) }
//                LineSwitch(
//                    title = "任务异常通知",
//                    desc = "当任务执行异常时提示用户，一次性通知",
//                    checked = checked,
//                    onChange = {
//                        ConfUnit.enableTaskExceptionNotification = it
//                    },
//                    modifier = Modifier.padding(vertical = 8.dp),
//                )
//            }
//            item {
//                val checked = remember { mutableStateOf(ConfUnit.logToXposed) }
//                LineSwitch(
//                    title = "日志输出至xposed",
//                    desc = "是否将日志信息输出至框架日志中(默认方式为输出至logcat)",
//                    checked = checked,
//                    onChange = {
//                        ConfUnit.logToXposed = it
//                    },
//                    modifier = Modifier.padding(vertical = 8.dp),
//                )
//            }
//            item {
//                val checked = remember { mutableStateOf(ConfUnit.enableDebugLog) }
//                LineSwitch(
//                    title = "是否输出调试日志",
//                    desc = "仅供调试使用",
//                    checked = checked,
//                    onChange = {
//                        ConfUnit.enableDebugLog = it
//                    },
//                    modifier = Modifier.padding(vertical = 8.dp),
//                )
//            }
//            item {
//                LineButton(
//                    title = "日志导出",
//                    desc = "保存日志文件打包导出至内部存储",
//                    onClick = {
//                        MainScope().launch(IO) {
//                            ToastUtil.send("请选择保存位置")
//                            logSaveLauncher.launch("XAutoDaily_${LocalDateTime.now()}.zip")
//                        }
//                    },
//                    modifier = Modifier.padding(vertical = 8.dp),
//                )
//            }
//            item {
//                LineButton(
//                    title = "配置备份",
//                    desc = "将配置文件打包导出至内部存储",
//                    onClick = {
//                        MainScope().launch(IO) {
//                            ToastUtil.send("请选择保存位置")
//                            configBackupLauncher.launch("XAutoDaily_config_${LocalDateTime.now()}.zip")
//
//                        }
//                    },
//                    modifier = Modifier.padding(vertical = 8.dp),
//                )
//            }
//            item {
//                LineButton(
//                    title = "配置恢复",
//                    desc = "选择待恢复配置文件",
//                    onClick = {
//                        restoreLauncher.launch("application/zip")
//                    },
//                    modifier = Modifier.padding(vertical = 8.dp),
//                )
//            }
        }
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
fun PreviewSettingLayout() {
    OtherLayout(rememberNavController())
}