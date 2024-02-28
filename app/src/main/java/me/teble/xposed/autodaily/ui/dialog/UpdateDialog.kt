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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dokar.sheets.BottomSheet
import com.dokar.sheets.BottomSheetState
import me.teble.xposed.autodaily.ui.composable.DialogButton
import me.teble.xposed.autodaily.ui.composable.DialogTopBar
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.DefaultDialogSheetBehaviors
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors


enum class UpdateType {
    Ignore,
    Drive,
    Github,
}

@Composable
fun UpdateDialog(
    state: BottomSheetState,
    text: String,
    info: String,
    onConfirm: (UpdateType) -> Unit,
    onDismiss: () -> Unit
) {

    BottomSheet(
        state = state,
        skipPeeked = true,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        backgroundColor = colors.colorBgDialog,
        behaviors = DefaultDialogSheetBehaviors,
        showAboveKeyboard = true,
        dragHandle = {}
    ) {
        Column {
            DialogTopBar(
                text = text,
                iconClick = onDismiss
            )

            HorizontalDivider(
                color = Color(0xFFF7F7F7),
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
                    color = Color(0xFF4F5355),
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
