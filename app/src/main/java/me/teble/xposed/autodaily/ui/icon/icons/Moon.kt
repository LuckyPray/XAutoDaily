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

public val Icons.Moon: ImageVector
    get() {
        if (_moon != null) {
            return _moon!!
        }
        _moon = Builder(
            name = "Moon", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
            viewportWidth = 24.0f, viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(12.0f, 19.448f)
                curveTo(15.362f, 19.448f, 18.215f, 17.225f, 19.164f, 14.171f)
                curveTo(18.464f, 14.354f, 17.738f, 14.448f, 17.0f, 14.448f)
                curveTo(12.313f, 14.448f, 8.5f, 10.635f, 8.5f, 5.948f)
                curveTo(8.5f, 5.731f, 8.508f, 5.515f, 8.524f, 5.301f)
                curveTo(7.697f, 5.735f, 6.95f, 6.323f, 6.327f, 7.042f)
                curveTo(5.149f, 8.403f, 4.5f, 10.145f, 4.5f, 11.948f)
                curveTo(4.5f, 16.084f, 7.865f, 19.448f, 12.0f, 19.448f)
                close()
                moveTo(12.0f, 20.948f)
                curveTo(7.037f, 20.948f, 3.0f, 16.911f, 3.0f, 11.948f)
                curveTo(3.0f, 9.785f, 3.779f, 7.694f, 5.193f, 6.06f)
                curveTo(6.594f, 4.443f, 8.523f, 3.375f, 10.625f, 3.053f)
                curveTo(10.211f, 3.964f, 10.0f, 4.937f, 10.0f, 5.948f)
                curveTo(10.0f, 9.808f, 13.14f, 12.948f, 17.0f, 12.948f)
                curveTo(18.438f, 12.948f, 19.82f, 12.515f, 20.997f, 11.696f)
                lineTo(20.999f, 11.769f)
                curveTo(20.999f, 11.829f, 21.0f, 11.889f, 21.0f, 11.948f)
                curveTo(21.0f, 16.911f, 16.963f, 20.948f, 12.0f, 20.948f)
                close()
            }
        }
            .build()
        return _moon!!
    }

private var _moon: ImageVector? = null
