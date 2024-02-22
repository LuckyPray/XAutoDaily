package me.teble.xposed.autodaily.activity.common

import android.os.Build
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import me.teble.xposed.autodaily.application.xaApp
import me.teble.xposed.autodaily.config.DataMigrationService
import me.teble.xposed.autodaily.config.PACKAGE_NAME_QQ
import me.teble.xposed.autodaily.config.PACKAGE_NAME_TIM
import me.teble.xposed.autodaily.shizuku.ShizukuApi
import me.teble.xposed.autodaily.shizuku.ShizukuConf
import me.teble.xposed.autodaily.ui.composable.SmallTitle
import me.teble.xposed.autodaily.ui.composable.SwitchInfoDivideItem
import me.teble.xposed.autodaily.ui.composable.SwitchInfoItem
import me.teble.xposed.autodaily.ui.composable.SwitchTextItem
import me.teble.xposed.autodaily.ui.composable.TextItem
import me.teble.xposed.autodaily.ui.composable.TopBar
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.theme.DefaultAlpha
import me.teble.xposed.autodaily.ui.theme.DisabledAlpha
import me.teble.xposed.autodaily.utils.TaskExecutor
import me.teble.xposed.autodaily.utils.toJsonString
import java.io.File

@Composable
fun SettingScene(
    navController: NavController
) {
    TopBar(text = "设置", backClick = {
        navController.popBackStack()
    })
    SettingLayout()
}


@Composable
private fun SettingLayout(viewmodel: SettingViewModel = viewModel()) {
    var shizukuEnable by remember { mutableStateOf(viewmodel.getShizukuEnable()) }
    LaunchedEffect(ShizukuApi.isPermissionGranted, ShizukuApi.isPermissionGranted) {
        shizukuEnable = viewmodel.getShizukuEnable()
    }

    val itemAlpha: Float by animateFloatAsState(
        targetValue = if (shizukuEnable) DefaultAlpha else DisabledAlpha,
        animationSpec = spring(), label = "switch item"
    )
    SmallTitle(
        title = "通用设置",
        modifier = Modifier
            .padding(bottom = 8.dp, start = 16.dp, top = 16.dp)
    )


    CommonLayout(shizukuEnable = shizukuEnable, itemAlpha = itemAlpha)
    ShizukuLayout(shizukuEnable = shizukuEnable, itemAlpha = itemAlpha)


}

@Composable
fun CommonLayout(
    itemAlpha: Float,
    shizukuEnable: Boolean,
    viewmodel: SettingViewModel = viewModel()
) {
    Column(
        Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .background(Color(0xFFFFFFFF).copy(alpha = itemAlpha))
    ) {
        val hiddenAppIcon by viewmodel.hiddenAppIcon.collectAsStateWithLifecycle(false)
        SwitchTextItem(
            text = if (hiddenAppIcon) "显示应用图标" else "隐藏应用图标",
            clickEnabled = shizukuEnable,
            enable = hiddenAppIcon,
            onClick = {

            })
        TextItem(text = "主题颜色", clickEnabled = shizukuEnable, onClick = {
            viewmodel.updateHiddenAppIcon(!hiddenAppIcon)
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val untrustedTouchEvents by viewmodel.untrustedTouchEvents.collectAsStateWithLifecycle(
                false
            )
            SwitchInfoItem(
                enable = untrustedTouchEvents,
                text = "取消安卓 12 不受信触摸",
                infoText = "安卓 12 后启用对 Toast 弹窗等事件触摸不可穿透，勾选此项可关闭",
                clickEnabled = shizukuEnable,
                onClick = {
                    viewmodel.updateUntrustedTouchEvents(it)
                }
            )
        }
    }

}

@Composable
fun ShizukuLayout(
    itemAlpha: Float,
    shizukuEnable: Boolean,
    viewmodel: SettingViewModel = viewModel()
) {
    SmallTitle(
        title = "Shizuku 保活",
        modifier = Modifier
            .padding(bottom = 8.dp, start = 16.dp, top = 16.dp)
    )
    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .background(Color(0xFFFFFFFF).copy(alpha = itemAlpha))

    ) {
        val keepAlive by viewmodel.keepAlive.collectAsStateWithLifecycle(false)

        SwitchInfoItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            enable = keepAlive,
            text = "启用 Shizuku 保活机制",
            infoText = "通过 Shizuku 运行一个 Service，当监测到 QQ/TIM 被杀死后重新拉起进程",
            clickEnabled = shizukuEnable,
            onClick = {
                viewmodel.updateKeepAliveChecked(it)
            }
        )
        if (xaApp.qPackageState.containsKey(PACKAGE_NAME_QQ)) {

            val qqKeepAlive by viewmodel.qqKeepAlive.collectAsStateWithLifecycle(false)

            KeepTimeItem(
                title = "启用 QQ 保活",
                info = "单击此处尝试后台唤醒 QQ，如果模块 hook 生效将会显示一个气泡",
                keepAlive = keepAlive && shizukuEnable,
                hostPackage = PACKAGE_NAME_QQ,
                hostKeepAlive = qqKeepAlive,
                onClick = {
                    ShizukuApi.startService(
                        PACKAGE_NAME_QQ, DataMigrationService,
                        arrayOf(
                            "-e", TaskExecutor.CORE_SERVICE_FLAG, "$",
                            "-e", TaskExecutor.CORE_SERVICE_TOAST_FLAG, "$"
                        )
                    )

                },
                onChange = {
                    viewmodel.updateQQKeepAlive(it)
                }
            )
        }

        if (xaApp.qPackageState.containsKey(PACKAGE_NAME_TIM)) {
            val timKeepAlive by viewmodel.timKeepAlive.collectAsStateWithLifecycle(false)

            KeepTimeItem(
                title = "启用 TIM 保活",
                info = "单击此处尝试后台唤醒 TIM，如果模块 hook 生效将会显示一个气泡",
                keepAlive = keepAlive && shizukuEnable,
                hostPackage = PACKAGE_NAME_TIM,
                hostKeepAlive = timKeepAlive,
                onClick = {
                    ShizukuApi.startService(
                        PACKAGE_NAME_TIM, DataMigrationService,
                        arrayOf(
                            "-e", TaskExecutor.CORE_SERVICE_FLAG, "$",
                            "-e", TaskExecutor.CORE_SERVICE_TOAST_FLAG, "$"
                        )
                    )
                },
                onChange = {
                    viewmodel.updateTimKeepAlive(it)
                }
            )
        }

    }
}

/**
 * @param hostPackage 被保活的包名
 *
 */
@Composable
private fun KeepTimeItem(
    title: String,
    info: String,
    keepAlive: Boolean,
    hostKeepAlive: Boolean,
    hostPackage: String,
    onClick: () -> Unit,
    onChange: (Boolean) -> Unit,
) {
    LaunchedEffect(hostKeepAlive, keepAlive) {
        val confDir = File(xaApp.getExternalFilesDir(null), "conf")
        if (confDir.isFile) {
            confDir.delete()
        }
        if (!confDir.exists()) {
            confDir.mkdirs()
        }
        val confFile = File(confDir, "conf.json")
        val conf = ShizukuConf(keepAlive,
            mutableMapOf<String, Boolean>().apply {
                put(hostPackage, hostKeepAlive)
            })
        val confString = conf.toJsonString()
        Log.d("XALog", confString)
        confFile.writeText(confString)
    }
    SwitchInfoDivideItem(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SmootherShape(12.dp)),
        enable = hostKeepAlive,
        text = title,
        infoText = info,
        clickEnabled = keepAlive,
        onClick = onClick,
        onChange = onChange
    )
}
