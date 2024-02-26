package me.teble.xposed.autodaily.ui.scene

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import me.teble.xposed.autodaily.ui.NavigationItem
import me.teble.xposed.autodaily.ui.composable.RoundedSnackbar
import me.teble.xposed.autodaily.ui.composable.TextInfoItem
import me.teble.xposed.autodaily.ui.composable.TextItem
import me.teble.xposed.autodaily.ui.composable.TopBar
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.ChevronRight
import me.teble.xposed.autodaily.ui.icon.icons.XAutoDaily
import me.teble.xposed.autodaily.ui.icon.icons.XAutoDailyRound
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.navigate
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors

@Composable
fun AboutScene(navController: NavController, viewmodel: AboutViewModel = viewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }

    // 展示对应 snackbarText
    LaunchedEffect(Unit) {
        viewmodel.snackbarText.collect {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        topBar = {
            TopBar(text = "关于", backClick = {
                navController.popBackStack()
            })
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                RoundedSnackbar(it)
            }
        },
        backgroundColor = Color(0xFFF7F7F7)
    ) { contentPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
                .clip(SmootherShape(12.dp))
                .verticalScroll(rememberScrollState())
                .defaultNavigationBarPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BuildConfigLayout()
            UpdateLayout()
            LicenseLayout(navController)
            OthterLayout(navController)
        }
    }
}

@Composable
private fun BuildConfigLayout(viewmodel: AboutViewModel = viewModel()) {

    val moduleVersionName by viewmodel.moduleVersionName.collectAsStateWithLifecycle()
    val moduleVersionCode by viewmodel.moduleVersionCode.collectAsStateWithLifecycle()

    Image(
        Icons.XAutoDailyRound,
        contentDescription = "XAutoDaily Round Icon",
        Modifier
            .padding(top = 36.dp)
            .size(80.dp)
    )

    Icon(
        tint = Color(0xFF202124),
        imageVector = Icons.XAutoDaily,
        contentDescription = "text logo",
        modifier = Modifier.padding(top = 16.dp)
    )

    Text(
        text = "v${moduleVersionName} (${moduleVersionCode}) ",
        modifier = Modifier.padding(top = 8.dp),
        style = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF5F6368),
            textAlign = TextAlign.Center,
        )
    )
}

@Composable
private fun UpdateLayout(viewmodel: AboutViewModel = viewModel()) {

    val qqVersionName by viewmodel.qqVersionName.collectAsStateWithLifecycle()
    val qqVersionCode by viewmodel.qqVersionCode.collectAsStateWithLifecycle()
    val configVersion by viewmodel.configVersion.collectAsStateWithLifecycle()
    val hasUpdate by viewmodel.hasUpdate.collectAsStateWithLifecycle()

    Row(
        Modifier
            .padding(top = 24.dp)
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .background(Color(0xFFFFFFFF))
            .clickable(role = Role.Button, onClick = {
                viewmodel.updateApp()
            })
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
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF202124)
                )
            )

            Text(
                text = "当前宿主版本：${qqVersionName} (${qqVersionCode})\n"
                        + "当前配置版本：${configVersion}",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF4F5355)
                )
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        if (hasUpdate) {
            Text(
                text = "有新版本",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.themeColor
                )
            )
        }

        Icon(
            imageVector = Icons.ChevronRight,
            contentDescription = "",
            modifier = Modifier.size(24.dp),
            tint = Color(0xFFE6E6E6)
        )
    }
}

@Composable
private fun LicenseLayout(navController: NavController) {
    TextItem(
        text = "开放源代码许可",
        modifier = Modifier
            .padding(top = 24.dp)
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .background(Color(0xFFFFFFFF)),
        clickEnabled = true
    ) {
        navController.navigate(NavigationItem.License, NavigationItem.About)
    }
}

@Composable
private fun OthterLayout(navController: NavController, viewmodel: AboutViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .padding(top = 24.dp)
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .background(color = Color(0xffffffff)),
    ) {
        TextItem(
            text = "开发者",
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            clickEnabled = true
        ) {
            navController.navigate(NavigationItem.Developer, NavigationItem.About)
        }

        TextItem(
            text = "Github",
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            clickEnabled = true
        ) {
            viewmodel.openGithub(navController)
        }

        TextItem(
            text = "Telegram 频道",
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            clickEnabled = true
        ) {
            viewmodel.openTelegram(navController)
        }

        TextInfoItem(
            text = "请作者吃辣条",
            infoText = "本模块完全免费开源，一切开发旨在学习，请勿用于非法用途。喜欢本模块的可以捐赠支持我，谢谢~~",
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            clickEnabled = true
        ) {
            viewmodel.openAliPay(navController)
        }
    }

}