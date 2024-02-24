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

public val Icons.Close: ImageVector
    get() {
        if (_close != null) {
            return _close!!
        }
        _close = Builder(
            name = "Close", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
            viewportWidth = 24.0f, viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF202124)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(13.4f, 12.0f)
                lineTo(18.7f, 6.7f)
                curveTo(18.793f, 6.608f, 18.868f, 6.499f, 18.919f, 6.379f)
                curveTo(18.97f, 6.258f, 18.996f, 6.129f, 18.997f, 5.998f)
                curveTo(18.998f, 5.867f, 18.972f, 5.737f, 18.923f, 5.616f)
                curveTo(18.873f, 5.495f, 18.8f, 5.385f, 18.707f, 5.293f)
                curveTo(18.615f, 5.2f, 18.505f, 5.127f, 18.384f, 5.077f)
                curveTo(18.263f, 5.028f, 18.133f, 5.002f, 18.002f, 5.003f)
                curveTo(17.871f, 5.004f, 17.742f, 5.03f, 17.621f, 5.081f)
                curveTo(17.501f, 5.132f, 17.392f, 5.207f, 17.3f, 5.3f)
                lineTo(12.0f, 10.6f)
                lineTo(6.7f, 5.3f)
                curveTo(6.514f, 5.117f, 6.263f, 5.016f, 6.002f, 5.017f)
                curveTo(5.741f, 5.018f, 5.491f, 5.123f, 5.307f, 5.307f)
                curveTo(5.123f, 5.491f, 5.018f, 5.741f, 5.017f, 6.002f)
                curveTo(5.016f, 6.263f, 5.117f, 6.514f, 5.3f, 6.7f)
                lineTo(10.6f, 12.0f)
                lineTo(5.3f, 17.3f)
                curveTo(5.205f, 17.39f, 5.13f, 17.499f, 5.078f, 17.619f)
                curveTo(5.027f, 17.74f, 5.0f, 17.869f, 5.0f, 18.0f)
                curveTo(5.0f, 18.131f, 5.027f, 18.26f, 5.078f, 18.381f)
                curveTo(5.13f, 18.501f, 5.205f, 18.61f, 5.3f, 18.7f)
                curveTo(5.39f, 18.795f, 5.499f, 18.87f, 5.619f, 18.922f)
                curveTo(5.74f, 18.973f, 5.869f, 19.0f, 6.0f, 19.0f)
                curveTo(6.131f, 19.0f, 6.26f, 18.973f, 6.381f, 18.922f)
                curveTo(6.501f, 18.87f, 6.61f, 18.795f, 6.7f, 18.7f)
                lineTo(12.0f, 13.4f)
                lineTo(17.3f, 18.7f)
                curveTo(17.39f, 18.795f, 17.499f, 18.87f, 17.619f, 18.922f)
                curveTo(17.74f, 18.973f, 17.869f, 19.0f, 18.0f, 19.0f)
                curveTo(18.131f, 19.0f, 18.26f, 18.973f, 18.381f, 18.922f)
                curveTo(18.501f, 18.87f, 18.61f, 18.795f, 18.7f, 18.7f)
                curveTo(18.795f, 18.61f, 18.87f, 18.501f, 18.922f, 18.381f)
                curveTo(18.973f, 18.26f, 19.0f, 18.131f, 19.0f, 18.0f)
                curveTo(19.0f, 17.869f, 18.973f, 17.74f, 18.922f, 17.619f)
                curveTo(18.87f, 17.499f, 18.795f, 17.39f, 18.7f, 17.3f)
                lineTo(13.4f, 12.0f)
                close()
            }
        }
            .build()
        return _close!!
    }

private var _close: ImageVector? = null
