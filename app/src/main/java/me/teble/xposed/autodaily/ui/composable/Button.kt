package me.teble.xposed.autodaily.ui.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.teble.xposed.autodaily.R
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.SelectNormal
import me.teble.xposed.autodaily.ui.icon.icons.SelectSelectd
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme

@Composable
fun SwitchButton(boolean: Boolean, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = if (boolean) R.drawable.ic_toggle_on else R.drawable.ic_toggle_off),
        contentDescription = "开关",
        modifier = modifier
    )
}


@Composable
fun SelectButton(boolean: Boolean, modifier: Modifier = Modifier) {
    val selectColor by animateColorAsState(
        targetValue = if (boolean) XAutodailyTheme.colors.themeColor else Color(
            0xFFE6E6E6
        ), label = "SelectButton"
    )
    Icon(
        imageVector = if (boolean) Icons.SelectSelectd else Icons.SelectNormal,
        tint = selectColor,
        contentDescription = "选中开关",
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogButton(text: String, modifier: Modifier, onClick: () -> Unit) {

    val buttonColor = RippleConfiguration(color = XAutodailyTheme.colors.themeColor)
    CompositionLocalProvider(LocalRippleConfiguration provides buttonColor) {
        Text(
            text = text,
            modifier = modifier
                .clip(SmootherShape(12.dp))
                .background(Color(0x0F0095FF))
                .clickable(
                    role = Role.Button,
                    onClick = onClick
                )
                .padding(vertical = 16.dp),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = XAutodailyTheme.colors.themeColor,
                textAlign = TextAlign.Center,
            )
        )
    }
}