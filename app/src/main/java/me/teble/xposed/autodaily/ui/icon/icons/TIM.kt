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

public val Icons.TIM: ImageVector
    get() {
        if (_tIM != null) {
            return _tIM!!
        }
        _tIM = Builder(
            name = "TIM", defaultWidth = 40.0.dp, defaultHeight = 40.0.dp, viewportWidth
            = 40.0f, viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF0188FB)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(26.376f, 30.02f)
                horizontalLineTo(25.711f)
                verticalLineTo(36.0f)
                lineTo(18.188f, 30.02f)
                horizontalLineTo(12.644f)
                curveTo(10.882f, 30.02f, 9.192f, 29.32f, 7.946f, 28.074f)
                curveTo(6.7f, 26.828f, 6.0f, 25.138f, 6.0f, 23.376f)
                verticalLineTo(9.644f)
                curveTo(6.0f, 7.882f, 6.7f, 6.192f, 7.946f, 4.946f)
                curveTo(9.192f, 3.7f, 10.882f, 3.0f, 12.644f, 3.0f)
                horizontalLineTo(26.376f)
                curveTo(28.138f, 3.0f, 29.828f, 3.7f, 31.074f, 4.946f)
                curveTo(32.32f, 6.192f, 33.02f, 7.882f, 33.02f, 9.644f)
                verticalLineTo(23.376f)
                curveTo(33.02f, 25.138f, 32.32f, 26.828f, 31.074f, 28.074f)
                curveTo(29.828f, 29.32f, 28.138f, 30.02f, 26.376f, 30.02f)
                close()
                moveTo(28.709f, 15.276f)
                lineTo(27.832f, 12.535f)
                curveTo(27.795f, 12.423f, 27.717f, 12.33f, 27.612f, 12.276f)
                curveTo(27.508f, 12.222f, 27.387f, 12.212f, 27.275f, 12.247f)
                lineTo(21.452f, 14.109f)
                lineTo(21.333f, 7.864f)
                curveTo(21.332f, 7.805f, 21.319f, 7.748f, 21.295f, 7.694f)
                curveTo(21.271f, 7.641f, 21.237f, 7.593f, 21.194f, 7.553f)
                curveTo(21.152f, 7.513f, 21.102f, 7.481f, 21.047f, 7.461f)
                curveTo(20.993f, 7.44f, 20.934f, 7.43f, 20.876f, 7.432f)
                lineTo(17.957f, 7.487f)
                curveTo(17.899f, 7.488f, 17.841f, 7.5f, 17.787f, 7.523f)
                curveTo(17.733f, 7.546f, 17.684f, 7.579f, 17.643f, 7.621f)
                curveTo(17.603f, 7.662f, 17.57f, 7.712f, 17.549f, 7.766f)
                curveTo(17.527f, 7.82f, 17.516f, 7.878f, 17.516f, 7.937f)
                lineTo(17.634f, 14.142f)
                lineTo(11.627f, 12.072f)
                curveTo(11.572f, 12.053f, 11.514f, 12.045f, 11.456f, 12.048f)
                curveTo(11.398f, 12.052f, 11.341f, 12.067f, 11.288f, 12.092f)
                curveTo(11.236f, 12.118f, 11.189f, 12.153f, 11.151f, 12.197f)
                curveTo(11.112f, 12.241f, 11.083f, 12.291f, 11.064f, 12.346f)
                lineTo(10.125f, 15.068f)
                curveTo(10.087f, 15.179f, 10.094f, 15.301f, 10.146f, 15.406f)
                curveTo(10.197f, 15.512f, 10.288f, 15.592f, 10.399f, 15.631f)
                lineTo(16.348f, 17.682f)
                lineTo(12.581f, 22.722f)
                curveTo(12.546f, 22.769f, 12.521f, 22.822f, 12.506f, 22.878f)
                curveTo(12.492f, 22.935f, 12.489f, 22.993f, 12.497f, 23.051f)
                curveTo(12.505f, 23.108f, 12.524f, 23.164f, 12.554f, 23.214f)
                curveTo(12.584f, 23.264f, 12.623f, 23.308f, 12.67f, 23.343f)
                lineTo(14.976f, 25.066f)
                curveTo(15.023f, 25.101f, 15.075f, 25.126f, 15.132f, 25.14f)
                curveTo(15.188f, 25.155f, 15.247f, 25.158f, 15.304f, 25.15f)
                curveTo(15.362f, 25.142f, 15.417f, 25.122f, 15.467f, 25.093f)
                curveTo(15.517f, 25.063f, 15.561f, 25.024f, 15.596f, 24.977f)
                lineTo(19.363f, 19.936f)
                lineTo(23.015f, 25.059f)
                curveTo(23.083f, 25.155f, 23.187f, 25.219f, 23.303f, 25.239f)
                curveTo(23.419f, 25.259f, 23.537f, 25.231f, 23.633f, 25.163f)
                lineTo(25.978f, 23.491f)
                curveTo(26.025f, 23.457f, 26.065f, 23.414f, 26.096f, 23.365f)
                curveTo(26.127f, 23.316f, 26.148f, 23.261f, 26.157f, 23.204f)
                curveTo(26.167f, 23.146f, 26.165f, 23.087f, 26.152f, 23.031f)
                curveTo(26.139f, 22.974f, 26.115f, 22.92f, 26.081f, 22.873f)
                lineTo(22.429f, 17.75f)
                lineTo(28.421f, 15.832f)
                curveTo(28.477f, 15.815f, 28.528f, 15.787f, 28.573f, 15.749f)
                curveTo(28.617f, 15.712f, 28.654f, 15.666f, 28.681f, 15.614f)
                curveTo(28.707f, 15.563f, 28.724f, 15.506f, 28.729f, 15.448f)
                curveTo(28.733f, 15.39f, 28.727f, 15.332f, 28.709f, 15.276f)
                close()
            }
        }
            .build()
        return _tIM!!
    }

private var _tIM: ImageVector? = null
