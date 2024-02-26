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

public val Icons.Delete: ImageVector
    get() {
        if (_delete != null) {
            return _delete!!
        }
        _delete = Builder(
            name = "Delete", defaultWidth = 14.0.dp, defaultHeight = 14.0.dp,
            viewportWidth = 14.0f, viewportHeight = 14.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(11.568f, 3.45f)
                horizontalLineTo(9.538f)
                verticalLineTo(2.94f)
                curveTo(9.541f, 2.739f, 9.504f, 2.54f, 9.428f, 2.354f)
                curveTo(9.353f, 2.168f, 9.241f, 1.999f, 9.099f, 1.857f)
                curveTo(8.957f, 1.715f, 8.788f, 1.603f, 8.602f, 1.528f)
                curveTo(8.416f, 1.453f, 8.217f, 1.415f, 8.016f, 1.418f)
                horizontalLineTo(5.986f)
                curveTo(5.785f, 1.415f, 5.586f, 1.453f, 5.4f, 1.528f)
                curveTo(5.214f, 1.603f, 5.045f, 1.715f, 4.903f, 1.857f)
                curveTo(4.761f, 1.999f, 4.649f, 2.168f, 4.574f, 2.354f)
                curveTo(4.498f, 2.54f, 4.461f, 2.739f, 4.464f, 2.94f)
                verticalLineTo(3.45f)
                horizontalLineTo(2.434f)
                curveTo(2.299f, 3.45f, 2.17f, 3.504f, 2.075f, 3.599f)
                curveTo(1.98f, 3.694f, 1.927f, 3.823f, 1.927f, 3.958f)
                curveTo(1.927f, 4.092f, 1.98f, 4.221f, 2.075f, 4.316f)
                curveTo(2.17f, 4.412f, 2.299f, 4.465f, 2.434f, 4.465f)
                horizontalLineTo(2.941f)
                verticalLineTo(11.065f)
                curveTo(2.938f, 11.266f, 2.975f, 11.465f, 3.051f, 11.651f)
                curveTo(3.126f, 11.837f, 3.238f, 12.006f, 3.38f, 12.148f)
                curveTo(3.522f, 12.29f, 3.691f, 12.402f, 3.877f, 12.477f)
                curveTo(4.063f, 12.553f, 4.262f, 12.59f, 4.463f, 12.587f)
                horizontalLineTo(9.538f)
                curveTo(9.739f, 12.59f, 9.938f, 12.553f, 10.124f, 12.478f)
                curveTo(10.31f, 12.402f, 10.479f, 12.29f, 10.622f, 12.148f)
                curveTo(10.764f, 12.006f, 10.876f, 11.837f, 10.951f, 11.651f)
                curveTo(11.027f, 11.465f, 11.064f, 11.266f, 11.061f, 11.065f)
                verticalLineTo(4.465f)
                horizontalLineTo(11.568f)
                curveTo(11.703f, 4.465f, 11.832f, 4.412f, 11.927f, 4.316f)
                curveTo(12.022f, 4.221f, 12.075f, 4.092f, 12.075f, 3.958f)
                curveTo(12.075f, 3.823f, 12.022f, 3.694f, 11.927f, 3.599f)
                curveTo(11.832f, 3.504f, 11.703f, 3.45f, 11.568f, 3.45f)
                close()
                moveTo(5.478f, 2.943f)
                curveTo(5.474f, 2.875f, 5.484f, 2.808f, 5.508f, 2.744f)
                curveTo(5.532f, 2.681f, 5.57f, 2.623f, 5.618f, 2.575f)
                curveTo(5.666f, 2.527f, 5.723f, 2.49f, 5.787f, 2.466f)
                curveTo(5.85f, 2.442f, 5.918f, 2.432f, 5.986f, 2.436f)
                horizontalLineTo(8.016f)
                curveTo(8.084f, 2.432f, 8.151f, 2.442f, 8.215f, 2.466f)
                curveTo(8.278f, 2.49f, 8.336f, 2.528f, 8.384f, 2.576f)
                curveTo(8.432f, 2.623f, 8.469f, 2.681f, 8.493f, 2.744f)
                curveTo(8.517f, 2.808f, 8.527f, 2.875f, 8.523f, 2.943f)
                verticalLineTo(3.45f)
                horizontalLineTo(5.478f)
                verticalLineTo(2.943f)
                close()
                moveTo(10.045f, 11.063f)
                curveTo(10.049f, 11.131f, 10.039f, 11.199f, 10.015f, 11.262f)
                curveTo(9.991f, 11.326f, 9.953f, 11.383f, 9.905f, 11.431f)
                curveTo(9.857f, 11.479f, 9.8f, 11.516f, 9.736f, 11.54f)
                curveTo(9.673f, 11.564f, 9.605f, 11.574f, 9.537f, 11.57f)
                horizontalLineTo(4.463f)
                curveTo(4.395f, 11.574f, 4.327f, 11.564f, 4.264f, 11.54f)
                curveTo(4.2f, 11.516f, 4.143f, 11.479f, 4.095f, 11.431f)
                curveTo(4.047f, 11.383f, 4.009f, 11.326f, 3.985f, 11.262f)
                curveTo(3.961f, 11.199f, 3.951f, 11.131f, 3.955f, 11.063f)
                verticalLineTo(4.463f)
                horizontalLineTo(10.045f)
                verticalLineTo(11.063f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(5.985f, 5.985f)
                curveTo(5.917f, 5.981f, 5.849f, 5.992f, 5.786f, 6.016f)
                curveTo(5.723f, 6.04f, 5.665f, 6.077f, 5.617f, 6.125f)
                curveTo(5.569f, 6.173f, 5.532f, 6.231f, 5.508f, 6.294f)
                curveTo(5.484f, 6.358f, 5.474f, 6.426f, 5.478f, 6.493f)
                verticalLineTo(9.538f)
                curveTo(5.478f, 9.673f, 5.531f, 9.802f, 5.627f, 9.897f)
                curveTo(5.722f, 9.992f, 5.851f, 10.046f, 5.985f, 10.046f)
                curveTo(6.12f, 10.046f, 6.249f, 9.992f, 6.344f, 9.897f)
                curveTo(6.44f, 9.802f, 6.493f, 9.673f, 6.493f, 9.538f)
                verticalLineTo(6.493f)
                curveTo(6.497f, 6.425f, 6.487f, 6.358f, 6.463f, 6.294f)
                curveTo(6.439f, 6.23f, 6.402f, 6.173f, 6.354f, 6.125f)
                curveTo(6.306f, 6.077f, 6.248f, 6.039f, 6.184f, 6.015f)
                curveTo(6.121f, 5.991f, 6.053f, 5.981f, 5.985f, 5.985f)
                close()
                moveTo(8.015f, 5.985f)
                curveTo(7.947f, 5.981f, 7.879f, 5.992f, 7.816f, 6.016f)
                curveTo(7.753f, 6.04f, 7.695f, 6.077f, 7.647f, 6.125f)
                curveTo(7.599f, 6.173f, 7.562f, 6.231f, 7.538f, 6.294f)
                curveTo(7.514f, 6.358f, 7.504f, 6.426f, 7.508f, 6.493f)
                verticalLineTo(9.538f)
                curveTo(7.508f, 9.673f, 7.561f, 9.802f, 7.657f, 9.897f)
                curveTo(7.752f, 9.992f, 7.881f, 10.046f, 8.015f, 10.046f)
                curveTo(8.15f, 10.046f, 8.279f, 9.992f, 8.374f, 9.897f)
                curveTo(8.47f, 9.802f, 8.523f, 9.673f, 8.523f, 9.538f)
                verticalLineTo(6.493f)
                curveTo(8.527f, 6.425f, 8.517f, 6.358f, 8.493f, 6.294f)
                curveTo(8.469f, 6.23f, 8.432f, 6.173f, 8.384f, 6.125f)
                curveTo(8.336f, 6.077f, 8.278f, 6.039f, 8.214f, 6.015f)
                curveTo(8.151f, 5.991f, 8.083f, 5.981f, 8.015f, 5.985f)
                close()
            }
        }
            .build()
        return _delete!!
    }

private var _delete: ImageVector? = null
