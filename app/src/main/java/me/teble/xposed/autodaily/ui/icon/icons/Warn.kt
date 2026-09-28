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

public val Icons.Warn: ImageVector
    get() {
        if (_warn != null) {
            return _warn!!
        }
        _warn = Builder(
            name = "Warn", defaultWidth = 40.0.dp, defaultHeight = 41.0.dp,
            viewportWidth = 40.0f, viewportHeight = 41.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(17.0f, 7.2f)
                curveTo(17.0f, 6.537f, 17.537f, 6.0f, 18.2f, 6.0f)
                horizontalLineTo(21.6f)
                curveTo(22.263f, 6.0f, 22.8f, 6.537f, 22.8f, 7.2f)
                verticalLineTo(23.8f)
                curveTo(22.8f, 24.905f, 21.905f, 25.8f, 20.8f, 25.8f)
                horizontalLineTo(19.0f)
                curveTo(17.895f, 25.8f, 17.0f, 24.905f, 17.0f, 23.8f)
                verticalLineTo(7.2f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(20.0f, 32.0f)
                moveToRelative(-3.0f, 0.0f)
                arcToRelative(3.0f, 3.0f, 0.0f, true, true, 6.0f, 0.0f)
                arcToRelative(3.0f, 3.0f, 0.0f, true, true, -6.0f, 0.0f)
            }
        }
            .build()
        return _warn!!
    }

private var _warn: ImageVector? = null
