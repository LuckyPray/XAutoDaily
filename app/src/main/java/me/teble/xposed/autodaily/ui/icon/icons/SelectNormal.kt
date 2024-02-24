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

public val Icons.SelectNormal: ImageVector
    get() {
        if (_selectNormal != null) {
            return _selectNormal!!
        }
        _selectNormal = Builder(
            name = "SelectNormal", defaultWidth = 24.0.dp, defaultHeight =
            24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(12.0f, 3.0f)
                curveTo(7.009f, 3.0f, 3.0f, 7.009f, 3.0f, 12.0f)
                curveTo(3.0f, 16.991f, 7.009f, 21.0f, 12.0f, 21.0f)
                curveTo(16.991f, 21.0f, 21.0f, 16.991f, 21.0f, 12.0f)
                curveTo(21.0f, 7.009f, 16.991f, 3.0f, 12.0f, 3.0f)
                close()
                moveTo(12.0f, 19.364f)
                curveTo(7.909f, 19.364f, 4.636f, 16.091f, 4.636f, 12.0f)
                curveTo(4.636f, 7.909f, 7.909f, 4.636f, 12.0f, 4.636f)
                curveTo(16.091f, 4.636f, 19.364f, 7.909f, 19.364f, 12.0f)
                curveTo(19.364f, 16.091f, 16.091f, 19.364f, 12.0f, 19.364f)
                close()
            }
        }
            .build()
        return _selectNormal!!
    }

private var _selectNormal: ImageVector? = null
