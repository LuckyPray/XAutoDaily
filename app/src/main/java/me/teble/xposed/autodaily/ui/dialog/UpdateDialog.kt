package me.teble.xposed.autodaily.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.teble.xposed.autodaily.ui.composable.DialogButton
import me.teble.xposed.autodaily.ui.composable.DialogTopBar
import me.teble.xposed.autodaily.ui.layout.contentWindowInsets
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors


enum class UpdateType {
    Ignore,
    Drive,
    Github,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateDialog(
    state: SheetState,
    text: String,
    info: String,
    onConfirm: (UpdateType) -> Unit,
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
        Column {
            DialogTopBar(
                text = text,
                iconClick = onDismiss
            )

            HorizontalDivider(
                color = colors.colorDialogDivider,
                thickness = 1.dp,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 24.dp)
            )


            Text(
                text = info,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, false)
                    .padding(horizontal = 32.dp)
                    .verticalScroll(rememberScrollState()),
                style = TextStyle(
                    color = colors.colorTextSecondary,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
            )

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

    }
}
