package me.teble.xposed.autodaily.ui.icon.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import me.teble.xposed.autodaily.ui.icon.Icons

public val Icons.Back: ImageVector
    get() {
        if (_back != null) {
            return _back!!
        }
        _back = Builder(name = "Back", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            group {
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(21.457f, 11.1f)
                    horizontalLineTo(3.257f)
                    curveTo(2.76f, 11.1f, 2.357f, 11.503f, 2.357f, 12.0f)
                    curveTo(2.357f, 12.497f, 2.76f, 12.9f, 3.257f, 12.9f)
                    horizontalLineTo(21.457f)
                    curveTo(21.954f, 12.9f, 22.357f, 12.497f, 22.357f, 12.0f)
                    curveTo(22.357f, 11.503f, 21.954f, 11.1f, 21.457f, 11.1f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(10.554f, 5.415f)
                    curveTo(10.866f, 5.103f, 10.866f, 4.596f, 10.554f, 4.284f)
                    curveTo(10.241f, 3.971f, 9.735f, 3.971f, 9.422f, 4.284f)
                    lineTo(2.351f, 11.355f)
                    curveTo(2.039f, 11.667f, 2.039f, 12.174f, 2.351f, 12.486f)
                    curveTo(2.664f, 12.799f, 3.17f, 12.799f, 3.483f, 12.486f)
                    lineTo(10.554f, 5.415f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(9.281f, 19.718f)
                    curveTo(9.593f, 20.03f, 10.1f, 20.03f, 10.412f, 19.718f)
                    curveTo(10.725f, 19.405f, 10.725f, 18.899f, 10.412f, 18.586f)
                    lineTo(3.341f, 11.515f)
                    curveTo(3.029f, 11.203f, 2.522f, 11.203f, 2.21f, 11.515f)
                    curveTo(1.897f, 11.828f, 1.897f, 12.334f, 2.21f, 12.647f)
                    lineTo(9.281f, 19.718f)
                    close()
                }
            }
        }
        .build()
        return _back!!
    }

private var _back: ImageVector? = null
