package me.teble.xposed.autodaily.ui.dialog

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.teble.xposed.autodaily.ui.composable.DialogButton
import me.teble.xposed.autodaily.ui.composable.DialogTopBar
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.Android
import me.teble.xposed.autodaily.ui.icon.icons.Chosen
import me.teble.xposed.autodaily.ui.icon.icons.Moon
import me.teble.xposed.autodaily.ui.icon.icons.Sun
import me.teble.xposed.autodaily.ui.icon.icons.Text
import me.teble.xposed.autodaily.ui.layout.contentWindowInsets
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeModelDialog(
    state: SheetState,
    targetTheme: XAutodailyTheme.Theme,
    isBlack: Boolean,
    onConfirm: (XAutodailyTheme.Theme, Boolean) -> Unit,
    onDismiss: () -> Unit
) {

    var theme by remember {
        mutableStateOf(targetTheme)
    }
    var black by remember {
        mutableStateOf(isBlack)
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = state,
        containerColor = colors.colorBgDialog,
        windowInsets = contentWindowInsets,
        dragHandle = {},
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
    ) {
        Column {

            DialogTopBar(
                text = "主题风格",
                iconClick = onDismiss
            )

            HorizontalDivider(
                color = Color(0xFFF7F7F7),
                thickness = 1.dp,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 18.dp)
            )
            Column(
                Modifier
                    .padding(horizontal = 32.dp)
                    .weight(weight = 1f, fill = false)
                    .verticalScroll(rememberScrollState())
            ) {


                ThemeItem(
                    text = "亮色模式",
                    imageVector = Icons.Sun,
                    checked = theme == XAutodailyTheme.Theme.Light,
                    onClick = {
                        theme = XAutodailyTheme.Theme.Light
                    }
                )
                ThemeItem(
                    text = "暗色模式",
                    imageVector = Icons.Moon,
                    checked = theme == XAutodailyTheme.Theme.Dark,
                    onClick = {
                        theme = XAutodailyTheme.Theme.Dark
                    }
                )
                ThemeItem(
                    text = "跟随系统",
                    imageVector = Icons.Android,
                    checked = theme == XAutodailyTheme.Theme.System,
                    onClick = {
                        theme = XAutodailyTheme.Theme.System
                    }
                )

                HorizontalDivider(
                    color = Color(0xFFF7F7F7),
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .height(1.dp)
                )
                ThemeItem(
                    text = "使用纯黑色深色主题",
                    imageVector = Icons.Text,
                    checked = black,
                    onClick = {
                        black = !black
                    }
                )
            }
            DialogButton(
                text = "确认",
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .padding(top = 24.dp)
                    .defaultNavigationBarPadding()
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(),
                clickEnabled = theme != targetTheme
                        || isBlack != black,
                onClick = {
                    onConfirm(theme, black)
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeItem(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    text: String,
    checked: Boolean = false,
    onClick: () -> Unit = {}
) {
    val backgroundColor by animateColorAsState(
        if (checked) colors.themeColor.copy(0.08f) else Color.Transparent, label = ""
    )
    val textColor by animateColorAsState(
        if (checked) colors.themeColor else Color(0xFF4F5355), label = ""
    )

    val fabColor =
        RippleConfiguration(color = if (checked) colors.themeColor else Color.Unspecified)
    // 替换了水波纹原本的颜色
    CompositionLocalProvider(LocalRippleConfiguration provides fabColor) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp))
                .background(color = backgroundColor)
                .clickable(
                    onClick = onClick
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = text + "的图标",
                tint = textColor
            )
            Text(
                text = text,
                color = textColor,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f),
                fontWeight = FontWeight.Normal
            )
            if (checked) {
                Icon(
                    Icons.Chosen,
                    contentDescription = "选择图标",
                    tint = textColor
                )
            }
        }
    }
}