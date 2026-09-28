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

public val Icons.Info: ImageVector
    get() {
        if (_info != null) {
            return _info!!
        }
        _info = Builder(name = "Info", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(12.0f, 3.0f)
                curveTo(10.22f, 3.0f, 8.48f, 3.528f, 7.0f, 4.517f)
                curveTo(5.52f, 5.506f, 4.366f, 6.911f, 3.685f, 8.556f)
                curveTo(3.004f, 10.2f, 2.826f, 12.01f, 3.173f, 13.756f)
                curveTo(3.52f, 15.502f, 4.377f, 17.105f, 5.636f, 18.364f)
                curveTo(6.895f, 19.623f, 8.498f, 20.48f, 10.244f, 20.827f)
                curveTo(11.99f, 21.174f, 13.8f, 20.996f, 15.444f, 20.315f)
                curveTo(17.089f, 19.634f, 18.494f, 18.48f, 19.483f, 17.0f)
                curveTo(20.472f, 15.52f, 21.0f, 13.78f, 21.0f, 12.0f)
                curveTo(21.003f, 10.817f, 20.773f, 9.645f, 20.322f, 8.552f)
                curveTo(19.871f, 7.458f, 19.208f, 6.465f, 18.372f, 5.628f)
                curveTo(17.535f, 4.792f, 16.542f, 4.129f, 15.448f, 3.678f)
                curveTo(14.355f, 3.227f, 13.183f, 2.997f, 12.0f, 3.0f)
                close()
                moveTo(12.0f, 19.364f)
                curveTo(10.543f, 19.364f, 9.12f, 18.932f, 7.909f, 18.123f)
                curveTo(6.698f, 17.314f, 5.754f, 16.164f, 5.197f, 14.818f)
                curveTo(4.639f, 13.472f, 4.493f, 11.992f, 4.778f, 10.563f)
                curveTo(5.062f, 9.135f, 5.763f, 7.823f, 6.793f, 6.793f)
                curveTo(7.823f, 5.763f, 9.135f, 5.062f, 10.563f, 4.778f)
                curveTo(11.992f, 4.493f, 13.472f, 4.639f, 14.818f, 5.197f)
                curveTo(16.164f, 5.754f, 17.314f, 6.698f, 18.123f, 7.909f)
                curveTo(18.932f, 9.12f, 19.364f, 10.544f, 19.364f, 12.0f)
                curveTo(19.368f, 12.968f, 19.18f, 13.927f, 18.812f, 14.823f)
                curveTo(18.443f, 15.718f, 17.9f, 16.531f, 17.216f, 17.216f)
                curveTo(16.531f, 17.9f, 15.718f, 18.442f, 14.823f, 18.811f)
                curveTo(13.927f, 19.18f, 12.968f, 19.368f, 12.0f, 19.364f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(11.999f, 11.181f)
                curveTo(11.89f, 11.175f, 11.781f, 11.191f, 11.678f, 11.23f)
                curveTo(11.576f, 11.269f, 11.483f, 11.329f, 11.406f, 11.406f)
                curveTo(11.329f, 11.483f, 11.269f, 11.576f, 11.23f, 11.678f)
                curveTo(11.191f, 11.781f, 11.175f, 11.89f, 11.181f, 11.999f)
                verticalLineTo(15.272f)
                curveTo(11.181f, 15.489f, 11.267f, 15.697f, 11.421f, 15.85f)
                curveTo(11.574f, 16.004f, 11.782f, 16.09f, 11.999f, 16.09f)
                curveTo(12.216f, 16.09f, 12.424f, 16.004f, 12.578f, 15.85f)
                curveTo(12.731f, 15.697f, 12.817f, 15.489f, 12.817f, 15.272f)
                verticalLineTo(11.999f)
                curveTo(12.824f, 11.89f, 12.807f, 11.781f, 12.768f, 11.678f)
                curveTo(12.729f, 11.576f, 12.669f, 11.483f, 12.592f, 11.406f)
                curveTo(12.515f, 11.329f, 12.422f, 11.269f, 12.32f, 11.23f)
                curveTo(12.217f, 11.191f, 12.108f, 11.175f, 11.999f, 11.181f)
                close()
                moveTo(12.735f, 8.399f)
                curveTo(12.735f, 8.317f, 12.653f, 8.317f, 12.653f, 8.235f)
                curveTo(12.653f, 8.224f, 12.651f, 8.213f, 12.647f, 8.203f)
                curveTo(12.643f, 8.193f, 12.637f, 8.184f, 12.63f, 8.176f)
                curveTo(12.622f, 8.169f, 12.613f, 8.163f, 12.603f, 8.159f)
                curveTo(12.593f, 8.155f, 12.582f, 8.153f, 12.571f, 8.153f)
                curveTo(12.46f, 8.03f, 12.312f, 7.947f, 12.149f, 7.917f)
                curveTo(11.986f, 7.887f, 11.818f, 7.913f, 11.671f, 7.989f)
                curveTo(11.589f, 8.071f, 11.507f, 8.071f, 11.426f, 8.153f)
                lineTo(11.344f, 8.235f)
                curveTo(11.344f, 8.317f, 11.262f, 8.317f, 11.262f, 8.399f)
                curveTo(11.262f, 8.481f, 11.262f, 8.481f, 11.18f, 8.563f)
                verticalLineTo(8.727f)
                curveTo(11.177f, 8.835f, 11.197f, 8.942f, 11.24f, 9.04f)
                curveTo(11.282f, 9.139f, 11.345f, 9.228f, 11.425f, 9.3f)
                curveTo(11.507f, 9.382f, 11.589f, 9.464f, 11.67f, 9.464f)
                curveTo(11.766f, 9.529f, 11.882f, 9.558f, 11.997f, 9.546f)
                horizontalLineTo(12.161f)
                curveTo(12.243f, 9.546f, 12.243f, 9.546f, 12.325f, 9.464f)
                curveTo(12.407f, 9.464f, 12.407f, 9.382f, 12.489f, 9.382f)
                lineTo(12.571f, 9.3f)
                curveTo(12.651f, 9.228f, 12.714f, 9.139f, 12.757f, 9.04f)
                curveTo(12.799f, 8.942f, 12.819f, 8.835f, 12.816f, 8.727f)
                verticalLineTo(8.563f)
                curveTo(12.816f, 8.481f, 12.734f, 8.481f, 12.734f, 8.399f)
                horizontalLineTo(12.735f)
                close()
            }
        }
        .build()
        return _info!!
    }

private var _info: ImageVector? = null
