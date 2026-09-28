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

public val Icons.Chosen: ImageVector
    get() {
        if (_chosen != null) {
            return _chosen!!
        }
        _chosen = Builder(
            name = "Chosen", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
            viewportWidth = 24.0f, viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(9.269f, 17.153f)
                curveTo(9.226f, 17.109f, 9.185f, 17.063f, 9.148f, 17.015f)
                lineTo(4.425f, 12.293f)
                curveTo(4.153f, 12.021f, 4.001f, 11.652f, 4.001f, 11.268f)
                curveTo(4.001f, 10.883f, 4.153f, 10.515f, 4.425f, 10.243f)
                curveTo(4.697f, 9.971f, 5.066f, 9.818f, 5.45f, 9.818f)
                curveTo(5.834f, 9.818f, 6.203f, 9.971f, 6.475f, 10.243f)
                lineTo(10.302f, 14.07f)
                lineTo(17.526f, 6.846f)
                curveTo(17.798f, 6.574f, 18.167f, 6.421f, 18.551f, 6.421f)
                curveTo(18.935f, 6.421f, 19.304f, 6.574f, 19.576f, 6.846f)
                curveTo(19.848f, 7.118f, 20.0f, 7.486f, 20.0f, 7.871f)
                curveTo(20.0f, 8.255f, 19.848f, 8.624f, 19.576f, 8.896f)
                lineTo(11.319f, 17.154f)
                curveTo(11.184f, 17.289f, 11.025f, 17.396f, 10.849f, 17.469f)
                curveTo(10.673f, 17.541f, 10.484f, 17.579f, 10.294f, 17.579f)
                curveTo(10.103f, 17.579f, 9.915f, 17.541f, 9.739f, 17.468f)
                curveTo(9.563f, 17.395f, 9.403f, 17.288f, 9.269f, 17.153f)
                close()
            }
        }
            .build()
        return _chosen!!
    }

private var _chosen: ImageVector? = null
