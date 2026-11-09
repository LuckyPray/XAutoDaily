package me.teble.xposed.autodaily.ui.icon.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import me.teble.xposed.autodaily.ui.icon.Icons

/**
 * 由 icon_status.svg 转换的签到状态图标，圆形底色由主页入口组件绘制。
 */
val Icons.Status: ImageVector
    get() {
        if (_status != null) return _status!!
        _status = ImageVector.Builder(
            name = "Status",
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
        ).apply {
            group(rotate = 45f, pivotX = 14.7458f, pivotY = 10.6001f) {
                path(fill = SolidColor(Color.White)) {
                    moveTo(16.2458f, 10.6001f)
                    arcToRelative(1.5f, 1.5f, 0f, false, true, 1.5f, 1.5f)
                    verticalLineTo(20.5493f)
                    arcToRelative(1.5f, 1.5f, 0f, false, true, -1.5f, 1.5f)
                    arcToRelative(1.5f, 1.5f, 0f, false, true, -1.5f, -1.5f)
                    verticalLineTo(12.1001f)
                    arcToRelative(1.5f, 1.5f, 0f, false, true, 1.5f, -1.5f)
                    close()
                }
            }
            group(rotate = 45f, pivotX = 23.2288f, pivotY = 11.1504f) {
                path(fill = SolidColor(Color.White)) {
                    moveTo(24.7288f, 11.1504f)
                    arcToRelative(1.5f, 1.5f, 0f, false, true, 1.5f, 1.5f)
                    verticalLineTo(21.1323f)
                    arcToRelative(1.5f, 1.5f, 0f, false, true, -1.5f, 1.5f)
                    arcToRelative(1.5f, 1.5f, 0f, false, true, -1.5f, -1.5f)
                    verticalLineTo(12.6504f)
                    arcToRelative(1.5f, 1.5f, 0f, false, true, 1.5f, -1.5f)
                    close()
                }
            }
            group(rotate = -20f, pivotX = 12.9099f, pivotY = 11.7764f) {
                path(fill = SolidColor(Color.White)) {
                    moveTo(14.4099f, 11.7764f)
                    arcToRelative(1.5f, 1.5f, 0f, false, true, 1.5f, 1.5f)
                    verticalLineTo(20.2764f)
                    arcToRelative(1.5f, 1.5f, 0f, false, true, -1.5f, 1.5f)
                    arcToRelative(1.5f, 1.5f, 0f, false, true, -1.5f, -1.5f)
                    verticalLineTo(13.2764f)
                    arcToRelative(1.5f, 1.5f, 0f, false, true, 1.5f, -1.5f)
                    close()
                }
            }
        }.build()
        return _status!!
    }

private var _status: ImageVector? = null
