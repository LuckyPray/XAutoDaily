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

public val Icons.Activated: ImageVector
    get() {
        if (_activated != null) {
            return _activated!!
        }
        _activated = Builder(
            name = "Activated", defaultWidth = 32.0.dp, defaultHeight = 33.0.dp,
            viewportWidth = 32.0f, viewportHeight = 33.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(15.117f, 23.393f)
                lineTo(26.077f, 12.433f)
                curveTo(26.858f, 11.652f, 26.858f, 10.386f, 26.077f, 9.605f)
                curveTo(25.296f, 8.824f, 24.03f, 8.824f, 23.249f, 9.605f)
                lineTo(12.288f, 20.565f)
                curveTo(11.507f, 21.346f, 11.507f, 22.612f, 12.288f, 23.393f)
                curveTo(13.069f, 24.174f, 14.336f, 24.174f, 15.117f, 23.393f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(15.117f, 20.565f)
                lineTo(8.753f, 14.201f)
                curveTo(7.972f, 13.42f, 6.705f, 13.42f, 5.924f, 14.201f)
                curveTo(5.143f, 14.982f, 5.143f, 16.249f, 5.924f, 17.03f)
                lineTo(12.288f, 23.394f)
                curveTo(13.069f, 24.175f, 14.335f, 24.175f, 15.117f, 23.394f)
                curveTo(15.898f, 22.612f, 15.898f, 21.346f, 15.117f, 20.565f)
                close()
            }
        }
            .build()
        return _activated!!
    }

private var _activated: ImageVector? = null
