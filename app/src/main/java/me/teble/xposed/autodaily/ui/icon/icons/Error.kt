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

public val Icons.Error: ImageVector
    get() {
        if (_error != null) {
            return _error!!
        }
        _error = Builder(
            name = "Error", defaultWidth = 40.0.dp, defaultHeight = 41.0.dp,
            viewportWidth = 40.0f, viewportHeight = 41.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(13.813f, 30.223f)
                lineTo(29.722f, 14.313f)
                curveTo(30.699f, 13.336f, 30.699f, 11.753f, 29.722f, 10.777f)
                curveTo(28.746f, 9.801f, 27.163f, 9.801f, 26.187f, 10.777f)
                lineTo(10.277f, 26.687f)
                curveTo(9.301f, 27.663f, 9.301f, 29.246f, 10.277f, 30.223f)
                curveTo(11.253f, 31.199f, 12.836f, 31.199f, 13.813f, 30.223f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(29.723f, 26.688f)
                lineTo(13.813f, 10.778f)
                curveTo(12.836f, 9.801f, 11.253f, 9.801f, 10.277f, 10.778f)
                curveTo(9.301f, 11.754f, 9.301f, 13.337f, 10.277f, 14.313f)
                lineTo(26.187f, 30.223f)
                curveTo(27.163f, 31.199f, 28.746f, 31.199f, 29.723f, 30.223f)
                curveTo(30.699f, 29.247f, 30.699f, 27.664f, 29.723f, 26.688f)
                close()
            }
        }
            .build()
        return _error!!
    }

private var _error: ImageVector? = null
