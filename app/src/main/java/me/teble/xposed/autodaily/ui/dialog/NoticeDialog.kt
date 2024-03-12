package me.teble.xposed.autodaily.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slack.circuit.overlay.OverlayHost
import com.slack.circuitx.overlays.BottomSheetOverlay
import com.slack.circuitx.overlays.DialogResult
import me.teble.xposed.autodaily.ui.composable.DialogButton
import me.teble.xposed.autodaily.ui.composable.Text
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.XAutodailyColors
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme


suspend fun OverlayHost.showNoticeOverlay(
    colors: XAutodailyColors,
    infoText: String
): DialogResult {
    return show(
        BottomSheetOverlay(
            model = infoText,
            skipPartiallyExpandedState = true,
            dragHandle = {},
            sheetContainerColor = colors.colorBgDialog,
            sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
        ) { text, overlayNavigator ->
            NoticeOverlayUI(
                infoText = text,
                onDismiss = {
                    overlayNavigator.finish(DialogResult.Dismiss)
                }
            )

        }
    )
}


@Composable
fun NoticeOverlayUI(
    infoText: String,
    onDismiss: () -> Unit
) {
    val colors = XAutodailyTheme.colors
    Column(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .defaultNavigationBarPadding()
    ) {
        Text(
            text = "公告",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp),
            color = { colors.colorText }
        )

        HorizontalDivider(
            color = XAutodailyTheme.colors.colorDialogDivider,
            thickness = 1.dp,
            modifier = Modifier.padding(top = 20.dp)
        )

        Text(
            text = infoText,
            modifier = Modifier
                .padding(top = 24.dp)
                .weight(1f, false)
                .verticalScroll(rememberScrollState()),
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
            ),
            color = { colors.colorTextSecondary }
        )

        DialogButton(
            text = "确定",
            modifier = Modifier
                .padding(top = 24.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            onClick = onDismiss
        )
    }
}