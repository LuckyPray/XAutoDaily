package me.teble.xposed.autodaily.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.Back
import me.teble.xposed.autodaily.ui.icon.icons.Close
import me.teble.xposed.autodaily.ui.icon.icons.XAutoDaily

@Composable
fun XAutoDailyTopBar(
    modifier: Modifier,
    icon: ImageVector? = null,
    contentDescription: String = "",
    iconClick: () -> Unit = {}
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            tint = Color(0xFF202124),
            imageVector = Icons.XAutoDaily,
            contentDescription = "logo"
        )
        Spacer(modifier = Modifier.weight(1f))

        if (icon != null) {
            Icon(
                imageVector = icon,
                tint = Color(0xFF202124),
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = iconClick)
                    .padding(6.dp),
                contentDescription = contentDescription
            )

        }
    }
}

@Composable
fun TopBar(
    text: String,
    modifier: Modifier = Modifier,
    backClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .statusBarsPadding()
            .padding(bottom = 20.dp, top = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Back,
            modifier = Modifier
                .padding(start = 26.dp)
                .size(36.dp)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = backClick)
                .padding(6.dp),
            contentDescription = "",
            tint = Color(0xFF202124)
        )
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = text,
            style = TextStyle(
                color = Color(0xFF202124),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
        )
        Spacer(modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.width(62.dp))

    }
}

@Composable
fun DialogTopBar(
    text: String,
    modifier: Modifier = Modifier,
    iconClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .padding(start = 32.dp, end = 26.dp)
            .padding(vertical = 21.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF202124),
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Close, contentDescription = "关闭",
            Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = iconClick)
                .padding(6.dp)
        )
    }
}