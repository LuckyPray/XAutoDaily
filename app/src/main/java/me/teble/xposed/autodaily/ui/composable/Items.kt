package me.teble.xposed.autodaily.ui.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.ChevronDown
import me.teble.xposed.autodaily.ui.icon.icons.ChevronRight
import me.teble.xposed.autodaily.ui.theme.DefaultAlpha
import me.teble.xposed.autodaily.ui.theme.DisabledAlpha

@Composable
fun TextItem(
    modifier: Modifier = Modifier,
    text: String,
    clickEnabled: Boolean,
    onClick: () -> Unit
) {
    val itemAlpha: Float by animateFloatAsState(
        targetValue = if (clickEnabled) DefaultAlpha else DisabledAlpha,
        animationSpec = spring(), label = "textInfoItem"
    )

    Row(
        modifier
            .clickable(role = Role.Switch, enabled = clickEnabled, onClick = { onClick() })
            .padding(vertical = 20.dp, horizontal = 16.dp)
            .alpha(itemAlpha),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF202124)
            )
        )
        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            imageVector = Icons.ChevronRight,
            contentDescription = "",
            modifier = Modifier.size(24.dp),
            tint = Color(0xFFE6E6E6)
        )
    }
}


@Composable
fun SelectionItem(
    modifier: Modifier = Modifier,
    text: String,
    clickEnabled: Boolean,
    onClick: () -> Unit
) {
    val itemAlpha: Float by animateFloatAsState(
        targetValue = if (clickEnabled) DefaultAlpha else DisabledAlpha,
        animationSpec = spring(), label = "textInfoItem"
    )

    Row(
        modifier
            .clickable(role = Role.Switch, enabled = clickEnabled, onClick = { onClick() })
            .padding(vertical = 20.dp, horizontal = 16.dp)
            .alpha(itemAlpha),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF202124)
            )
        )
        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            imageVector = Icons.ChevronDown,
            contentDescription = "",
            modifier = Modifier.size(24.dp),
            tint = Color(0xFFD6DDE7)
        )
    }
}


@Composable
fun TextInfoItem(
    modifier: Modifier = Modifier,
    text: String,
    infoText: String,
    clickEnabled: Boolean,
    onClick: () -> Unit
) {
    val itemAlpha: Float by animateFloatAsState(
        targetValue = if (clickEnabled) DefaultAlpha else DisabledAlpha,
        animationSpec = spring(), label = "textInfoItem"
    )

    Row(
        modifier
            .clickable(role = Role.Button, enabled = clickEnabled, onClick = { onClick() })
            .padding(vertical = 20.dp, horizontal = 16.dp)
            .alpha(itemAlpha),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = text,
                maxLines = 1,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF202124)
                )
            )

            Text(
                text = infoText,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF4F5355)
                )
            )
        }
        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            imageVector = Icons.ChevronRight,
            contentDescription = "",
            modifier = Modifier.size(24.dp),
            tint = Color(0xFFE6E6E6)
        )
    }
}


@Composable
fun SwitchTextItem(
    modifier: Modifier = Modifier,
    text: String,
    clickEnabled: Boolean,
    enable: Boolean,
    onClick: (Boolean) -> Unit,

    ) {
    Row(
        modifier
            .clickable(role = Role.Switch, enabled = clickEnabled, onClick = { onClick(!enable) })
            .padding(vertical = 20.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF202124)
            )
        )
        Spacer(modifier = Modifier.width(16.dp))
        SwitchButton(
            enable,
            modifier = Modifier
                .size(width = 48.dp, height = 24.dp)
                .clickable(
                    role = Role.Switch,
                    enabled = clickEnabled,
                    onClick = { onClick(!enable) })
        )
    }
}

@Composable
fun SwitchTextDivideItem(
    modifier: Modifier = Modifier,
    text: String,
    clickEnabled: Boolean,
    enable: Boolean,
    onClick: (Boolean) -> Unit
) {

    Row(
        modifier
            .clickable(role = Role.Switch, enabled = clickEnabled, onClick = { onClick(!enable) })
            .padding(vertical = 20.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF202124)
            )
        )
        Spacer(modifier = Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .width(2.dp)
                .fillMaxHeight()
                .background(color = Color(0xff202124), SmootherShape(2.dp)),
        )
        Spacer(modifier = Modifier.width(16.dp))

        SwitchButton(
            enable,
            modifier = Modifier
                .size(width = 48.dp, height = 24.dp)
                .clickable(
                    role = Role.Switch,
                    enabled = clickEnabled,
                    onClick = { onClick(!enable) })
        )
    }
}

@Composable
fun SwitchInfoItem(
    modifier: Modifier = Modifier,
    text: String,
    infoText: String,
    clickEnabled: Boolean,
    enable: Boolean,
    onClick: (Boolean) -> Unit

) {
    val itemAlpha: Float by animateFloatAsState(
        targetValue = if (clickEnabled) DefaultAlpha else DisabledAlpha,
        animationSpec = spring(), label = "switch item"
    )

    Row(
        modifier
            .clickable(role = Role.Button, enabled = clickEnabled, onClick = { onClick(!enable) })
            .padding(vertical = 20.dp, horizontal = 16.dp)
            .alpha(itemAlpha),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = text,
                maxLines = 1,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF202124)
                )
            )

            Text(
                text = infoText,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF4F5355)
                )
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        SwitchButton(
            enable,
            modifier = Modifier
                .size(width = 48.dp, height = 24.dp)
                .clickable(
                    role = Role.Switch,
                    enabled = clickEnabled,
                    onClick = { onClick(!enable) })
        )
    }
}

@Composable
fun SwitchInfoDivideItem(
    modifier: Modifier = Modifier,
    text: String,
    infoText: String,
    clickEnabled: Boolean,
    enable: Boolean,
    onClick: () -> Unit,
    onChange: (Boolean) -> Unit
) {
    val itemAlpha: Float by animateFloatAsState(
        targetValue = if (clickEnabled) DefaultAlpha else DisabledAlpha,
        animationSpec = spring(), label = "switch item"
    )
    Row(
        modifier.alpha(itemAlpha),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .clip(SmootherShape(12.dp))
                .clickable(
                    role = Role.Button,
                    enabled = clickEnabled,
                    onClick = { onClick() })
                .padding(vertical = 20.dp, horizontal = 16.dp),
        ) {
            Text(
                modifier = Modifier.padding(end = 16.dp),
                text = text,
                maxLines = 1,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF202124)
                )
            )

            Text(
                modifier = Modifier.padding(end = 16.dp),
                text = infoText,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF4F5355)
                )
            )
        }

        // 其他不渲染，先使用 Text 占位
        Text(
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
                .background(
                    color = Color(0xFFE6E6E6),
                    SmootherShape(1.dp)
                ),
            text = ""
        )
        Spacer(modifier = Modifier.width(16.dp))
        SwitchButton(
            enable,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(width = 48.dp, height = 24.dp)
                .clickable(
                    role = Role.Switch,
                    enabled = clickEnabled,
                    onClick = { onChange(!enable) })
        )
    }
}

@Composable
fun ImageItem(
    icon: Painter,
    contentDescription: String,
    title: String,
    info: String,
    clickEnabled: Boolean = true,
    onClick: () -> Unit = {},
    shape: Shape = SmootherShape(0.dp)
) {
    val itemAlpha: Float by animateFloatAsState(
        targetValue = if (clickEnabled) DefaultAlpha else DisabledAlpha,
        animationSpec = spring(), label = "textInfoItem"
    )
    Row(
        Modifier
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .alpha(itemAlpha)
            .clickable(role = Role.Button) {
                onClick()
            }
            .padding(16.dp)
    ) {
        Image(
            painter = icon,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(40.dp)
                .clip(shape),
            contentDescription = contentDescription
        )
        Column(
            Modifier
                .padding(start = 10.dp)
                .weight(1f)
        ) {

            Text(
                text = title, style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF202124),
                )
            )
            Text(
                text = info, Modifier.padding(top = 4.dp), style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF5F6368),
                )
            )
        }

        Icon(
            imageVector = Icons.ChevronRight,
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(24.dp),
            tint = Color(0xFFE6E6E6)
        )
    }
}


@Composable
fun SelectInfoItem(
    modifier: Modifier = Modifier,
    text: String,
    infoText: String,
    clickEnabled: Boolean,
    enable: Boolean,
    onClick: (Boolean) -> Unit

) {
    val itemAlpha: Float by animateFloatAsState(
        targetValue = if (clickEnabled) DefaultAlpha else DisabledAlpha,
        animationSpec = spring(), label = "select item"
    )

    Row(
        modifier
            .clickable(role = Role.Button, enabled = clickEnabled, onClick = { onClick(!enable) })
            .padding(all = 16.dp)
            .alpha(itemAlpha),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = text,
                maxLines = 1,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF3C4043)
                )
            )

            Text(
                text = infoText,
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF4F5355)
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        SelectButton(
            enable,
            modifier = Modifier
                .size(24.dp)
                .clickable(
                    role = Role.RadioButton,
                    enabled = clickEnabled,
                    onClick = { onClick(!enable) })
        )
    }
}