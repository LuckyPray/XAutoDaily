package me.teble.xposed.autodaily.ui.scene

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.teble.xposed.autodaily.R
import me.teble.xposed.autodaily.ui.composable.Icon
import me.teble.xposed.autodaily.ui.composable.RoundedSnackbarHost
import me.teble.xposed.autodaily.ui.composable.Text
import me.teble.xposed.autodaily.ui.composable.XAutoDailyTopBar
import me.teble.xposed.autodaily.ui.dialog.NoticeDialog
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.About
import me.teble.xposed.autodaily.ui.icon.icons.ChevronRight
import me.teble.xposed.autodaily.ui.icon.icons.Configuration
import me.teble.xposed.autodaily.ui.icon.icons.Notice
import me.teble.xposed.autodaily.ui.icon.icons.Script
import me.teble.xposed.autodaily.ui.icon.icons.Setting
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.CardDisabledAlpha
import me.teble.xposed.autodaily.ui.theme.DefaultAlpha
import me.teble.xposed.autodaily.ui.theme.DisabledAlpha
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScene(
    onNavigateToSign: () -> Unit,
    onNavigateToSetting: () -> Unit,
    onNavigateToAbout: () -> Unit,

    viewmodel: MainViewModel = viewModel()
) {


    val colors = colors

    Box {


        Scaffold(
            snackbarHost = {
                RoundedSnackbarHost(hostState = viewmodel.snackbarHostState)
            }, topBar = {
                XAutoDailyTopBar(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp)
                        .padding(vertical = 20.dp)
                        .padding(start = 16.dp),
                    icon = Icons.Notice,
                    contentDescription = "公告",
                    iconClick = viewmodel::showNoticeDialog
                )
            }, containerColor = colors.colorBgLayout
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = 16.dp)
                    .clip(SmootherShape(12.dp))
                    .verticalScroll(rememberScrollState())
                    .defaultNavigationBarPadding()
            ) {

                Banner(execTaskNum = viewmodel::execTaskNum, signClick = viewmodel::signClick)
                GridLayout(
                    onNavigateToSign = onNavigateToSign,
                    onNavigateToSetting = onNavigateToSetting,
                    onNavigateToAbout = onNavigateToAbout,
                    execTaskNum = viewmodel::execTaskNum
                )
            }

        }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        LaunchedEffect(viewmodel.noticeDialog) {
            if (viewmodel.noticeDialog) {
                sheetState.expand()
            } else {
                sheetState.hide()
            }
        }
        NoticeDialog(
            enable = viewmodel::noticeDialog,
            sheetState = sheetState,
            infoText = viewmodel::noticeText,
            onDismiss = viewmodel::dismissNoticeDialog
        )

    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.Banner(execTaskNum: () -> Int, signClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(top = 24.dp)
            .align(alignment = Alignment.CenterHorizontally),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val colors = colors

        Text(
            text = { execTaskNum().toString() },
            style = TextStyle(
                fontSize = 64.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFF2ECC71),
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.tcloud_number_vf))
            )
        )
        Text(
            text = "今日执行", style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            ),
            color = { colors.colorTextSecondary }
        )

        val fabColor = RippleConfiguration(
            color = colors.themeColor, rippleAlpha = RippleAlpha(
                pressedAlpha = 0.32f,
                focusedAlpha = 0.32f,
                draggedAlpha = 0.24f,
                hoveredAlpha = 0.16f
            )
        )
        CompositionLocalProvider(LocalRippleConfiguration provides fabColor) {
            Text(
                text = "立即签到",
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clip(shape = SmootherShape(radius = 24.dp))
                    .drawBehind {
                        drawRect(colors.themeColor.copy(alpha = 0.08f))
                    }
                    .clickable(
                        role = Role.Button,
                        onClick = signClick
                    )
                    .padding(start = 32.dp, top = 10.dp, end = 32.dp, bottom = 10.dp),
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                color = { colors.themeColor }
            )
        }


    }
}

@Composable
private fun GridLayout(
    onNavigateToSign: () -> Unit,
    onNavigateToSetting: () -> Unit,
    onNavigateToAbout: () -> Unit,
    execTaskNum: () -> Int
) {
    val colors = colors
    Column(
        modifier = Modifier.padding(top = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CardItem(
                iconColor = Color(0xFF47B6FF),
                Icons.Configuration,
                "签到配置",
                { "已启用 ${execTaskNum()} 项" },
                true,
                onClick = onNavigateToSign
            )
            CardItem(
                iconColor = Color(0xFF8286FF), Icons.Script, "自定义脚本", { "敬请期待" }, false
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp))
                .drawBehind {
                    drawRect(colors.colorBgContainer)
                },
        ) {
            TextItem(iconColor = Color(0xFF60D893), Icons.Setting, "设置", onNavigateToSetting)
            TextItem(iconColor = Color(0xFFFFBC04), Icons.About, "关于", onNavigateToAbout)

        }
    }


}


@Composable
private fun RowScope.CardItem(
    iconColor: Color,
    imageVector: ImageVector,
    text: String,
    subText: () -> String,
    enable: Boolean = true,
    onClick: () -> Unit = {}
) {
    val colors = colors
    val cardColorAlpha: Float by animateFloatAsState(
        targetValue = if (enable) DefaultAlpha else CardDisabledAlpha,
        animationSpec = spring(),
        label = "switch item"
    )
    val textColorAlpha: Float by animateFloatAsState(
        targetValue = if (enable) DefaultAlpha else DisabledAlpha,
        animationSpec = spring(),
        label = "switch item color"
    )

    Column(
        Modifier
            .weight(1f)
            .clip(SmootherShape(12.dp))
            .drawBehind {
                drawRect(colors.colorBgContainer.copy(alpha = cardColorAlpha))
            }
            .clickable(role = Role.Button, enabled = enable, onClick = onClick)
            .padding(top = 24.dp, start = 16.dp, bottom = 24.dp)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "",
            modifier = Modifier
                .size(32.dp)
                .drawBehind {
                    drawCircle(iconColor.copy(cardColorAlpha))
                },
            tint = { Color(0xffffffff) }
        )

        Text(
            text = text,
            Modifier
                .padding(top = 16.dp)
                .graphicsLayer {
                    alpha = textColorAlpha
                },
            style = TextStyle(
                fontSize = 18.sp,
                lineHeight = 21.6.sp,
                fontWeight = FontWeight.Bold
            ),
            color = { colors.colorText },
        )
        Text(
            text = subText,
            Modifier
                .padding(top = 4.dp)
                .graphicsLayer {
                    alpha = textColorAlpha
                },
            style = TextStyle(
                fontSize = 12.sp,
                lineHeight = 14.4.sp,
                fontWeight = FontWeight.Normal
            ),
            color = { colors.colorTextSecondary }
        )
    }
}

@Composable
private fun TextItem(
    iconColor: Color,
    imageVector: ImageVector,
    text: String, onClick: () -> Unit
) {
    val colors = colors
    Row(
        Modifier
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = 15.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "",
            modifier = Modifier
                .size(32.dp)
                .drawBehind {
                    drawCircle(iconColor)
                },
            tint = { Color(0xffffffff) }
        )

        Text(
            text = text, Modifier.padding(start = 16.dp), style = TextStyle(
                fontSize = 18.sp, fontWeight = FontWeight.Bold
            ),
            color = { colors.colorText }
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.ChevronRight,
            contentDescription = "",
            modifier = Modifier.size(24.dp),
            tint = { colors.colorIcon }
        )
    }
}

