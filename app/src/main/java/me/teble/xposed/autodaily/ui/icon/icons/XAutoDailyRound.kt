package me.teble.xposed.autodaily.ui.icon.icons

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
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

public val Icons.XAutoDailyRound: ImageVector
    get() {
        if (_xAutoDailyRound != null) {
            return _xAutoDailyRound!!
        }
        _xAutoDailyRound = Builder(
            name = "XAutoDailyRound", defaultWidth = 80.0.dp, defaultHeight =
            80.0.dp, viewportWidth = 80.0f, viewportHeight = 80.0f
        ).apply {
            group {
                path(
                    fill = linearGradient(
                        0.0f to Color(0xFF00BFFF),
                        0.189f to Color(0xFF00BFFF),
                        0.191f to Color(0xFF44EFA8),
                        0.395f to Color(0xFF44EFA8),
                        0.395f to
                                Color(0xFFFFD900),
                        0.586f to Color(0xFFFFD900),
                        0.586f to Color(0xFFF5A61D),
                        0.809f to Color(0xFFF5A61D),
                        0.809f to Color(0xFFFE6565),
                        1.0f to
                                Color(0xFFFE6565),
                        start = Offset(69.92f, 12.32f),
                        end =
                        Offset(16.64f, 73.36f)
                    ), stroke = null, strokeLineWidth = 0.0f, strokeLineCap
                    = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType =
                    NonZero
                ) {
                    moveTo(40.0f, 40.0f)
                    moveToRelative(-40.0f, 0.0f)
                    arcToRelative(40.0f, 40.0f, 0.0f, true, true, 80.0f, 0.0f)
                    arcToRelative(40.0f, 40.0f, 0.0f, true, true, -80.0f, 0.0f)
                }
                path(
                    fill = SolidColor(Color(0xFFffffff)), stroke = null, fillAlpha = 0.36f,
                    strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero
                ) {
                    moveTo(40.0f, 65.0f)
                    curveTo(53.807f, 65.0f, 65.0f, 53.807f, 65.0f, 40.0f)
                    curveTo(65.0f, 26.193f, 53.807f, 15.0f, 40.0f, 15.0f)
                    curveTo(26.193f, 15.0f, 15.0f, 26.193f, 15.0f, 40.0f)
                    curveTo(15.0f, 53.807f, 26.193f, 65.0f, 40.0f, 65.0f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero
                ) {
                    moveTo(37.2f, 51.332f)
                    lineTo(55.554f, 32.977f)
                    curveTo(56.286f, 32.245f, 56.286f, 31.058f, 55.554f, 30.326f)
                    lineTo(53.897f, 28.668f)
                    curveTo(53.165f, 27.936f, 51.977f, 27.936f, 51.245f, 28.668f)
                    lineTo(32.891f, 47.023f)
                    curveTo(32.159f, 47.755f, 32.159f, 48.942f, 32.891f, 49.674f)
                    lineTo(34.548f, 51.332f)
                    curveTo(35.28f, 52.064f, 36.467f, 52.064f, 37.2f, 51.332f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero
                ) {
                    moveTo(38.857f, 47.023f)
                    lineTo(28.831f, 36.996f)
                    curveTo(28.098f, 36.264f, 26.911f, 36.264f, 26.179f, 36.996f)
                    lineTo(24.522f, 38.653f)
                    curveTo(23.789f, 39.386f, 23.789f, 40.573f, 24.522f, 41.305f)
                    lineTo(34.548f, 51.332f)
                    curveTo(35.281f, 52.064f, 36.468f, 52.064f, 37.2f, 51.332f)
                    lineTo(38.857f, 49.674f)
                    curveTo(39.59f, 48.942f, 39.59f, 47.755f, 38.857f, 47.023f)
                    close()
                }
            }
        }
            .build()
        return _xAutoDailyRound!!
    }

private var _xAutoDailyRound: ImageVector? = null
