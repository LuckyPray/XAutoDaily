package me.teble.xposed.autodaily.ui.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.RippleAlpha
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.SelectNormal
import me.teble.xposed.autodaily.ui.icon.icons.SelectSelectd
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwitchButton(
    boolean: Boolean,
    modifier: Modifier = Modifier,
    clickEnabled: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue =
        if (boolean) colors.themeColor else colors.colorSwitch, label = ""
    )
    val fabColor = RippleConfiguration(
        color = Color(0xFFFFFFFF), rippleAlpha = RippleAlpha(
            pressedAlpha = 0.36f,
            focusedAlpha = 0.36f,
            draggedAlpha = 0.24f,
            hoveredAlpha = 0.16f
        )
    )
    CompositionLocalProvider(LocalRippleConfiguration provides fabColor) {
        Box(
            modifier = modifier
                .size(width = 48.dp, height = 24.dp)
                .clip(SmootherShape(12.dp))
                .background(color = backgroundColor)
                .clickable(
                    role = Role.Switch,
                    enabled = clickEnabled,
                    onClick = onClick
                )
                .padding(6.dp)
        ) {


            val translationX by animateDpAsState(
                targetValue = if (boolean) {
                    24.dp
                } else {
                    0.dp
                },
                animationSpec = spring(),
                label = "startPadding"
            )
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        this.translationX = translationX.toPx()
                    }
                    .size(12.dp)
                    .background(Color.White, shape = CircleShape)
            )
        }
    }

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
fun DialogButton(
    text: String,
    modifier: Modifier,
    clickEnabled: Boolean = false,
    onClick: () -> Unit
) {

    val buttonColor = RippleConfiguration(color = XAutodailyTheme.colors.themeColor)
    CompositionLocalProvider(LocalRippleConfiguration provides buttonColor) {

        val backgroundColor by animateColorAsState(
            if (clickEnabled) XAutodailyTheme.colors.themeColor.copy(0.06f) else XAutodailyTheme.colors.themeColor.copy(
                0.04f
            ), label = ""
        )

        val textColor by animateColorAsState(
            if (clickEnabled) XAutodailyTheme.colors.themeColor else XAutodailyTheme.colors.themeColor.copy(
                0.6f
            ), label = ""
        )

        Text(
            text = text,
            modifier = modifier
                .clip(SmootherShape(12.dp))
                .background(color = backgroundColor)
                .clickable(
                    role = Role.Button,
                    enabled = clickEnabled,
                    onClick = onClick
                )
                .padding(vertical = 16.dp),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center,
            )
        )

    }
}