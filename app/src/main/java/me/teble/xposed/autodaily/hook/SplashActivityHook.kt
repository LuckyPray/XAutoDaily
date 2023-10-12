package me.teble.xposed.autodaily.hook

import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.teble.xposed.autodaily.activity.common.MainActivity
import me.teble.xposed.autodaily.activity.module.ModuleActivity
import me.teble.xposed.autodaily.config.GITHUB_RELEASE_URL
import me.teble.xposed.autodaily.config.PACKAGE_NAME_SELF
import me.teble.xposed.autodaily.config.PAN_URL
import me.teble.xposed.autodaily.config.SplashActivity
import me.teble.xposed.autodaily.hook.annotation.MethodHook
import me.teble.xposed.autodaily.hook.base.BaseHook
import me.teble.xposed.autodaily.hook.base.ProcUtil
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.task.util.ConfigUtil.loadSaveConf
import me.teble.xposed.autodaily.task.util.formatDate
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.ui.TaskErrorInfo
import me.teble.xposed.autodaily.ui.errInfo
import me.teble.xposed.autodaily.ui.retryCount
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.TaskExecutor.AUTO_EXEC
import me.teble.xposed.autodaily.utils.TaskExecutor.handler
import me.teble.xposed.autodaily.utils.TimeUtil
import java.io.File
import java.io.RandomAccessFile
import java.nio.channels.FileLock

class SplashActivityHook : BaseHook() {

    override val isCompatible: Boolean
        get() = ProcUtil.isMain

    override val enabled: Boolean
        get() = true

    private val scope = CoroutineScope(Dispatchers.Default)

    @MethodHook("SplashActivity Hook")
    private fun splashActivityHook() {
        findMethod(SplashActivity) { name == "doOnCreate" }.hookAfter {
            val context = it.thisObject as Activity
            scope.launch {
                withContext(Dispatchers.IO) {
                    val daemonDir = context.getExternalFilesDir("xa_daemon")
                    val file = File(daemonDir, ".init_service")
                    if (file.exists()) {
                        runCatching {
                            val accessFile = RandomAccessFile(file, "rw")
                            val lock = accessFile.channel.tryLock()
                            lock?.let {
                                LogUtil.d("获取文件锁成功，守护进程未在运行？")
                                context.openJumpModuleDialog(lock, file)
                            }
                        }.onFailure { LogUtil.e(it, "try lock fail: ") }
                    }
                    ConfUnit.metaInfoCache ?: ConfigUtil.checkUpdate(false)
                    autoResetTask(true)
                }
                handler.sendEmptyMessageDelayed(AUTO_EXEC, 10_000)
            }
        }

        findMethod(SplashActivity) { name == "doOnStart" }.hookAfter {
            val context = it.thisObject as Activity
            scope.launch {
                withContext(Dispatchers.IO) {
                    if (ConfUnit.metaInfoCache == null
                        || System.currentTimeMillis() - ConfUnit.lastFetchTime > 1 * 60 * 60_000L) {
                        ConfigUtil.checkUpdate(false)
                    }
                    autoResetTask(false)
                }
                if (ConfUnit.needUpdate) {
                    context.openAppUpdateDialog()
                } else if (ConfUnit.needShowUpdateLog) {
                    context.openConfigUpdateLog()
                }
            }
        }
    }
}

private lateinit var appUpdateDialog: AlertDialog

private var lastAutoResetTime = 0L

@Synchronized
private fun autoResetTask(isOnCreate: Boolean) {
    if (System.currentTimeMillis() - lastAutoResetTime < 30 * 60_000L) {
        return
    }
    lastAutoResetTime = System.currentTimeMillis()
    val conf = loadSaveConf()
    val currDateStr = TimeUtil.getCNDate().formatDate()
    conf.taskGroups.forEach { group ->
        group.tasks.forEach { task ->
            val errInfo = task.errInfo
            if (errInfo.dateStr.isNotEmpty() && errInfo.dateStr != currDateStr) {
                if (task.retryCount != 0) {
                    task.retryCount = 0
                    task.errInfo = TaskErrorInfo()
                }
            } else if (errInfo.count >= 3) {
                if ((!isOnCreate && task.retryCount < 2) ||
                    (isOnCreate && task.retryCount < 3)) {
                    task.errInfo = TaskErrorInfo()
                    task.retryCount++
                    LogUtil.d("重置task: ${task.id} retryCount: ${task.retryCount}")
                }
            }
        }
    }
}

suspend fun Activity.openAppUpdateDialog() {
    withContext(Dispatchers.IO) {
        var builder: AlertDialog.Builder? = null
        val isInitialized = ::appUpdateDialog.isInitialized
        if (!isInitialized) {
            builder = AlertDialog.Builder(this@openAppUpdateDialog, 5).apply {
                setTitle("检测到新版本 ${ConfUnit.metaInfoCache?.app?.versionName ?: ""}")
                val updateLog = ConfUnit.metaInfoCache?.app?.updateLog ?: ""
                val updateLogLines = updateLog.lines().toTypedArray()
                setItems(updateLogLines, null)
                setNeutralButton("本次更新不再提示") { _, _ ->
                    ConfUnit.skipUpdateVersion = "${ConfUnit.metaInfoCache?.app?.versionCode}"
                }
                setNegativeButton("蓝奏云") { _, _ ->
                    context.startActivity(Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = Uri.parse(PAN_URL)
                    })
                }
                setPositiveButton("github") { _, _ ->
                    context.startActivity(Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = Uri.parse(GITHUB_RELEASE_URL)
                    })
                }
            }
        }
        withContext(Dispatchers.Main) {
            if (!isInitialized) {
                appUpdateDialog = builder!!.create()
            }
            if (!appUpdateDialog.isShowing && !isFinishing
                && ConfUnit.skipUpdateVersion != "${ConfUnit.metaInfoCache?.app?.versionCode}") {
                appUpdateDialog.show()
            }
        }
    }
}

private lateinit var configUpdateDialog: AlertDialog
private var configVer = 0

suspend fun Activity.openConfigUpdateLog() {
    withContext(Dispatchers.IO) {
        var builder: AlertDialog.Builder? = null
        val conf = loadSaveConf()
        if (conf.version != configVer) {
            configVer = conf.version
            builder = AlertDialog.Builder(this@openConfigUpdateLog, 5).apply {
                setTitle("配置更新日志")
                val updateLog = buildList {
                    conf.updateLogs.forEach {
                        add("v${it.version}:\n${it.desc}")
                    }
                }.reversed().toTypedArray()
                setCancelable(false)
                setItems(updateLog, null)
                setNegativeButton("确定") { _, _ ->
                    ConfUnit.needShowUpdateLog = false
                }
                setPositiveButton("前往配置") { _, _ ->
                    val intent = Intent(context, ModuleActivity::class.java)
                    ConfUnit.needShowUpdateLog = false
                    context.startActivity(intent)
                }
            }
        }
        withContext(Dispatchers.Main) {
            builder?.let {
                configUpdateDialog = it.create()
            }
            if (!configUpdateDialog.isShowing && !isFinishing) {
                configUpdateDialog.show()
            }
        }
    }
}


private lateinit var jumpModuleDialog: AlertDialog
suspend fun Activity.openJumpModuleDialog(lock: FileLock, file: File) {
    withContext(Dispatchers.IO) {
        var builder: AlertDialog.Builder? = null
        val isInitialized = ::jumpModuleDialog.isInitialized
        if (!isInitialized) {
            builder = AlertDialog.Builder(this@openJumpModuleDialog, 5).apply {
                setTitle("守护进程提示")
                setMessage("检测到守护进程异常退出，是否前往模块启动？")
                setNegativeButton("我不需要") { _, _ ->
                    runCatching {
                        lock.release()
                        file.delete()
                    }
                }
                setPositiveButton("前往模块") { _, _ ->
                    val intent = Intent().apply {
                        component = ComponentName(PACKAGE_NAME_SELF, MainActivity::class.java.name)
                    }
                    context.startActivity(intent)
                }
            }
        }
        withContext(Dispatchers.Main) {
            if (!isInitialized) {
                configUpdateDialog = builder!!.create()
            }
            if (!configUpdateDialog.isShowing && !isFinishing) {
                configUpdateDialog.show()
            }
        }
    }
}