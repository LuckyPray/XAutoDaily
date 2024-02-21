package me.teble.xposed.autodaily.ui.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.theme.DefaultAlpha
import me.teble.xposed.autodaily.ui.theme.DisabledAlpha


@Composable
fun SwitchTextItem(
    modifier: Modifier = Modifier,
    text: String,
    clickEnabled: Boolean,
    onClick: (Boolean) -> Unit,
    enable: Boolean
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
                fontWeight = FontWeight(700),
                color = Color(0xFF202124)
            )
        )
        Spacer(modifier = Modifier.width(16.dp))
        SwitchButton(
            enable,
            modifier = Modifier
                .defaultMinSize(minWidth = 36.dp, minHeight = 24.dp)
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
    onClick: (Boolean) -> Unit,
    enable: Boolean
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
                fontWeight = FontWeight(700),
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
                .defaultMinSize(minWidth = 36.dp, minHeight = 24.dp)
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
    onClick: (Boolean) -> Unit,
    enable: Boolean

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
                    fontWeight = FontWeight(700),
                    color = Color(0xFF202124)
                )
            )

            Text(
                text = infoText,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF4F5355)
                )
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        SwitchButton(
            enable,
            modifier = Modifier
                .defaultMinSize(minWidth = 36.dp, minHeight = 24.dp)
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
                    fontWeight = FontWeight(700),
                    color = Color(0xFF202124)
                )
            )

            Text(
                modifier = Modifier.padding(end = 16.dp),
                text = infoText,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight(400),
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
                .defaultMinSize(minWidth = 36.dp, minHeight = 24.dp)
                .padding(end = 16.dp)
                .clickable(
                    role = Role.Switch,
                    enabled = clickEnabled,
                    onClick = { onChange(!enable) })
        )
    }
}