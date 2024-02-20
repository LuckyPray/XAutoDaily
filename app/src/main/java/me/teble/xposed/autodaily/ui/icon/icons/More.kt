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

public val Icons.More: ImageVector
    get() {
        if (_more != null) {
            return _more!!
        }
        _more = Builder(
            name = "More", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
            viewportWidth = 24.0f, viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF202124)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(12.0f, 8.0f)
                curveTo(12.396f, 8.0f, 12.782f, 7.883f, 13.111f, 7.663f)
                curveTo(13.44f, 7.443f, 13.696f, 7.131f, 13.848f, 6.765f)
                curveTo(13.999f, 6.4f, 14.039f, 5.998f, 13.962f, 5.61f)
                curveTo(13.884f, 5.222f, 13.694f, 4.865f, 13.414f, 4.586f)
                curveTo(13.134f, 4.306f, 12.778f, 4.116f, 12.39f, 4.038f)
                curveTo(12.002f, 3.961f, 11.6f, 4.001f, 11.235f, 4.152f)
                curveTo(10.869f, 4.304f, 10.557f, 4.56f, 10.337f, 4.889f)
                curveTo(10.117f, 5.218f, 10.0f, 5.604f, 10.0f, 6.0f)
                curveTo(10.002f, 6.53f, 10.213f, 7.038f, 10.587f, 7.412f)
                curveTo(10.962f, 7.787f, 11.47f, 7.998f, 12.0f, 8.0f)
                close()
                moveTo(12.0f, 10.0f)
                curveTo(11.604f, 10.0f, 11.218f, 10.117f, 10.889f, 10.337f)
                curveTo(10.56f, 10.557f, 10.304f, 10.869f, 10.152f, 11.235f)
                curveTo(10.001f, 11.6f, 9.961f, 12.002f, 10.038f, 12.39f)
                curveTo(10.116f, 12.778f, 10.306f, 13.134f, 10.586f, 13.414f)
                curveTo(10.866f, 13.694f, 11.222f, 13.884f, 11.61f, 13.962f)
                curveTo(11.998f, 14.039f, 12.4f, 13.999f, 12.765f, 13.848f)
                curveTo(13.131f, 13.696f, 13.443f, 13.44f, 13.663f, 13.111f)
                curveTo(13.883f, 12.782f, 14.0f, 12.396f, 14.0f, 12.0f)
                curveTo(13.998f, 11.47f, 13.787f, 10.962f, 13.413f, 10.587f)
                curveTo(13.038f, 10.213f, 12.53f, 10.002f, 12.0f, 10.0f)
                close()
                moveTo(12.0f, 16.0f)
                curveTo(11.604f, 16.0f, 11.218f, 16.117f, 10.889f, 16.337f)
                curveTo(10.56f, 16.557f, 10.304f, 16.869f, 10.152f, 17.235f)
                curveTo(10.001f, 17.6f, 9.961f, 18.002f, 10.038f, 18.39f)
                curveTo(10.116f, 18.778f, 10.306f, 19.135f, 10.586f, 19.414f)
                curveTo(10.866f, 19.694f, 11.222f, 19.884f, 11.61f, 19.962f)
                curveTo(11.998f, 20.039f, 12.4f, 19.999f, 12.765f, 19.848f)
                curveTo(13.131f, 19.696f, 13.443f, 19.44f, 13.663f, 19.111f)
                curveTo(13.883f, 18.782f, 14.0f, 18.396f, 14.0f, 18.0f)
                curveTo(13.998f, 17.47f, 13.787f, 16.962f, 13.413f, 16.587f)
                curveTo(13.038f, 16.213f, 12.53f, 16.002f, 12.0f, 16.0f)
                close()
            }
        }
            .build()
        return _more!!
    }

private var _more: ImageVector? = null
