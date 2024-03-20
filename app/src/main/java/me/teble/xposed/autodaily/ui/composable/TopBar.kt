package me.teble.xposed.autodaily.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import me.teble.xposed.autodaily.ui.layout.StatusBarsTopPadding
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors

@Composable
fun XAutoDailyTopBar(
    modifier: Modifier,
    icon: ImageVector? = null,
    contentDescription: String = "",
    iconClick: () -> Unit = {}
) {
    val colors = colors
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            tint = { colors.colorText },
            imageVector = Icons.XAutoDaily,
            contentDescription = "logo"
        )
        Spacer(modifier = Modifier.weight(1f))

        if (icon != null) {
            Icon(
                imageVector = icon,
                tint = { colors.colorText },
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
    hasBackProvider: () -> Boolean = { true },
    backClick: () -> Unit = {},
) {
    val colors = colors
    Row(
        modifier = modifier
            .padding(top = StatusBarsTopPadding)
            .padding(bottom = 20.dp, top = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hasBackProvider()) {
            Icon(
                imageVector = Icons.Back,
                modifier = Modifier
                    .padding(start = 26.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = backClick)
                    .padding(6.dp),
                contentDescription = "",
                tint = { colors.colorText }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        BasicText(
            text = text,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            ),
            color = { colors.colorText }
        )
        Spacer(modifier = Modifier.weight(1f))

        if (hasBackProvider()) {
            Spacer(modifier = Modifier.width(62.dp))
        }

    }
}

@Composable
fun DialogTopBar(
    text: String,
    modifier: Modifier = Modifier,
    iconClick: () -> Unit = {},
) {
    val colors = colors
    Row(
        modifier = modifier
            .padding(start = 32.dp, end = 26.dp)
            .padding(vertical = 21.dp)
    ) {
        BasicText(
            text = text,
            modifier = Modifier.align(Alignment.CenterVertically),
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            color = { colors.colorText }
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Close,
            contentDescription = "关闭",
            tint = { colors.colorText },
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = iconClick)
                .padding(6.dp)
        )
    }
}