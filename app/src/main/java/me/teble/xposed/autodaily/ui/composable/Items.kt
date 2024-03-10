package me.teble.xposed.autodaily.ui.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
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
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors

@Composable
fun TextItem(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {

    val colors = colors
    Row(
        modifier
            .clickable(role = Role.Switch, onClick = { onClick() })
            .padding(vertical = 20.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            color = { colors.colorText }
        )
        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            imageVector = Icons.ChevronRight,
            contentDescription = "",
            modifier = Modifier.size(24.dp),
            tint = { colors.colorIcon }
        )
    }
}


@Composable
fun SelectionItem(
    modifier: Modifier = Modifier,
    text: String,
    clickEnabled: () -> Boolean,
    onClick: () -> Unit
) {
    val itemAlpha: Float by animateFloatAsState(
        targetValue = if (clickEnabled()) DefaultAlpha else DisabledAlpha,
        animationSpec = spring(), label = "textInfoItem"
    )
    val colors = colors

    Row(
        modifier
            .clickable(role = Role.Switch, enabled = clickEnabled(), onClick = { onClick() })
            .padding(vertical = 20.dp, horizontal = 16.dp)
            .graphicsLayer {
                alpha = itemAlpha
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            color = { colors.colorText }
        )
        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            imageVector = Icons.ChevronDown,
            contentDescription = "",
            modifier = Modifier.size(24.dp),
            tint = { colors.colorSelection }
        )
    }
}


@Composable
fun TextInfoItem(
    modifier: Modifier = Modifier,
    text: String,
    infoText: String,
    clickEnabled: () -> Boolean,
    onClick: () -> Unit
) {
    val itemAlpha: Float by animateFloatAsState(
        targetValue = if (clickEnabled()) DefaultAlpha else DisabledAlpha,
        animationSpec = spring(), label = "textInfoItem"
    )

    val colors = colors
    Row(
        modifier
            .clickable(role = Role.Button, enabled = clickEnabled(), onClick = { onClick() })
            .padding(vertical = 20.dp, horizontal = 16.dp)
            .graphicsLayer {
                alpha = itemAlpha
            },
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
                    fontWeight = FontWeight.Bold

                ),
                color = { colors.colorText }
            )

            Text(
                text = infoText,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = { colors.colorTextSecondary }
            )
        }
        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            imageVector = Icons.ChevronRight,
            contentDescription = "",
            modifier = Modifier.size(24.dp),
            tint = { colors.colorIcon }
        )
    }
}


@Composable
fun SwitchTextItem(
    modifier: Modifier = Modifier,
    text: String,
    clickEnabled: () -> Boolean,
    enable: () -> Boolean,
    onClick: (Boolean) -> Unit
) {

    val colors = colors
    Row(
        modifier
            .clickable(
                role = Role.Switch,
                enabled = clickEnabled(),
                onClick = { onClick(!enable()) })
            .padding(vertical = 20.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            color = { colors.colorText }
        )
        Spacer(modifier = Modifier.width(16.dp))
        SwitchButton(
            boolean = enable,
            modifier = Modifier,
            clickEnabled = clickEnabled,
            onClick = {
                onClick(!enable())
            }
        )
    }
}

@Composable
fun SwitchTextDivideItem(
    modifier: Modifier = Modifier,
    text: String,
    clickEnabled: () -> Boolean,
    enable: () -> Boolean,
    onClick: (Boolean) -> Unit
) {
    val colors = colors
    Row(
        modifier
            .clickable(
                role = Role.Switch,
                enabled = clickEnabled(),
                onClick = { onClick(!enable()) })
            .padding(vertical = 20.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            color = { colors.colorText }
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
            boolean = enable,
            modifier = Modifier,
            clickEnabled = clickEnabled,
            onClick = {
                onClick(!enable())
            }
        )
    }
}

@Composable
fun SwitchInfoItem(
    modifier: Modifier = Modifier,
    text: String,
    infoText: String,
    clickEnabled: () -> Boolean,
    enable: () -> Boolean,
    onClick: (Boolean) -> Unit

) {
    val itemAlpha: Float by animateFloatAsState(
        targetValue = if (clickEnabled()) DefaultAlpha else DisabledAlpha,
        animationSpec = spring(), label = "switch item"
    )

    val colors = colors
    Row(
        modifier
            .clickable(
                role = Role.Button,
                enabled = clickEnabled(),
                onClick = { onClick(!enable()) })
            .padding(vertical = 20.dp, horizontal = 16.dp)
            .graphicsLayer {
                alpha = itemAlpha
            },
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
                ),
                color = { colors.colorText }
            )

            Text(
                text = infoText,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = { colors.colorTextSecondary }
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        SwitchButton(
            boolean = enable,
            modifier = Modifier,
            clickEnabled = clickEnabled,
            onClick = {
                onClick(!enable())
            }
        )
    }
}

@Composable
fun SwitchInfoDivideItem(
    modifier: Modifier = Modifier,
    text: String,
    infoText: String,
    clickEnabled: () -> Boolean,
    enable: () -> Boolean,
    onClick: () -> Unit,
    onChange: (Boolean) -> Unit
) {

    val itemAlpha: Float by animateFloatAsState(
        targetValue = if (clickEnabled()) DefaultAlpha else DisabledAlpha,
        animationSpec = spring(), label = "switch item"
    )

    val colors = colors

    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .graphicsLayer {
                alpha = itemAlpha
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(SmootherShape(12.dp))
                .clickable(
                    role = Role.Button,
                    enabled = clickEnabled(),
                    onClick = { onClick() })
                .padding(vertical = 20.dp, horizontal = 16.dp),
        ) {
            Text(
                modifier = Modifier.padding(end = 16.dp),
                text = text,
                maxLines = 1,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = { colors.colorText }
            )

            Text(
                modifier = Modifier.padding(end = 16.dp),
                text = infoText,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = { colors.colorTextSecondary }
            )
        }
        VerticalDivider(
            color = colors.colorSwitch,
            thickness = 2.dp,
            modifier = Modifier
                .height(26.dp)
                .clip(SmootherShape(1.dp)),
        )
        Spacer(modifier = Modifier.width(16.dp))
        SwitchButton(
            boolean = enable,
            modifier = Modifier.padding(end = 16.dp),
            clickEnabled = clickEnabled,
            onClick = {
                onChange(!enable())
            }
        )
    }

}

@Composable
fun ImageItem(
    icon: Painter,
    contentDescription: String,
    title: String,
    info: String,
    clickEnabled: () -> Boolean = { true },
    onClick: () -> Unit = {},
    shape: Shape = SmootherShape(0.dp)
) {
    val itemAlpha: Float by animateFloatAsState(
        targetValue = if (clickEnabled()) DefaultAlpha else DisabledAlpha,
        animationSpec = spring(), label = "textInfoItem"
    )
    val colors = colors
    Row(
        Modifier
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .graphicsLayer {
                alpha = itemAlpha
            }
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
                text = title,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = { colors.colorText }
            )
            Text(
                text = info, Modifier.padding(top = 4.dp),
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = { colors.colorTextSecondary }
            )
        }

        Icon(
            imageVector = Icons.ChevronRight,
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(24.dp),
            tint = { colors.colorIcon }
        )
    }
}


@Composable
fun SelectInfoItem(
    modifier: Modifier = Modifier,
    text: String,
    infoText: String,
    clickEnabled: () -> Boolean,
    enable: () -> Boolean,
    onClick: (Boolean) -> Unit

) {
    val itemAlpha: Float by animateFloatAsState(
        targetValue = if (clickEnabled()) DefaultAlpha else DisabledAlpha,
        animationSpec = spring(), label = "select item"
    )

    val colors = colors

    Row(
        modifier
            .clickable(
                role = Role.Button,
                enabled = clickEnabled(),
                onClick = { onClick(!enable()) })
            .padding(all = 16.dp)
            .graphicsLayer {
                alpha = itemAlpha
            },
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
                    fontWeight = FontWeight.Normal
                ),
                color = { colors.colorText }
            )

            Text(
                text = infoText,
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = { colors.colorText },
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        SelectButton(
            enable,
            clickEnabled = clickEnabled,
            onClick = {
                onClick(!enable())
            }
        )
    }
}