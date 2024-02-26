package me.teble.xposed.autodaily.ui.composable

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.gigamole.composeshadowsplus.softlayer.softLayerShadow
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
    CompositionLocalProvider(LocalRippleConfiguration provides fabColor) {
        Box(
            modifier = modifier
                .background(color = Color(0xFF26C288), CircleShape)
                .softLayerShadow(
                    radius = 4.dp,
                    color = Color(0xFF26C288).copy(alpha = DisabledAlpha),
                    shape = CircleShape,
                    offset = DpOffset(
                        x = 0.dp, y = 2.dp
                    )
                )
                .clip(CircleShape)
                .clickable(role = Role.Button) {
                    onClick()
                }
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Save, "保存", modifier = Modifier.size(24.dp), tint = Color(0xFFFFFFFF))

        }
    }
}