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

public val Icons.ChevronDown: ImageVector
    get() {
        if (_chevronDown != null) {
            return _chevronDown!!
        }
        _chevronDown = Builder(
            name = "ChevronDown", defaultWidth = 24.0.dp, defaultHeight =
            24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(12.814f, 14.861f)
                curveTo(12.722f, 14.99f, 12.599f, 15.096f, 12.458f, 15.169f)
                curveTo(12.316f, 15.241f, 12.16f, 15.279f, 12.0f, 15.279f)
                curveTo(11.841f, 15.279f, 11.685f, 15.241f, 11.543f, 15.169f)
                curveTo(11.402f, 15.096f, 11.28f, 14.99f, 11.187f, 14.861f)
                lineTo(8.13f, 10.581f)
                curveTo(8.023f, 10.431f, 7.96f, 10.255f, 7.947f, 10.072f)
                curveTo(7.933f, 9.889f, 7.971f, 9.706f, 8.055f, 9.543f)
                curveTo(8.139f, 9.379f, 8.266f, 9.242f, 8.423f, 9.146f)
                curveTo(8.579f, 9.051f, 8.759f, 9.0f, 8.943f, 9.0f)
                horizontalLineTo(15.057f)
                curveTo(15.241f, 9.0f, 15.421f, 9.05f, 15.578f, 9.146f)
                curveTo(15.735f, 9.242f, 15.862f, 9.379f, 15.946f, 9.542f)
                curveTo(16.03f, 9.705f, 16.068f, 9.889f, 16.055f, 10.072f)
                curveTo(16.041f, 10.255f, 15.978f, 10.431f, 15.871f, 10.581f)
                lineTo(12.814f, 14.861f)
                close()
            }
        }
            .build()
        return _chevronDown!!
    }

private var _chevronDown: ImageVector? = null
