package me.teble.xposed.autodaily.ui.dialog

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.teble.xposed.autodaily.ui.composable.DialogButton
import me.teble.xposed.autodaily.ui.composable.DialogTopBar
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.Chosen
import me.teble.xposed.autodaily.ui.layout.contentWindowInsets
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDialog(
    state: SheetState,
    title: String,
    list: List<String>,
    selectValue: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = state,
        containerColor = colors.colorBgDialog,
        windowInsets = contentWindowInsets,
        dragHandle = {},
        scrimColor = colors.colorBgMask,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
    ) {
    Column() {

        DialogTopBar(
            text = title,
            iconClick = onDismiss
        )

        HorizontalDivider(
            color = colors.colorBgDialog,
            thickness = 1.dp,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .padding(bottom = 16.dp)
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

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListItem(
    modifier: Modifier = Modifier,
    text: String,
    checked: Boolean = false,
    onClick: () -> Unit = {}
) {

    val backgroundColor by animateColorAsState(
        if (checked) colors.themeColor.copy(0.08f) else Color.Transparent, label = ""
    )
    val textColor by animateColorAsState(
        if (checked) colors.themeColor else colors.colorTextTheme, label = ""
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

//@Preview
//@Composable
//fun Vip() {
//    val state = rememberBottomSheetState()
//
//    ListDialog(
//        state = state,
//        "超级会员每月积分",
//        listOf("Vip1", "Vip2", "Vip3"),
//        selectValue = "Vip1",
//        onConfirm = {},
//        onDismiss = {}
//    )
//}