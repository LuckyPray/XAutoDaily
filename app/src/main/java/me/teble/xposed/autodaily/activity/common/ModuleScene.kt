package me.teble.xposed.autodaily.activity.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import me.teble.xposed.autodaily.application.xaApp
import me.teble.xposed.autodaily.config.PACKAGE_NAME_QQ
import me.teble.xposed.autodaily.config.PACKAGE_NAME_TIM
import me.teble.xposed.autodaily.hook.enums.QQTypeEnum
import me.teble.xposed.autodaily.shizuku.ShizukuApi
import me.teble.xposed.autodaily.ui.composable.ImageItem
import me.teble.xposed.autodaily.ui.composable.SmallTitle
import me.teble.xposed.autodaily.ui.composable.TextItem
import me.teble.xposed.autodaily.ui.composable.XAutoDailyTopBar
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.Activated
import me.teble.xposed.autodaily.ui.icon.icons.Error
import me.teble.xposed.autodaily.ui.icon.icons.QQ
import me.teble.xposed.autodaily.ui.icon.icons.TIM
import me.teble.xposed.autodaily.ui.icon.icons.Warn


@Composable
fun ModuleScene(navController: NavController) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .padding(horizontal = 16.dp)

    ) {
        ModuleTopBar()
        Column(
            Modifier
                .clip(SmootherShape(12.dp))
                .verticalScroll(rememberScrollState())
                .weight(weight = 1f, fill = false)
                .padding(bottom = 24.dp)
                .navigationBarsPadding()
        ) {
            ShizukuCard()

            EntryLayout()
            TextItem(
                text = "更多设置",
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth()
                    .clip(SmootherShape(12.dp))
                    .background(Color(0xFFFFFFFF)),
                clickEnabled = true
            ) {
                navController.navigate(NavigationItem.Module, NavigationItem.Setting)
            }

        }

    }

}

@Composable
private fun ModuleTopBar() {
    XAutoDailyTopBar(
        modifier = Modifier
            .statusBarsPadding()
            .padding(vertical = 20.dp)
            .padding(start = 16.dp),
    )
}

@Composable
private fun ShizukuCard(
    viewmodel: ModuleViewModel = viewModel()
) {

    val shizukuDaemonRunning by viewmodel.shizukuDaemonRunning.collectAsStateWithLifecycle()
    val shizukuErrInfo by viewmodel.shizukuErrInfo.collectAsStateWithLifecycle()
    var shizukuState by remember {
        mutableStateOf(
            viewmodel.getShizukuState()
        )
    }

    LaunchedEffect(
        ShizukuApi.binderAvailable,
        ShizukuApi.isPermissionGranted,
        shizukuDaemonRunning,
        shizukuErrInfo
    ) {
        shizukuState = viewmodel.getShizukuState()
    }

    val backgroundColor by animateColorAsState(

        targetValue = when (shizukuState) {
            is ShisukuState.Activated -> Color(0xFF25CD8E)
            is ShisukuState.Warn -> Color(0xFFFFBC04)
            ShisukuState.Error -> Color(0xFFFA5F5C)
        }, label = "shizuku background"
    )

    val titleText by remember {
        derivedStateOf {
            when (shizukuState) {
                is ShisukuState.Warn,
                is ShisukuState.Activated -> "Shizuku 服务正在运行"

                ShisukuState.Error -> "Shizuku 服务未在运行"
            }
        }
    }

    val icon by remember {
        derivedStateOf {
            when (shizukuState) {
                is ShisukuState.Activated -> Icons.Activated
                is ShisukuState.Warn -> Icons.Warn
                ShisukuState.Error -> Icons.Error
            }
        }
    }

    val versionText by remember {
        derivedStateOf {
            when (shizukuState) {
                is ShisukuState.Activated -> "${(shizukuState as ShisukuState.Activated).version}"
                is ShisukuState.Warn -> "${(shizukuState as ShisukuState.Warn).version}"
                ShisukuState.Error -> "Unknown"
            }
        }
    }

    val infoText by remember {
        derivedStateOf {
            when (shizukuState) {
                is ShisukuState.Activated -> "守护进程正在运行，点击停止运行"
                is ShisukuState.Warn -> (shizukuState as ShisukuState.Warn).info
                ShisukuState.Error -> "部分功能无法运行"
            }
        }
    }

    Row(
        Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .background(backgroundColor)
            .clickable(role = Role.Button) {
                viewmodel.shizukuClickable()
            }
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Image(
            imageVector = icon,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(40.dp),
            contentDescription = "shizuku-icon"
        )
        Column(Modifier.padding(start = 10.dp)) {

            Text(
                text = titleText, style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFFFFF),
                )
            )
            Text(
                text = "Shizuku Api Version: $versionText",
                Modifier.padding(top = 4.dp),
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFFFFFFF),
                )
            )
            Text(
                text = infoText, Modifier.padding(top = 4.dp), style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFFFFFFF),
                )
            )
        }
    }
}

@Composable
private fun EntryLayout(viewmodel: ModuleViewModel = viewModel()) {
    if (xaApp.qPackageState.containsKey(PACKAGE_NAME_TIM)
        || xaApp.qPackageState.containsKey(PACKAGE_NAME_QQ)
    ) {
        SmallTitle(
            title = "模块入口",
            modifier = Modifier
                .padding(bottom = 8.dp, start = 16.dp, top = 16.dp)
        )

        Column(
            Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp))
                .background(Color(0xFFFFFFFF))
        ) {
            val context = LocalContext.current
            if (xaApp.qPackageState.containsKey(PACKAGE_NAME_QQ)) {
                ImageItem(
                    icon = Icons.QQ,
                    contentDescription = "QQ Logo",
                    title = "QQ",
                    info = "QQ 侧滑 > 设置 > XAutoDaily",
                    onClick = {
                        viewmodel.openHostSetting(context, QQTypeEnum.QQ)
                    })
            }
            if (xaApp.qPackageState.containsKey(PACKAGE_NAME_TIM)) {
                ImageItem(
                    icon = Icons.TIM,
                    contentDescription = "Tim Logo",
                    title = "Tim",
                    info = "TIM 侧滑 > 设置 > XAutoDaily",
                    onClick = {
                        viewmodel.openHostSetting(context, QQTypeEnum.TIM)
                    }
                )
            }
        }
    }
}
