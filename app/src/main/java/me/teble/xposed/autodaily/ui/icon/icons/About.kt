package me.teble.xposed.autodaily.ui.icon.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import me.teble.xposed.autodaily.ui.icon.Icons

public val Icons.About: ImageVector
    get() {
        if (_about != null) {
            return _about!!
        }
        _about = Builder(name = "About", defaultWidth = 32.0.dp, defaultHeight = 32.0.dp,
                viewportWidth = 32.0f, viewportHeight = 32.0f).apply {
            path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(16.0f, 24.0f)
                curveTo(20.418f, 24.0f, 24.0f, 20.418f, 24.0f, 16.0f)
                curveTo(24.0f, 11.582f, 20.418f, 8.0f, 16.0f, 8.0f)
                curveTo(11.582f, 8.0f, 8.0f, 11.582f, 8.0f, 16.0f)
                curveTo(8.0f, 20.418f, 11.582f, 24.0f, 16.0f, 24.0f)
                close()
                moveTo(16.9f, 12.9f)
                curveTo(16.9f, 12.403f, 16.497f, 12.0f, 16.0f, 12.0f)
                curveTo(15.503f, 12.0f, 15.1f, 12.403f, 15.1f, 12.9f)
                curveTo(15.1f, 13.397f, 15.503f, 13.8f, 16.0f, 13.8f)
                curveTo(16.497f, 13.8f, 16.9f, 13.397f, 16.9f, 12.9f)
                close()
                moveTo(16.0f, 15.2f)
                curveTo(16.497f, 15.2f, 16.9f, 15.603f, 16.9f, 16.1f)
                verticalLineTo(19.3f)
                curveTo(16.9f, 19.797f, 16.497f, 20.2f, 16.0f, 20.2f)
                curveTo(15.503f, 20.2f, 15.1f, 19.797f, 15.1f, 19.3f)
                verticalLineTo(16.1f)
                curveTo(15.1f, 15.603f, 15.503f, 15.2f, 16.0f, 15.2f)
                close()
            }
            path(fill = SolidColor(Color(0xFFffffff)), stroke = null, fillAlpha = 0.3f,
                    strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(16.9f, 12.9f)
                curveTo(16.9f, 12.403f, 16.497f, 12.0f, 16.0f, 12.0f)
                curveTo(15.503f, 12.0f, 15.1f, 12.403f, 15.1f, 12.9f)
                curveTo(15.1f, 13.397f, 15.503f, 13.8f, 16.0f, 13.8f)
                curveTo(16.497f, 13.8f, 16.9f, 13.397f, 16.9f, 12.9f)
                close()
            }
            path(fill = SolidColor(Color(0xFFffffff)), stroke = null, fillAlpha = 0.3f,
                    strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(16.9f, 16.1f)
                curveTo(16.9f, 15.603f, 16.497f, 15.2f, 16.0f, 15.2f)
                curveTo(15.503f, 15.2f, 15.1f, 15.603f, 15.1f, 16.1f)
                verticalLineTo(19.3f)
                curveTo(15.1f, 19.797f, 15.503f, 20.2f, 16.0f, 20.2f)
                curveTo(16.497f, 20.2f, 16.9f, 19.797f, 16.9f, 19.3f)
                verticalLineTo(16.1f)
                close()
            }
        }
        .build()
        return _about!!
    }

private var _about: ImageVector? = null
