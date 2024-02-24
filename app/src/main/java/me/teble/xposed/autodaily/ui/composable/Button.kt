package me.teble.xposed.autodaily.ui.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import me.teble.xposed.autodaily.R
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.SelectNormal
import me.teble.xposed.autodaily.ui.icon.icons.SelectSelectd

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
        targetValue = if (boolean) Color(0xFF0095FF) else Color(
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