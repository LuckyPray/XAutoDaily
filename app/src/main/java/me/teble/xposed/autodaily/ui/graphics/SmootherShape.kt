package me.teble.xposed.autodaily.ui.graphics

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

@Immutable
class SmootherShape(
    private val radius: Dp = 13.dp,
    private val smoothness: Float = 0.6f, // iOS Figma default
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val width = size.width
        val height = size.height
        val maxRadius = min(width, height) / 2
        val cornerRadius = min(radius.value * density.density, maxRadius)

        val (a, b, c, d, p, angleTheta, _) = calculateSmoothRadius(
            width = width,
            height = height,
            cornerRadius = cornerRadius,
            smoothing = smoothness
        )

        val sweepAngle = 90f - angleTheta
        val path = Path().apply {
            // Top right
            moveTo(max(width / 2, width - p), 0f)
            cubicTo(width - (p - a), 0f , width - (p - a - b), 0f, width - (p - a - b - c), d)
            arcTo(
                Rect(left = width - cornerRadius * 2, top = 0f, right = width, bottom = cornerRadius * 2),
                270f + angleTheta,
                sweepAngle,
                false
            )
            cubicTo(width, p - a - b, width, p - a, width, min(height / 2, p))

            // Bottom right
            lineTo(width, max(height / 2, height - p))
            cubicTo(
                width,
                height - (p - a),
                width,
                height - (p - a - b),
                width - d,
                height - (p - a - b - c)
            )
            arcTo(
                Rect(left = width - cornerRadius * 2, top = height - cornerRadius * 2, right = width, bottom = height),
                0f + angleTheta,
                sweepAngle,
                false
            )
            cubicTo(
                width - (p - a - b),
                height,
                width - (p - a),
                height,
                max(width / 2, width - p),
                height
            )

            // Bottom left
            lineTo(min(width / 2, p), height)
            cubicTo(p - a, height, p - a - b, height, p - a - b - c, height - d)
            arcTo(
                Rect(left = 0f, top = height - cornerRadius * 2, right = cornerRadius * 2, bottom = height),
                90f + angleTheta,
                sweepAngle,
                false
            )
            cubicTo(
                0f,
                height - (p - a - b),
                0f,
                height - (p - a),
                0f,
                max(height / 2, height - p)
            )

            // Top left
            lineTo(0f, min(height / 2, p))
            cubicTo(0f, p - a, 0f, p - a - b, d, p - a - b - c)
            arcTo(
                Rect(left = 0f, top = 0f, right = cornerRadius * 2, bottom = cornerRadius * 2),
                180f + angleTheta,
                sweepAngle,
                false
            )
            cubicTo(
                p - a - b,
                0f,
                p - a,
                0f,
                min(width / 2, p),
                0f
            )

            close()
        }

        return Outline.Generic(path)
    }
}

data class SmoothRadius(
    val a: Float,
    val b: Float,
    val c: Float,
    val d: Float,
    val p: Float,
    val angleTheta: Float,
    val circularSectionLength: Float
)

fun calculateSmoothRadius(
    width: Float,
    height: Float,
    cornerRadius: Float,
    smoothing: Float
): SmoothRadius {
    val maxRadius = min(width, height) / 2
    val ANGLE_TO_RADIANS = (Math.PI / 180f).toFloat()

    val p = min((1 + smoothing) * cornerRadius, maxRadius)
    val angleAlpha: Float
    val angleBeta: Float

    if (cornerRadius <= maxRadius / 2) {
        angleBeta = 90f * (1 - smoothing)
        angleAlpha = 45f * smoothing
    } else {
        val diffRatio = (cornerRadius - maxRadius / 2) / (maxRadius / 2)
        angleBeta = 90f * (1 - smoothing * (1 - diffRatio))
        angleAlpha = 45f * smoothing * (1 - diffRatio)
    }

    val angleTheta = (90f - angleBeta) / 2

    val p3ToP4Distance = cornerRadius * tan((angleTheta / 2) * ANGLE_TO_RADIANS)
    val circularSectionLength = sin(angleBeta / 2 * ANGLE_TO_RADIANS) * cornerRadius * sqrt(2f)

    val c = p3ToP4Distance * cos(angleAlpha * ANGLE_TO_RADIANS)
    val d = c * tan(angleAlpha * ANGLE_TO_RADIANS)
    val b = (p - circularSectionLength - c - d) / 3
    val a = 2 * b

    return SmoothRadius(
        a = a,
        b = b,
        c = c,
        d = d,
        p = p,
        angleTheta = angleTheta,
        circularSectionLength = circularSectionLength
    )
}