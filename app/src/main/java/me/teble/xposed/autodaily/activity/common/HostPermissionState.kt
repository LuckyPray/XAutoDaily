package me.teble.xposed.autodaily.activity.common

import androidx.lifecycle.ViewModel

/**
 * 保存权限申请状态，在屏幕旋转时保留，进程重启后重新初始化。
 */
internal class HostPermissionState : ViewModel() {
    var automaticRequestAttempted = false
}
