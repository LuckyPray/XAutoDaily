package me.teble.xposed.autodaily.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import me.teble.xposed.autodaily.R

@Composable
fun SwitchButton(boolean: Boolean, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = if (boolean) R.drawable.ic_toggle_on else R.drawable.ic_toggle_off),
        contentDescription = "开关",
        modifier = modifier
    )
}