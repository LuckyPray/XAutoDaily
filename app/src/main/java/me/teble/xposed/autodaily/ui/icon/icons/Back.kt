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

public val Icons.Back: ImageVector
    get() {
        if (_back != null) {
            return _back!!
        }
        _back = Builder(name = "Back", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(20.547f, 11.1f)
                horizontalLineTo(4.167f)
                curveTo(3.72f, 11.1f, 3.357f, 11.503f, 3.357f, 12.0f)
                curveTo(3.357f, 12.497f, 3.72f, 12.9f, 4.167f, 12.9f)
                horizontalLineTo(20.547f)
                curveTo(20.994f, 12.9f, 21.357f, 12.497f, 21.357f, 12.0f)
                curveTo(21.357f, 11.503f, 20.994f, 11.1f, 20.547f, 11.1f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(11.554f, 5.415f)
                curveTo(11.866f, 5.102f, 11.866f, 4.596f, 11.554f, 4.283f)
                curveTo(11.241f, 3.971f, 10.735f, 3.971f, 10.422f, 4.283f)
                lineTo(3.351f, 11.354f)
                curveTo(3.039f, 11.667f, 3.039f, 12.174f, 3.351f, 12.486f)
                curveTo(3.664f, 12.798f, 4.17f, 12.798f, 4.483f, 12.486f)
                lineTo(11.554f, 5.415f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(10.281f, 19.718f)
                curveTo(10.593f, 20.03f, 11.1f, 20.03f, 11.412f, 19.718f)
                curveTo(11.725f, 19.405f, 11.725f, 18.899f, 11.412f, 18.586f)
                lineTo(4.341f, 11.515f)
                curveTo(4.029f, 11.203f, 3.522f, 11.203f, 3.21f, 11.515f)
                curveTo(2.897f, 11.828f, 2.897f, 12.334f, 3.21f, 12.646f)
                lineTo(10.281f, 19.718f)
                close()
            }
        }
        .build()
        return _back!!
    }

private var _back: ImageVector? = null
