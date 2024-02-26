package me.teble.xposed.autodaily.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalRippleConfiguration
import androidx.compose.material.RippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.dokar.sheets.BottomSheetState
import androidx.compose.ui.Alignment
import me.teble.xposed.autodaily.ui.icon.Icons
import androidx.compose.ui.Modifier
import me.teble.xposed.autodaily.ui.icon.icons.Chosen
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import me.teble.xposed.autodaily.ui.icon.icons.Close
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dokar.sheets.BottomSheet
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.icons.Android
import me.teble.xposed.autodaily.ui.icon.icons.Moon
import me.teble.xposed.autodaily.ui.icon.icons.Sun
import me.teble.xposed.autodaily.ui.icon.icons.Text
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.DefaultDialogSheetBehaviors

@Composable
fun ThemeDialog(
    state: BottomSheetState,

    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    BottomSheet(
        state = state,
        skipPeeked = true,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        backgroundColor = Color(0xFFFFFFFF),
        behaviors = DefaultDialogSheetBehaviors,
        showAboveKeyboard = true,
        dragHandle = {}
    ) {
        Column {

            Row(
                modifier = Modifier
                    .padding(start = 32.dp, end = 26.dp)
                    .padding(vertical = 21.dp)
            ) {
                Text(
                    text = "主题风格",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF202124),
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Close, contentDescription = "关闭",
                    Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable(role = Role.Button, onClick = onDismiss)
                        .padding(6.dp)
                )
            }
            Divider(
                color = Color(0xFFF7F7F7),
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 12.dp)
                    .height(1.dp)
                    .fillMaxWidth()
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
                    checked = true,
                    onClick = {

                    }
                )
                ThemeItem(
                    text = "暗色模式",
                    imageVector = Icons.Moon,
                    checked = false,
                    onClick = {

                    }
                )
                ThemeItem(
                    text = "跟随系统",
                    imageVector = Icons.Android,
                    checked = false,
                    onClick = {

                    }
                )

                Divider(
                    color = Color(0xFFF7F7F7),
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .height(1.dp)
                        .fillMaxWidth()
                )
                ThemeItem(
                    text = "使用纯黑色深色主题",
                    imageVector = Icons.Text,
                    checked = false,
                    onClick = {

                    }
                )


            }
            Text(
                text = "确认",
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .padding(top = 24.dp)
                    .defaultNavigationBarPadding()
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .clip(SmootherShape(12.dp))
                    .background(Color(0x0F0095FF))
                    .clickable(
                        role = Role.Button,
                        onClick = onConfirm
                    )
                    .padding(vertical = 16.dp),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0095FF),
                    textAlign = TextAlign.Center,
                )
            )
        }

    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ThemeItem(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    text: String,
    checked: Boolean = false,
    onClick: () -> Unit = {}
) {
    // 如果添加 animateColorAsState 动画，则在点击确定的时候有概率闪退，我也不知道什么问题，不过这个加不加点击动画都那样不管他
    val backgroundColor = if (checked) Color(0xFFF5FBFF) else Color.Transparent
    val textColor = if (checked) Color(0xFF0095FF) else Color(0xFF4F5355)

    val fabColor =
        RippleConfiguration(color = if (checked) Color(0xFF0095FF) else Color.Unspecified)
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