package me.teble.xposed.autodaily.activity.common

import android.os.Build
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.teble.xposed.autodaily.application.xaApp
import me.teble.xposed.autodaily.config.PACKAGE_NAME_QQ
import me.teble.xposed.autodaily.config.PACKAGE_NAME_TIM
import me.teble.xposed.autodaily.shizuku.DaemonConnectionManager
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScene(
    themeViewModel: ThemeViewModel,
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
                text = "允许悬浮窗下方点击",
                infoText = "解决部分悬浮窗遮挡后无法点击的问题。需要 Shizuku。",
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
        title = "Shizuku 任务守护",
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
            text = "启用任务守护",
            infoText = "任务执行时保持应用运行，减少后台休眠导致的中断。需保持 Shizuku 运行。",
            clickEnabled = shizukuEnable,
            onClick = updateKeepAliveChecked
        )
        if (xaApp.qPackageState.containsKey(PACKAGE_NAME_QQ)) {


            KeepTimeItem(
                title = "QQ 任务守护",
                info = "点击尝试唤醒 QQ，不执行签到。",
                keepAlive = keepAlive,
                shizukuEnable = shizukuEnable,
                hostKeepAlive = qqKeepAlive,
                onClick = {
                    DaemonConnectionManager.testWake(PACKAGE_NAME_QQ)

                },
                onChange = updateQQKeepAlive
            )
        }

        if (xaApp.qPackageState.containsKey(PACKAGE_NAME_TIM)) {

            KeepTimeItem(
                title = "TIM 任务守护",
                info = "点击尝试唤醒 TIM，不执行签到。",
                keepAlive = keepAlive,
                shizukuEnable = shizukuEnable,
                hostKeepAlive = timKeepAlive,
                onClick = {
                    DaemonConnectionManager.testWake(PACKAGE_NAME_TIM)

                },
                onChange = updateTimKeepAlive
            )
        }

    }
}

@Composable
private fun KeepTimeItem(
    title: String,
    info: String,
    keepAlive: () -> Boolean,
    shizukuEnable: () -> Boolean,
    hostKeepAlive: () -> Boolean,
    onClick: () -> Unit,
    onChange: (Boolean) -> Unit,
) {
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
