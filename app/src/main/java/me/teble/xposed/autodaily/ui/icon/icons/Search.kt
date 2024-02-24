package me.teble.xposed.autodaily.ui.icon.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import me.teble.xposed.autodaily.ui.icon.Icons

public val Icons.Search: ImageVector
    get() {
        if (_search != null) {
            return _search!!
        }
        _search = Builder(
            name = "Search", defaultWidth = 16.0.dp, defaultHeight = 16.0.dp,
            viewportWidth = 16.0f, viewportHeight = 16.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF919191)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(13.82f, 12.98f)
                lineTo(11.6f, 10.76f)
                curveTo(12.365f, 9.806f, 12.788f, 8.623f, 12.8f, 7.4f)
                curveTo(12.8f, 6.332f, 12.483f, 5.288f, 11.89f, 4.4f)
                curveTo(11.297f, 3.512f, 10.453f, 2.82f, 9.466f, 2.411f)
                curveTo(8.48f, 2.002f, 7.394f, 1.895f, 6.347f, 2.104f)
                curveTo(5.299f, 2.312f, 4.337f, 2.826f, 3.582f, 3.582f)
                curveTo(2.826f, 4.337f, 2.312f, 5.299f, 2.104f, 6.347f)
                curveTo(1.895f, 7.394f, 2.002f, 8.48f, 2.411f, 9.466f)
                curveTo(2.82f, 10.453f, 3.512f, 11.297f, 4.4f, 11.89f)
                curveTo(5.288f, 12.483f, 6.332f, 12.8f, 7.4f, 12.8f)
                curveTo(8.628f, 12.816f, 9.82f, 12.39f, 10.76f, 11.6f)
                lineTo(12.98f, 13.82f)
                curveTo(13.034f, 13.877f, 13.099f, 13.922f, 13.172f, 13.953f)
                curveTo(13.244f, 13.984f, 13.321f, 14.0f, 13.4f, 14.0f)
                curveTo(13.479f, 14.0f, 13.556f, 13.984f, 13.628f, 13.953f)
                curveTo(13.701f, 13.922f, 13.766f, 13.877f, 13.82f, 13.82f)
                curveTo(13.877f, 13.766f, 13.922f, 13.701f, 13.953f, 13.628f)
                curveTo(13.984f, 13.556f, 14.0f, 13.479f, 14.0f, 13.4f)
                curveTo(14.0f, 13.321f, 13.984f, 13.244f, 13.953f, 13.172f)
                curveTo(13.922f, 13.099f, 13.877f, 13.034f, 13.82f, 12.98f)
                close()
                moveTo(3.2f, 7.4f)
                curveTo(3.2f, 6.286f, 3.642f, 5.218f, 4.43f, 4.43f)
                curveTo(5.218f, 3.642f, 6.286f, 3.2f, 7.4f, 3.2f)
                curveTo(8.514f, 3.2f, 9.582f, 3.642f, 10.37f, 4.43f)
                curveTo(11.158f, 5.218f, 11.6f, 6.286f, 11.6f, 7.4f)
                curveTo(11.597f, 8.499f, 11.167f, 9.553f, 10.4f, 10.34f)
                curveTo(9.808f, 10.915f, 9.061f, 11.305f, 8.251f, 11.462f)
                curveTo(7.441f, 11.62f, 6.602f, 11.537f, 5.838f, 11.225f)
                curveTo(5.074f, 10.913f, 4.418f, 10.385f, 3.949f, 9.706f)
                curveTo(3.481f, 9.026f, 3.22f, 8.225f, 3.2f, 7.4f)
                close()
            }
        }
            .build()
        return _search!!
    }

private var _search: ImageVector? = null
