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

public val Icons.Android: ImageVector
    get() {
        if (_android != null) {
            return _android!!
        }
        _android = Builder(
            name = "Android", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
            viewportWidth = 24.0f, viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(19.984f, 17.602f)
                curveTo(19.995f, 17.437f, 20.001f, 17.269f, 20.001f, 17.102f)
                curveTo(20.001f, 12.966f, 16.412f, 9.602f, 12.001f, 9.602f)
                curveTo(7.59f, 9.602f, 4.001f, 12.966f, 4.001f, 17.102f)
                curveTo(4.001f, 17.264f, 4.006f, 17.432f, 4.018f, 17.602f)
                horizontalLineTo(19.984f)
                close()
                moveTo(19.984f, 19.102f)
                horizontalLineTo(4.018f)
                curveTo(3.231f, 19.102f, 2.578f, 18.493f, 2.522f, 17.707f)
                curveTo(2.508f, 17.508f, 2.501f, 17.304f, 2.501f, 17.102f)
                curveTo(2.501f, 12.139f, 6.763f, 8.102f, 12.001f, 8.102f)
                curveTo(17.239f, 8.102f, 21.501f, 12.139f, 21.501f, 17.102f)
                curveTo(21.501f, 17.299f, 21.494f, 17.498f, 21.481f, 17.693f)
                curveTo(21.433f, 18.479f, 20.781f, 19.102f, 19.984f, 19.102f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(5.676f, 5.4f)
                curveTo(5.779f, 5.341f, 5.893f, 5.302f, 6.01f, 5.287f)
                curveTo(6.128f, 5.272f, 6.248f, 5.28f, 6.363f, 5.311f)
                curveTo(6.477f, 5.343f, 6.585f, 5.396f, 6.678f, 5.469f)
                curveTo(6.772f, 5.542f, 6.851f, 5.633f, 6.909f, 5.736f)
                lineTo(8.717f, 8.9f)
                curveTo(8.776f, 9.003f, 8.814f, 9.117f, 8.83f, 9.235f)
                curveTo(8.845f, 9.353f, 8.836f, 9.472f, 8.805f, 9.587f)
                curveTo(8.774f, 9.702f, 8.721f, 9.809f, 8.648f, 9.903f)
                curveTo(8.576f, 9.997f, 8.485f, 10.076f, 8.382f, 10.135f)
                curveTo(8.279f, 10.194f, 8.165f, 10.233f, 8.047f, 10.248f)
                curveTo(7.93f, 10.263f, 7.81f, 10.255f, 7.695f, 10.224f)
                curveTo(7.581f, 10.192f, 7.473f, 10.139f, 7.379f, 10.066f)
                curveTo(7.285f, 9.994f, 7.206f, 9.903f, 7.147f, 9.8f)
                lineTo(5.339f, 6.636f)
                curveTo(5.279f, 6.533f, 5.241f, 6.419f, 5.225f, 6.301f)
                curveTo(5.21f, 6.183f, 5.218f, 6.063f, 5.249f, 5.948f)
                curveTo(5.281f, 5.833f, 5.334f, 5.725f, 5.408f, 5.631f)
                curveTo(5.481f, 5.537f, 5.572f, 5.459f, 5.676f, 5.4f)
                close()
                moveTo(18.326f, 5.4f)
                curveTo(18.223f, 5.341f, 18.11f, 5.302f, 17.992f, 5.287f)
                curveTo(17.874f, 5.272f, 17.754f, 5.28f, 17.64f, 5.311f)
                curveTo(17.525f, 5.343f, 17.418f, 5.396f, 17.324f, 5.469f)
                curveTo(17.23f, 5.542f, 17.152f, 5.633f, 17.093f, 5.736f)
                lineTo(15.285f, 8.9f)
                curveTo(15.226f, 9.003f, 15.188f, 9.117f, 15.173f, 9.235f)
                curveTo(15.158f, 9.353f, 15.166f, 9.472f, 15.197f, 9.587f)
                curveTo(15.228f, 9.702f, 15.281f, 9.809f, 15.354f, 9.903f)
                curveTo(15.427f, 9.997f, 15.517f, 10.076f, 15.62f, 10.135f)
                curveTo(15.723f, 10.194f, 15.837f, 10.233f, 15.955f, 10.248f)
                curveTo(16.073f, 10.263f, 16.192f, 10.255f, 16.307f, 10.224f)
                curveTo(16.422f, 10.192f, 16.529f, 10.139f, 16.623f, 10.066f)
                curveTo(16.717f, 9.994f, 16.796f, 9.903f, 16.855f, 9.8f)
                lineTo(18.663f, 6.636f)
                curveTo(18.723f, 6.533f, 18.762f, 6.419f, 18.777f, 6.301f)
                curveTo(18.792f, 6.183f, 18.784f, 6.063f, 18.753f, 5.948f)
                curveTo(18.722f, 5.833f, 18.668f, 5.725f, 18.594f, 5.631f)
                curveTo(18.521f, 5.537f, 18.43f, 5.459f, 18.326f, 5.4f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(8.501f, 15.276f)
                curveTo(9.053f, 15.276f, 9.501f, 14.829f, 9.501f, 14.276f)
                curveTo(9.501f, 13.724f, 9.053f, 13.276f, 8.501f, 13.276f)
                curveTo(7.949f, 13.276f, 7.501f, 13.724f, 7.501f, 14.276f)
                curveTo(7.501f, 14.829f, 7.949f, 15.276f, 8.501f, 15.276f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(15.501f, 15.276f)
                curveTo(16.053f, 15.276f, 16.501f, 14.829f, 16.501f, 14.276f)
                curveTo(16.501f, 13.724f, 16.053f, 13.276f, 15.501f, 13.276f)
                curveTo(14.949f, 13.276f, 14.501f, 13.724f, 14.501f, 14.276f)
                curveTo(14.501f, 14.829f, 14.949f, 15.276f, 15.501f, 15.276f)
                close()
            }
        }
            .build()
        return _android!!
    }

private var _android: ImageVector? = null
