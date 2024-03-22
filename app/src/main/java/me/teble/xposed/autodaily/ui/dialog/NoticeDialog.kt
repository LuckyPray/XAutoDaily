package me.teble.xposed.autodaily.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.teble.xposed.autodaily.ui.composable.DialogButton
import me.teble.xposed.autodaily.ui.composable.Text
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.layout.DialogHorizontalPadding
import me.teble.xposed.autodaily.ui.layout.DialogVerticalPadding
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme


@Composable
fun NoticeOverlayUI(
    infoText: String,
    onDismiss: () -> Unit
) {
    val colors = XAutodailyTheme.colors
    Column(
        modifier = Modifier
            .padding(DialogHorizontalPadding)
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
                .padding(DialogVerticalPadding),
            color = { colors.colorText }
        )

        HorizontalDivider(
            color = XAutodailyTheme.colors.colorDialogDivider,
            thickness = 1.dp
        )

        Text(
            text = infoText,
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