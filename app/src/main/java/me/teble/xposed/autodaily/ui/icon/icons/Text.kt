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

public val Icons.Text: ImageVector
    get() {
        if (_text != null) {
            return _text!!
        }
        _text = Builder(
            name = "Text", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
            viewportWidth = 24.0f, viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(8.849f, 15.987f)
                lineTo(7.958f, 18.436f)
                curveTo(7.907f, 18.606f, 7.797f, 18.752f, 7.647f, 18.848f)
                curveTo(7.497f, 18.943f, 7.318f, 18.982f, 7.142f, 18.956f)
                curveTo(7.054f, 18.969f, 6.963f, 18.965f, 6.877f, 18.941f)
                curveTo(6.791f, 18.919f, 6.71f, 18.878f, 6.64f, 18.822f)
                curveTo(6.571f, 18.766f, 6.513f, 18.696f, 6.472f, 18.617f)
                curveTo(6.43f, 18.538f, 6.406f, 18.451f, 6.4f, 18.362f)
                curveTo(6.41f, 18.076f, 6.486f, 17.797f, 6.623f, 17.546f)
                lineTo(10.779f, 6.339f)
                curveTo(11.026f, 5.646f, 11.422f, 5.3f, 11.966f, 5.3f)
                curveTo(12.256f, 5.3f, 12.536f, 5.405f, 12.755f, 5.597f)
                curveTo(12.973f, 5.788f, 13.115f, 6.051f, 13.154f, 6.339f)
                lineTo(17.384f, 17.546f)
                curveTo(17.52f, 17.797f, 17.597f, 18.076f, 17.607f, 18.362f)
                curveTo(17.607f, 18.758f, 17.335f, 18.956f, 16.79f, 18.956f)
                curveTo(16.616f, 18.968f, 16.443f, 18.924f, 16.296f, 18.83f)
                curveTo(16.149f, 18.737f, 16.036f, 18.598f, 15.974f, 18.436f)
                lineTo(15.157f, 15.987f)
                horizontalLineTo(8.849f)
                close()
                moveTo(14.638f, 14.577f)
                lineTo(12.038f, 7.229f)
                horizontalLineTo(11.966f)
                lineTo(9.366f, 14.577f)
                horizontalLineTo(14.638f)
                close()
            }
        }
            .build()
        return _text!!
    }

private var _text: ImageVector? = null
