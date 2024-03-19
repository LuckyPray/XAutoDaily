package me.teble.xposed.autodaily.ui.composable

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.Save
import me.teble.xposed.autodaily.ui.theme.DisabledAlpha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloatingButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val fabColor = RippleConfiguration(
        color = Color(0xFFFFFFFF), rippleAlpha = RippleAlpha(
            pressedAlpha = 0.54f,
            focusedAlpha = 0.54f,
            draggedAlpha = 0.48f,
            hoveredAlpha = 0.24f
        )
    )
    val mutableInteractionSource = remember {
        MutableInteractionSource()
    }
    val pressed = mutableInteractionSource.collectIsPressedAsState()

    val elevation by animateDpAsState(
        targetValue = if (pressed.value) 2.dp else 4.dp,
        label = "elevation"
    )

    CompositionLocalProvider(LocalRippleConfiguration provides fabColor) {
        Box(
            modifier = modifier
                .shadow(
                    ambientColor = Color(0xFF26C288).copy(alpha = DisabledAlpha),
                    spotColor = Color(0xFF26C288).copy(alpha = DisabledAlpha),
                    clip = true, shape = CircleShape,
                    elevation = elevation
                )
                .graphicsLayer {
                    this.shadowElevation = elevation.toPx()
                }
                .clip(CircleShape)
                .drawBehind {
                    drawCircle(Color(0xFF26C288))
                }
                .clickable(
                    interactionSource = mutableInteractionSource,
                    indication = ripple(),
                    role = Role.Button
                ) {
                    onClick()
                }
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Save, "保存", modifier = Modifier.size(32.dp), tint = Color(0xFFFFFFFF))
        }
    }
}