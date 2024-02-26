package me.teble.xposed.autodaily.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalRippleConfiguration
import androidx.compose.material.RippleConfiguration
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dokar.sheets.BottomSheetState
import com.dokar.sheets.rememberBottomSheetState
import me.teble.xposed.autodaily.ui.composable.DialogButton
import me.teble.xposed.autodaily.ui.composable.DialogTopBar
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.Chosen
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors


@Composable
fun ListDialog(
    state: BottomSheetState,
    title: String,
    list: List<String>,
    selectValue: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {

    Column() {

        DialogTopBar(
            text = title,
            iconClick = onDismiss
        )

        Divider(
            color = Color(0xFFF7F7F7),
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .padding(bottom = 12.dp)
                .height(1.dp)
                .fillMaxWidth()
        )

        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .weight(1f, false)
                .fillMaxWidth()
        ) {
            items(
                items = list,
                key = { it },
                contentType = { it }) { item ->
                ListItem(
                    text = item,
                    checked = item == selectValue
                )
            }
        }

        DialogButton(
            text = "确认",
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .padding(top = 24.dp)
                .defaultNavigationBarPadding()
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            onClick = onConfirm
        )

    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ListItem(
    modifier: Modifier = Modifier,
    text: String,
    checked: Boolean = false,
    onClick: () -> Unit = {}
) {
    // 如果添加 animateColorAsState 动画，则在点击确定的时候有概率闪退，我也不知道什么问题，不过这个加不加点击动画都那样不管他
    val backgroundColor = if (checked) Color(0xFFF5FBFF) else Color.Transparent
    val textColor = if (checked) colors.themeColor else Color(0xFF4F5355)

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
            Text(
                text = text,
                color = textColor,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
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

@Preview
@Composable
fun Vip() {
    val state = rememberBottomSheetState()

    ListDialog(
        state = state,
        "超级会员每月积分",
        listOf("Vip1", "Vip2", "Vip3"),
        selectValue = "Vip1",
        onConfirm = {},
        onDismiss = {}
    )
}