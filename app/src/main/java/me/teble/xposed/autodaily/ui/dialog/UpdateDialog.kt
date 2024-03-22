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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.teble.xposed.autodaily.ui.composable.DialogButton
import me.teble.xposed.autodaily.ui.composable.DialogTopBar
import me.teble.xposed.autodaily.ui.composable.Text
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.layout.DialogHorizontalPadding
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors


enum class UpdateType {
    Ignore,
    Drive,
    Github,
}

@Composable
fun UpdateOverlayUI(
    info: () -> String,
    onDismiss: () -> Unit,
    viewmodel: UpdateViewModel = viewModel()
) {
    val context = LocalContext.current
    Column {
        DialogTopBar(
            text = "新版本",
            iconClick = onDismiss
        )

        HorizontalDivider(
            color = colors.colorDialogDivider,
            thickness = 1.dp,
            modifier = Modifier
                .padding(DialogHorizontalPadding)
                .padding(bottom = 24.dp)
        )


        Text(
            text = info,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, false)
                .padding(DialogHorizontalPadding)
                .verticalScroll(rememberScrollState())
                .clip(SmootherShape(12.dp)),
            style = TextStyle(
                color = colors.colorTextSecondary,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )
        )

        UpdateBottomBar(onConfirm = viewmodel.updateConfirm(context))

    }
}

@Composable
private fun UpdateBottomBar(onConfirm: (UpdateType) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .defaultNavigationBarPadding()
            .padding(top = 24.dp)
            .padding(horizontal = 32.dp)
    ) {
        DialogButton(
            text = "忽略",
            modifier = Modifier.weight(3f),
            onClick = {
                onConfirm(UpdateType.Ignore)
            }
        )
        Spacer(modifier = Modifier.width(16.dp))

        DialogButton(
            text = "123 盘",
            modifier = Modifier
                .weight(4f),
            onClick = {
                onConfirm(UpdateType.Drive)
            }
        )
        Spacer(modifier = Modifier.width(16.dp))

        DialogButton(
            text = "GitHub",
            modifier = Modifier.weight(4f),
            onClick = {
                onConfirm(UpdateType.Github)
            }
        )
    }
}
