package me.teble.xposed.autodaily.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.teble.xposed.autodaily.ui.composable.DialogButton
import me.teble.xposed.autodaily.ui.composable.DialogTopBar
import me.teble.xposed.autodaily.ui.composable.Text
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.layout.DialogHorizontalPadding
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme
import kotlin.system.exitProcess

@Composable
fun RestoreOverlayUI(
    onDismiss: () -> Unit
) {
    val colors = XAutodailyTheme.colors
    DialogTopBar(
        text = "配置已恢复",
        iconClick = onDismiss
    )
    Column(
        modifier = Modifier
            .padding(DialogHorizontalPadding)
            .defaultNavigationBarPadding()
    ) {
        HorizontalDivider(
            color = XAutodailyTheme.colors.colorDialogDivider,
            thickness = 1.dp
        )

        Text(
            text = "配置恢复完成，新配置需要重启应用才能生效，是否立刻重启？",
            modifier = Modifier
                .padding(top = 24.dp)
                .weight(1f, false)
                .verticalScroll(rememberScrollState())
                .clip(SmootherShape(12.dp)),
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
            ),
            color = { colors.colorTextSecondary }
        )

        RestorBottomBar(onDismiss = onDismiss)

    }
}

@Composable
private fun RestorBottomBar(onDismiss: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        DialogButton(
            text = "稍后重启",
            modifier = Modifier.weight(1f),
            onClick = onDismiss
        )
        Spacer(modifier = Modifier.width(16.dp))

        DialogButton(
            text = "立即重启",
            modifier = Modifier.weight(1f),
            onClick = {
                exitProcess(0)
            }
        )
    }
}