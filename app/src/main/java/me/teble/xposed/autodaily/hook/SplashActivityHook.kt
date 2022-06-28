package me.teble.xposed.autodaily.hook

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.teble.xposed.autodaily.activity.module.ModuleActivity
import me.teble.xposed.autodaily.config.GITHUB_RELEASE_URL
import me.teble.xposed.autodaily.config.PAN_URL
import me.teble.xposed.autodaily.config.SplashActivity
import me.teble.xposed.autodaily.hook.CoreServiceHook.Companion.AUTO_EXEC
import me.teble.xposed.autodaily.hook.CoreServiceHook.Companion.handler
import me.teble.xposed.autodaily.hook.annotation.MethodHook
import me.teble.xposed.autodaily.hook.base.BaseHook
import me.teble.xposed.autodaily.hook.base.ProcUtil
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.task.util.ConfigUtil.loadSaveConf
import me.teble.xposed.autodaily.task.util.formatDate
import me.teble.xposed.autodaily.ui.ConfUnit
import java.util.*

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
                    loadSaveConf()
                    if (ConfigUtil.checkUpdate(false)) {
                        ConfUnit.needUpdate = true
                    }
                }
                if (ConfUnit.needUpdate) {
                    context.openAppUpdateDialog()
                } else if (ConfUnit.needShowUpdateLog) {
                    context.openConfigUpdateLog()
                    ConfUnit.needShowUpdateLog = false
                }
                handler.sendEmptyMessageDelayed(AUTO_EXEC, 10_000)
            }
        }

        findMethod(SplashActivity) { name == "doOnStart" }.hookAfter {
            val context = it.thisObject as Activity
            scope.launch {
                if (ConfUnit.needUpdate) {
                    context.openAppUpdateDialog()
                } else if (ConfUnit.needShowUpdateLog) {
                    context.openConfigUpdateLog()
                    ConfUnit.needShowUpdateLog = false
                }
            }
        }
    }
}

private lateinit var appUpdateDialog: AlertDialog

suspend fun Activity.openAppUpdateDialog() {
    withContext(Dispatchers.IO) {
        var builder: AlertDialog.Builder? = null
        val isInitialized = ::appUpdateDialog.isInitialized
        if (!isInitialized) {
            builder = AlertDialog.Builder(this@openAppUpdateDialog, 5).apply {
                setTitle("检测到新版本")
                val updateList = ConfUnit.versionInfoCache?.updateLog ?: emptyList()
                val updateLog = buildList {
                    updateList.forEach {
                        if (it.isNotEmpty()) {
                            add(it)
                        }
                    }
                }.toTypedArray()
                setItems(updateLog, null)
                setNeutralButton("今日不再提醒") { _, _ ->
                    ConfUnit.blockUpdateOneDay = Date().formatDate()
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
            if (!appUpdateDialog.isShowing && !isFinishing) {
                appUpdateDialog.show()
            }
        }
    }
}

private lateinit var configUpdateDialog: AlertDialog

suspend fun Activity.openConfigUpdateLog() {
    withContext(Dispatchers.IO) {
        var builder: AlertDialog.Builder? = null
        val isInitialized = ::configUpdateDialog.isInitialized
        if (!isInitialized) {
            builder = AlertDialog.Builder(this@openConfigUpdateLog, 5).apply {
                setTitle("配置更新日志")
                val updateLog = buildList {
                    loadSaveConf().updateLogs.forEach {
                        add("v${it.version}:\n${it.desc}")
                    }
                }.reversed().toTypedArray()
                setItems(updateLog, null)
                setNegativeButton("确定") { _, _ -> }
                setPositiveButton("前往配置") { _, _ ->
                    val intent = Intent(context, ModuleActivity::class.java)
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