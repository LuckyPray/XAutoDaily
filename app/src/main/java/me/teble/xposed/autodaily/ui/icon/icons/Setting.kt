package me.teble.xposed.autodaily.ui.icon.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.teble.xposed.autodaily.ui.icon.Icons


public val Icons.Setting: ImageVector
    get() {
        if (_setting != null) {
            return _setting!!
        }
        _setting = Builder(name = "Setting", defaultWidth = 32.0.dp, defaultHeight = 32.0.dp,
                viewportWidth = 32.0f, viewportHeight = 32.0f).apply {
            path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(19.843f, 9.27f)
                curveTo(19.539f, 9.093f, 19.192f, 9.0f, 18.84f, 9.0f)
                horizontalLineTo(13.162f)
                curveTo(12.809f, 9.0f, 12.463f, 9.093f, 12.158f, 9.27f)
                curveTo(11.854f, 9.447f, 11.601f, 9.701f, 11.426f, 10.007f)
                lineTo(8.569f, 15.007f)
                curveTo(8.396f, 15.309f, 8.305f, 15.651f, 8.305f, 16.0f)
                curveTo(8.305f, 16.348f, 8.396f, 16.69f, 8.569f, 16.992f)
                lineTo(11.426f, 21.992f)
                curveTo(11.601f, 22.298f, 11.853f, 22.553f, 12.158f, 22.73f)
                curveTo(12.463f, 22.907f, 12.809f, 23.0f, 13.162f, 23.0f)
                horizontalLineTo(18.84f)
                curveTo(19.192f, 23.0f, 19.538f, 22.907f, 19.843f, 22.73f)
                curveTo(20.148f, 22.553f, 20.401f, 22.299f, 20.576f, 21.993f)
                lineTo(23.433f, 16.993f)
                curveTo(23.606f, 16.691f, 23.697f, 16.349f, 23.697f, 16.0f)
                curveTo(23.697f, 15.652f, 23.606f, 15.31f, 23.433f, 15.008f)
                lineTo(20.576f, 10.008f)
                curveTo(20.401f, 9.702f, 20.148f, 9.447f, 19.843f, 9.27f)
                close()
                moveTo(16.001f, 19.75f)
                curveTo(18.072f, 19.75f, 19.751f, 18.071f, 19.751f, 16.0f)
                curveTo(19.751f, 13.929f, 18.072f, 12.25f, 16.001f, 12.25f)
                curveTo(13.93f, 12.25f, 12.251f, 13.929f, 12.251f, 16.0f)
                curveTo(12.251f, 18.071f, 13.93f, 19.75f, 16.001f, 19.75f)
                close()
                moveTo(16.001f, 18.0f)
                curveTo(17.105f, 18.0f, 18.001f, 17.105f, 18.001f, 16.0f)
                curveTo(18.001f, 14.895f, 17.105f, 14.0f, 16.001f, 14.0f)
                curveTo(14.896f, 14.0f, 14.001f, 14.895f, 14.001f, 16.0f)
                curveTo(14.001f, 17.105f, 14.896f, 18.0f, 16.001f, 18.0f)
                close()
            }
        }
        .build()
        return _setting!!
    }

private var _setting: ImageVector? = null

