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

public val Icons.ChevronRight: ImageVector
    get() {
        if (_chevronRight != null) {
            return _chevronRight!!
        }
        _chevronRight = Builder(name = "ChevronRight", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(15.172f, 11.4f)
                lineTo(10.029f, 6.257f)
                curveTo(9.868f, 6.107f, 9.655f, 6.024f, 9.435f, 6.028f)
                curveTo(9.215f, 6.032f, 9.004f, 6.121f, 8.849f, 6.277f)
                curveTo(8.693f, 6.432f, 8.604f, 6.643f, 8.6f, 6.863f)
                curveTo(8.596f, 7.083f, 8.679f, 7.296f, 8.829f, 7.457f)
                lineTo(13.372f, 12.0f)
                lineTo(8.829f, 16.543f)
                curveTo(8.748f, 16.62f, 8.683f, 16.713f, 8.639f, 16.817f)
                curveTo(8.595f, 16.92f, 8.572f, 17.031f, 8.572f, 17.143f)
                curveTo(8.572f, 17.255f, 8.595f, 17.366f, 8.639f, 17.469f)
                curveTo(8.683f, 17.573f, 8.748f, 17.666f, 8.829f, 17.743f)
                curveTo(8.906f, 17.824f, 9.0f, 17.889f, 9.103f, 17.933f)
                curveTo(9.206f, 17.977f, 9.317f, 18.0f, 9.429f, 18.0f)
                curveTo(9.541f, 18.0f, 9.652f, 17.977f, 9.755f, 17.933f)
                curveTo(9.858f, 17.889f, 9.952f, 17.824f, 10.029f, 17.743f)
                lineTo(15.172f, 12.6f)
                curveTo(15.253f, 12.523f, 15.318f, 12.429f, 15.362f, 12.326f)
                curveTo(15.406f, 12.223f, 15.429f, 12.112f, 15.429f, 12.0f)
                curveTo(15.429f, 11.888f, 15.406f, 11.777f, 15.362f, 11.674f)
                curveTo(15.318f, 11.571f, 15.253f, 11.477f, 15.172f, 11.4f)
                close()
            }
        }
        .build()
        return _chevronRight!!
    }

private var _chevronRight: ImageVector? = null
