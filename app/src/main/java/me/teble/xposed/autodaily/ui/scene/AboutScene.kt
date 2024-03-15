package me.teble.xposed.autodaily.ui.scene

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.teble.xposed.autodaily.ui.composable.Icon
import me.teble.xposed.autodaily.ui.composable.RoundedSnackbarHost
import me.teble.xposed.autodaily.ui.composable.Text
import me.teble.xposed.autodaily.ui.composable.TextInfoItem
import me.teble.xposed.autodaily.ui.composable.TextItem
import me.teble.xposed.autodaily.ui.composable.TopBar
import me.teble.xposed.autodaily.ui.composable.XaScaffold
import me.teble.xposed.autodaily.ui.dialog.UpdateDialog
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.ChevronRight
import me.teble.xposed.autodaily.ui.icon.icons.XAutoDaily
import me.teble.xposed.autodaily.ui.icon.icons.XAutoDailyRound
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScene(
    backClick: () -> Unit,
    hasBackProvider: () -> Boolean,
    onNavigateToLicense: () -> Unit,
    onNavigateToDeveloper: () -> Unit,
    viewmodel: AboutViewModel = viewModel()
) {
    Box {
        val context = LocalContext.current


        XaScaffold(
            topBar = {
                TopBar(text = "关于", backClick = backClick, hasBackProvider = hasBackProvider)
            },
            snackbarHost = {
                RoundedSnackbarHost(hostState = viewmodel.snackbarHostState)
            },
            containerColor = colors.colorBgLayout
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .clip(SmootherShape(12.dp))
                    .verticalScroll(rememberScrollState())
                    .defaultNavigationBarPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BuildConfigLayout(
                    moduleVersionName = viewmodel::moduleVersionName,
                    moduleVersionCode = viewmodel::moduleVersionCode
                )
                UpdateLayout(
                    qqVersionName = viewmodel::qqVersionName,
                    qqVersionCode = viewmodel::qqVersionCode,
                    configVersion = viewmodel::configVersion,
                    hasUpdate = viewmodel::hasUpdate,
                    updateApp = viewmodel::updateApp
                )
                LicenseLayout(onNavigateToLicense = onNavigateToLicense)
                OthterLayout(
                    onNavigateToDeveloper = onNavigateToDeveloper,
                    openGithub = viewmodel.openGithub(context),
                    openTelegram = viewmodel.openTelegram(context),
                    openAliPay = viewmodel.openAliPay(context)
                )
            }
        }

        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        LaunchedEffect(viewmodel.showUpdateDialog) {
            if (viewmodel.showUpdateDialog) sheetState.expand()
            else sheetState.hide()
        }

        UpdateDialog(
            state = sheetState,
            enable = viewmodel::showUpdateDialog,
            text = "新版本",
            info = viewmodel::updateDialogText,
            onDismiss = viewmodel::dismissUpdateDialog,
            onConfirm = viewmodel.updateConfirm(context)
        )
    }

}

@Composable
private fun BuildConfigLayout(
    moduleVersionName: () -> String,
    moduleVersionCode: () -> Int,
) {

    val colors = colors
    Image(
        Icons.XAutoDailyRound,
        contentDescription = "XAutoDaily Round Icon",
        Modifier
            .padding(top = 36.dp)
            .size(80.dp)
    )

    Icon(
        tint = { colors.colorText },
        imageVector = Icons.XAutoDaily,
        contentDescription = "text logo",
        modifier = Modifier.padding(top = 16.dp)
    )

    Text(
        text = { "v${moduleVersionName()} (${moduleVersionCode()}) " },
        modifier = Modifier.padding(top = 8.dp),
        style = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
        ),
        color = { colors.colorAboutText }
    )
}

@Composable
private fun UpdateLayout(
    qqVersionName: () -> String,
    qqVersionCode: () -> Long,
    configVersion: () -> Int,
    hasUpdate: () -> Boolean,
    updateApp: () -> Unit

) {


    val colors = colors
    Row(
        Modifier
            .padding(top = 24.dp)
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .drawBehind {
                drawRect(colors.colorBgContainer)
            }
            .clickable(role = Role.Button, onClick = updateApp)
            .padding(vertical = 20.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "检查更新",
                maxLines = 1,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = { colors.colorText }
            )

            Text(
                text = "当前宿主版本：${qqVersionName()} (${qqVersionCode()})\n"
                        + "当前配置版本：${configVersion()}",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.colorTextSecondary
                ),
                color = { colors.colorText }
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        if (hasUpdate()) {
            Text(
                text = "有新版本",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = { colors.themeColor }
            )
        }

        Icon(
            imageVector = Icons.ChevronRight,
            contentDescription = "",
            modifier = Modifier.size(24.dp),
            tint = { colors.colorIcon }
        )
    }
}

@Composable
private fun LicenseLayout(onNavigateToLicense: () -> Unit) {
    val colors = colors
    TextItem(
        text = "开放源代码许可",
        modifier = Modifier
            .padding(top = 24.dp)
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .drawBehind {
                drawRect(colors.colorBgContainer)
            },
        onClick = onNavigateToLicense
    )
}

@Composable
private fun OthterLayout(
    onNavigateToDeveloper: () -> Unit,
    openGithub: () -> Unit,
    openTelegram: () -> Unit,
    openAliPay: () -> Unit,
) {

    val colors = colors
    Column(
        modifier = Modifier
            .padding(top = 24.dp)
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .drawBehind {
                drawRect(colors.colorBgContainer)
            },
    ) {
        TextItem(
            text = "开发者",
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            onClick = onNavigateToDeveloper
        )

        TextItem(
            text = "Github",
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            onClick = openGithub
        )

        TextItem(
            text = "Telegram 频道",
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            onClick = openTelegram
        )

        TextInfoItem(
            text = "请作者吃辣条",
            infoText = "本模块完全免费开源，一切开发旨在学习，请勿用于非法用途。喜欢本模块的可以捐赠支持我，谢谢~~",
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            clickEnabled = { true },
            onClick = openAliPay
        )
    }

}