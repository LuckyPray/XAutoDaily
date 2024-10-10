package me.teble.xposed.autodaily.activity.common

import android.os.Build
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.collections.immutable.persistentMapOf
import me.teble.xposed.autodaily.application.xaApp
import me.teble.xposed.autodaily.config.DataMigrationService
import me.teble.xposed.autodaily.config.PACKAGE_NAME_QQ
import me.teble.xposed.autodaily.config.PACKAGE_NAME_TIM
import me.teble.xposed.autodaily.shizuku.ShizukuApi
import me.teble.xposed.autodaily.shizuku.ShizukuConf
import me.teble.xposed.autodaily.ui.composable.SelectionItem
import me.teble.xposed.autodaily.ui.composable.SmallTitle
import me.teble.xposed.autodaily.ui.composable.SwitchInfoDivideItem
import me.teble.xposed.autodaily.ui.composable.SwitchInfoItem
import me.teble.xposed.autodaily.ui.composable.SwitchTextItem
import me.teble.xposed.autodaily.ui.composable.XaScaffold
import me.teble.xposed.autodaily.ui.dialog.ThemeModelDialog
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.DefaultAlpha
import me.teble.xposed.autodaily.ui.theme.DisabledAlpha
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors
import me.teble.xposed.autodaily.utils.TaskExecutor
import me.teble.xposed.autodaily.utils.toJsonString
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScene(
    themeViewModel: ModuleThemeViewModel,
    hasBackProvider: () -> Boolean,
    onBackClick: () -> Unit,
    viewmodel: SettingViewModel = viewModel()
) {


    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    LaunchedEffect(viewmodel.showThemeDialog) {
        if (viewmodel.showThemeDialog) {
            sheetState.expand()
        } else {
            sheetState.hide()
        }
    }
    LaunchedEffect(sheetState.isVisible) {
        viewmodel.updateThemeDialogState(sheetState.isVisible)
    }
    Box {
        XaScaffold(
            text = "更多设置",
            backClick = onBackClick,
            hasBackProvider = hasBackProvider,
            containerColor = colors.colorBgLayout
        ) {

            SettingLayout(
                shizukuEnable = viewmodel::shizukuState,
                hiddenAppIcon = viewmodel::hiddenAppIcon,
                untrustedTouchEvents = viewmodel::untrustedTouchEvents,
                keepAlive = viewmodel::keepAlive,
                qqKeepAlive = viewmodel::qqKeepAlive,
                timKeepAlive = viewmodel::timKeepAlive,


                updateKeepAliveChecked = viewmodel::updateKeepAliveChecked,
                updateQQKeepAlive = viewmodel::updateQQKeepAlive,
                updateTimKeepAlive = viewmodel::updateTimKeepAlive,
                updateHiddenAppIcon = viewmodel::updateHiddenAppIcon,
                updateUntrustedTouchEvents = viewmodel::updateUntrustedTouchEvents,
                showThemeDialog = viewmodel::showThemeDialog,

                modifier = Modifier
                    .fillMaxSize()
                    .clip(SmootherShape(12.dp))
                    .verticalScroll(rememberScrollState())
                    .defaultNavigationBarPadding()
            )

        }

        ThemeModelDialog(
            sheetState = sheetState,
            enable = viewmodel::showThemeDialog,
            targetTheme = themeViewModel::currentTheme,
            targetBlack = themeViewModel::blackTheme,
            onDismiss = viewmodel::dismissThemeDialog,

            onConfirm = themeViewModel::confirmTheme
        )


    }

}


@Composable
private fun SettingLayout(
    shizukuEnable: () -> Boolean,
    hiddenAppIcon: () -> Boolean,
    untrustedTouchEvents: () -> Boolean,
    keepAlive: () -> Boolean,
    qqKeepAlive: () -> Boolean,
    timKeepAlive: () -> Boolean,


    updateKeepAliveChecked: (Boolean) -> Unit,
    updateQQKeepAlive: (Boolean) -> Unit,
    updateTimKeepAlive: (Boolean) -> Unit,
    updateHiddenAppIcon: (Boolean) -> Unit,
    updateUntrustedTouchEvents: (Boolean) -> Unit,
    showThemeDialog: () -> Unit,
    modifier: Modifier
) {


    Column(
        modifier = modifier
    ) {

        CommonLayout(
            shizukuEnable = shizukuEnable, hiddenAppIcon = hiddenAppIcon,
            untrustedTouchEvents = untrustedTouchEvents,

            updateHiddenAppIcon = updateHiddenAppIcon,
            updateUntrustedTouchEvents = updateUntrustedTouchEvents,
            showThemeDialog = showThemeDialog
        )
        ShizukuLayout(
            shizukuEnable = shizukuEnable,
            keepAlive = keepAlive,
            qqKeepAlive = qqKeepAlive,
            timKeepAlive = timKeepAlive,

            updateKeepAliveChecked = updateKeepAliveChecked,
            updateQQKeepAlive = updateQQKeepAlive,
            updateTimKeepAlive = updateTimKeepAlive
        )
    }

}

@Composable
private fun CommonLayout(
    shizukuEnable: () -> Boolean,
    hiddenAppIcon: () -> Boolean,
    untrustedTouchEvents: () -> Boolean,

    updateHiddenAppIcon: (Boolean) -> Unit,
    updateUntrustedTouchEvents: (Boolean) -> Unit,
    showThemeDialog: () -> Unit
) {
    SmallTitle(
        title = "通用",
        modifier = Modifier
            .padding(bottom = 8.dp, start = 16.dp, top = 16.dp)
    )

    Column(
        Modifier
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .background(colors.colorBgContainer)
    ) {


        val hideText by remember {
            derivedStateOf {
                if (hiddenAppIcon()) "显示应用图标" else "隐藏应用图标"
            }
        }
        SwitchTextItem(
            Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            text = hideText,
            clickEnabled = { true },
            enable = hiddenAppIcon,
            onClick = updateHiddenAppIcon
        )
        SelectionItem(
            Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            text = "主题颜色", clickEnabled = { true },
            onClick = showThemeDialog
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            SwitchInfoItem(
                Modifier
                    .fillMaxWidth()
                    .clip(SmootherShape(12.dp)),
                enable = untrustedTouchEvents,
                text = "取消安卓 12 不受信触摸",
                infoText = "安卓 12 后启用对 Toast 弹窗等事件触摸不可穿透，勾选此项可关闭",
                clickEnabled = shizukuEnable,
                onClick = updateUntrustedTouchEvents
            )
        }
    }

}

@Composable
private fun ShizukuLayout(
    shizukuEnable: () -> Boolean,
    keepAlive: () -> Boolean,
    qqKeepAlive: () -> Boolean,
    timKeepAlive: () -> Boolean,

    updateKeepAliveChecked: (Boolean) -> Unit,
    updateQQKeepAlive: (Boolean) -> Unit,
    updateTimKeepAlive: (Boolean) -> Unit
) {

    val itemAlpha: Float by animateFloatAsState(
        targetValue = if (shizukuEnable()) DefaultAlpha else DisabledAlpha,
        animationSpec = spring(), label = "switch item"
    )
    SmallTitle(
        title = "Shizuku 保活",
        modifier = Modifier
            .padding(bottom = 8.dp, start = 16.dp, top = 24.dp)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .background(colors.colorBgContainer.copy(alpha = itemAlpha))

    ) {

        SwitchInfoItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            enable = keepAlive,
            text = "启用 Shizuku 保活机制",
            infoText = "通过 Shizuku 运行一个 Service，当监测到 QQ/TIM 被杀死后重新拉起进程",
            clickEnabled = shizukuEnable,
            onClick = updateKeepAliveChecked
        )
        if (xaApp.qPackageState.containsKey(PACKAGE_NAME_QQ)) {


            KeepTimeItem(
                title = "启用 QQ 保活",
                info = "单击此处尝试后台唤醒 QQ，如果模块 hook 生效将会显示一个气泡",
                keepAlive = keepAlive,
                shizukuEnable = shizukuEnable,
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
                onChange = updateQQKeepAlive
            )
        }

        if (xaApp.qPackageState.containsKey(PACKAGE_NAME_TIM)) {

            KeepTimeItem(
                title = "启用 TIM 保活",
                info = "单击此处尝试后台唤醒 TIM，如果模块 hook 生效将会显示一个气泡",
                keepAlive = keepAlive,
                shizukuEnable = shizukuEnable,
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
                onChange = updateTimKeepAlive
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
    keepAlive: () -> Boolean,
    shizukuEnable: () -> Boolean,
    hostKeepAlive: () -> Boolean,
    hostPackage: String,
    onClick: () -> Unit,
    onChange: (Boolean) -> Unit,
) {
    LaunchedEffect(hostKeepAlive(), shizukuEnable(), keepAlive()) {
        val confDir = File(xaApp.getExternalFilesDir(null), "conf")
        if (confDir.isFile) {
            confDir.delete()
        }
        if (!confDir.exists()) {
            confDir.mkdirs()
        }
        val confFile = File(confDir, "conf.json")
        val conf = ShizukuConf(
            shizukuEnable() && keepAlive(),
            persistentMapOf<String, Boolean>().apply {
                put(hostPackage, hostKeepAlive())
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
        clickEnabled = { shizukuEnable() && keepAlive() },
        onClick = onClick,
        onLongClick = {  },
        onChange = onChange,
    )
}
