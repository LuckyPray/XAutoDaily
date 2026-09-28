package me.teble.xposed.autodaily.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import me.teble.xposed.autodaily.ui.layout.DialogHorizontalPadding
import me.teble.xposed.autodaily.ui.layout.DialogVerticalPadding
import me.teble.xposed.autodaily.ui.layout.HorizontalPadding
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
                    .clickable(
                        interactionSource = null,
                        role = Role.Button,
                        indication = ripple(bounded = false, radius = 18.dp),
                        onClick = iconClick
                    )
                    .size(24.dp),
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
            .padding(HorizontalPadding)
            .padding(StatusBarsTopPadding)
            .padding(bottom = 20.dp, top = 10.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (hasBackProvider()) {
            Icon(
                imageVector = Icons.Back,
                modifier = Modifier
                    .clickable(
                        interactionSource = null,
                        role = Role.Button,
                        indication = ripple(bounded = false, radius = 18.dp),
                        onClick = backClick
                    )
                    .size(24.dp),
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
            Spacer(modifier = Modifier.width(24.dp))
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
            .padding(DialogHorizontalPadding)
            .padding(DialogVerticalPadding),
        verticalAlignment = Alignment.CenterVertically

    ) {
        BasicText(
            text = text,
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
                .clickable(
                    interactionSource = null,
                    role = Role.Button,
                    indication = ripple(bounded = false, radius = 18.dp),
                    onClick = iconClick
                )
                .size(24.dp)


        )
    }
}