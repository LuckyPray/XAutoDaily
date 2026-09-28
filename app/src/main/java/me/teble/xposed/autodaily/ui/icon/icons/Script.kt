package me.teble.xposed.autodaily.ui.icon.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import me.teble.xposed.autodaily.ui.icon.Icons

public val Icons.Script: ImageVector
    get() {
        if (_script != null) {
            return _script!!
        }
        _script = Builder(name = "Script", defaultWidth = 32.0.dp, defaultHeight = 32.0.dp,
                viewportWidth = 32.0f, viewportHeight = 32.0f).apply {
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = EvenOdd
            ) {
                moveTo(22.65f, 13.473f)
                curveTo(23.04f, 13.083f, 23.04f, 12.45f, 22.65f, 12.059f)
                lineTo(19.883f, 9.293f)
                curveTo(19.493f, 8.902f, 18.86f, 8.902f, 18.469f, 9.293f)
                lineTo(9.281f, 18.481f)
                curveTo(9.273f, 18.489f, 9.265f, 18.497f, 9.258f, 18.506f)
                curveTo(9.154f, 18.629f, 9.0f, 18.797f, 9.0f, 18.959f)
                verticalLineTo(21.941f)
                curveTo(9.0f, 22.493f, 9.448f, 22.941f, 10.0f, 22.941f)
                horizontalLineTo(12.982f)
                curveTo(13.142f, 22.941f, 13.31f, 22.789f, 13.434f, 22.687f)
                curveTo(13.443f, 22.679f, 13.453f, 22.67f, 13.462f, 22.661f)
                lineTo(22.65f, 13.473f)
                close()
            }
        }
        .build()
        return _script!!
    }

private var _script: ImageVector? = null
