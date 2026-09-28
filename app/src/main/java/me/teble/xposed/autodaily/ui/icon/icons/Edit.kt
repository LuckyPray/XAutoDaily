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

public val Icons.Edit: ImageVector
    get() {
        if (_edit != null) {
            return _edit!!
        }
        _edit = Builder(name = "Edit", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(17.818f, 13.236f)
                curveTo(17.382f, 13.236f, 17.091f, 13.527f, 17.091f, 13.964f)
                verticalLineTo(17.818f)
                curveTo(17.091f, 18.255f, 16.8f, 18.545f, 16.364f, 18.545f)
                horizontalLineTo(6.182f)
                curveTo(5.745f, 18.545f, 5.455f, 18.255f, 5.455f, 17.818f)
                verticalLineTo(7.636f)
                curveTo(5.455f, 7.2f, 5.745f, 6.909f, 6.182f, 6.909f)
                horizontalLineTo(10.036f)
                curveTo(10.473f, 6.909f, 10.764f, 6.618f, 10.764f, 6.182f)
                curveTo(10.764f, 5.746f, 10.473f, 5.455f, 10.036f, 5.455f)
                horizontalLineTo(6.182f)
                curveTo(4.945f, 5.455f, 4.0f, 6.4f, 4.0f, 7.636f)
                verticalLineTo(17.818f)
                curveTo(4.0f, 19.055f, 4.945f, 20.0f, 6.182f, 20.0f)
                horizontalLineTo(16.364f)
                curveTo(17.6f, 20.0f, 18.545f, 19.055f, 18.545f, 17.818f)
                verticalLineTo(13.964f)
                curveTo(18.545f, 13.527f, 18.254f, 13.236f, 17.818f, 13.236f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(19.782f, 7.127f)
                lineTo(16.873f, 4.218f)
                curveTo(16.582f, 3.927f, 16.146f, 3.927f, 15.855f, 4.218f)
                lineTo(8.582f, 11.491f)
                curveTo(8.436f, 11.636f, 8.364f, 11.782f, 8.364f, 12.0f)
                verticalLineTo(14.909f)
                curveTo(8.364f, 15.345f, 8.655f, 15.636f, 9.091f, 15.636f)
                horizontalLineTo(12.0f)
                curveTo(12.218f, 15.636f, 12.364f, 15.564f, 12.509f, 15.418f)
                lineTo(19.782f, 8.145f)
                curveTo(20.073f, 7.855f, 20.073f, 7.418f, 19.782f, 7.127f)
                close()
                moveTo(11.709f, 14.182f)
                horizontalLineTo(9.818f)
                verticalLineTo(12.291f)
                lineTo(16.364f, 5.745f)
                lineTo(18.255f, 7.636f)
                lineTo(11.709f, 14.182f)
                close()
            }
        }
        .build()
        return _edit!!
    }

private var _edit: ImageVector? = null
