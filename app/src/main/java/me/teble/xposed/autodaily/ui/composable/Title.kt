package me.teble.xposed.autodaily.ui.composable

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors

@Composable
fun SmallTitle(title: String, modifier: Modifier) {
    Text(
        text = title,
        modifier = modifier,
        style = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = colors.colorTextSmallTitle
        )
    )
}