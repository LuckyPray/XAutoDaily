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

public val Icons.Save: ImageVector
    get() {
        if (_save != null) {
            return _save!!
        }
        _save = Builder(name = "Save", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF3C4043)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(20.73f, 7.766f)
                lineTo(16.23f, 3.266f)
                curveTo(16.151f, 3.178f, 16.053f, 3.108f, 15.945f, 3.062f)
                curveTo(15.836f, 3.015f, 15.718f, 2.993f, 15.6f, 2.996f)
                horizontalLineTo(5.7f)
                curveTo(5.344f, 2.99f, 4.99f, 3.055f, 4.659f, 3.188f)
                curveTo(4.329f, 3.322f, 4.028f, 3.52f, 3.776f, 3.772f)
                curveTo(3.524f, 4.024f, 3.326f, 4.325f, 3.192f, 4.655f)
                curveTo(3.059f, 4.986f, 2.993f, 5.34f, 3.0f, 5.696f)
                verticalLineTo(18.296f)
                curveTo(2.993f, 18.652f, 3.059f, 19.006f, 3.192f, 19.337f)
                curveTo(3.326f, 19.667f, 3.524f, 19.968f, 3.776f, 20.22f)
                curveTo(4.028f, 20.472f, 4.329f, 20.67f, 4.659f, 20.804f)
                curveTo(4.99f, 20.937f, 5.344f, 21.003f, 5.7f, 20.996f)
                horizontalLineTo(18.3f)
                curveTo(18.656f, 21.003f, 19.01f, 20.937f, 19.341f, 20.804f)
                curveTo(19.671f, 20.67f, 19.972f, 20.472f, 20.224f, 20.22f)
                curveTo(20.476f, 19.968f, 20.674f, 19.667f, 20.808f, 19.337f)
                curveTo(20.941f, 19.006f, 21.007f, 18.652f, 21.0f, 18.296f)
                verticalLineTo(8.396f)
                curveTo(21.003f, 8.278f, 20.981f, 8.16f, 20.934f, 8.051f)
                curveTo(20.888f, 7.943f, 20.818f, 7.845f, 20.73f, 7.766f)
                close()
                moveTo(15.6f, 19.196f)
                horizontalLineTo(8.4f)
                verticalLineTo(13.796f)
                horizontalLineTo(15.6f)
                verticalLineTo(19.196f)
                close()
                moveTo(19.2f, 18.296f)
                curveTo(19.207f, 18.416f, 19.189f, 18.536f, 19.146f, 18.649f)
                curveTo(19.104f, 18.761f, 19.038f, 18.864f, 18.953f, 18.949f)
                curveTo(18.868f, 19.034f, 18.765f, 19.1f, 18.653f, 19.142f)
                curveTo(18.54f, 19.185f, 18.42f, 19.203f, 18.3f, 19.196f)
                horizontalLineTo(17.4f)
                verticalLineTo(12.896f)
                curveTo(17.407f, 12.776f, 17.389f, 12.656f, 17.346f, 12.543f)
                curveTo(17.304f, 12.431f, 17.238f, 12.328f, 17.153f, 12.243f)
                curveTo(17.067f, 12.158f, 16.965f, 12.092f, 16.853f, 12.05f)
                curveTo(16.74f, 12.007f, 16.62f, 11.989f, 16.5f, 11.996f)
                horizontalLineTo(7.5f)
                curveTo(7.38f, 11.989f, 7.26f, 12.007f, 7.147f, 12.05f)
                curveTo(7.035f, 12.092f, 6.932f, 12.158f, 6.847f, 12.243f)
                curveTo(6.762f, 12.328f, 6.696f, 12.431f, 6.654f, 12.543f)
                curveTo(6.611f, 12.656f, 6.593f, 12.776f, 6.6f, 12.896f)
                verticalLineTo(19.196f)
                horizontalLineTo(5.7f)
                curveTo(5.58f, 19.203f, 5.46f, 19.185f, 5.347f, 19.142f)
                curveTo(5.235f, 19.1f, 5.132f, 19.034f, 5.047f, 18.949f)
                curveTo(4.962f, 18.864f, 4.896f, 18.761f, 4.854f, 18.649f)
                curveTo(4.811f, 18.536f, 4.793f, 18.416f, 4.8f, 18.296f)
                verticalLineTo(5.696f)
                curveTo(4.793f, 5.576f, 4.811f, 5.456f, 4.854f, 5.343f)
                curveTo(4.896f, 5.231f, 4.962f, 5.129f, 5.047f, 5.043f)
                curveTo(5.132f, 4.958f, 5.235f, 4.892f, 5.347f, 4.85f)
                curveTo(5.46f, 4.807f, 5.58f, 4.789f, 5.7f, 4.796f)
                horizontalLineTo(6.6f)
                verticalLineTo(8.396f)
                curveTo(6.593f, 8.516f, 6.611f, 8.636f, 6.654f, 8.749f)
                curveTo(6.696f, 8.861f, 6.762f, 8.964f, 6.847f, 9.049f)
                curveTo(6.932f, 9.134f, 7.035f, 9.2f, 7.147f, 9.242f)
                curveTo(7.26f, 9.285f, 7.38f, 9.303f, 7.5f, 9.296f)
                horizontalLineTo(14.7f)
                curveTo(14.939f, 9.296f, 15.168f, 9.201f, 15.336f, 9.032f)
                curveTo(15.505f, 8.864f, 15.6f, 8.635f, 15.6f, 8.396f)
                curveTo(15.6f, 8.157f, 15.505f, 7.928f, 15.336f, 7.76f)
                curveTo(15.168f, 7.591f, 14.939f, 7.496f, 14.7f, 7.496f)
                horizontalLineTo(8.4f)
                verticalLineTo(4.796f)
                horizontalLineTo(15.24f)
                lineTo(19.2f, 8.756f)
                verticalLineTo(18.296f)
                close()
            }
        }
        .build()
        return _save!!
    }

private var _save: ImageVector? = null
