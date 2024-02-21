package me.teble.xposed.autodaily.activity.common

import android.os.Build
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import me.teble.xposed.autodaily.application.xaApp
import me.teble.xposed.autodaily.config.DataMigrationService
import me.teble.xposed.autodaily.config.PACKAGE_NAME_QQ
import me.teble.xposed.autodaily.config.PACKAGE_NAME_TIM
import me.teble.xposed.autodaily.hook.enums.QQTypeEnum
import me.teble.xposed.autodaily.shizuku.ShizukuApi
import me.teble.xposed.autodaily.shizuku.ShizukuConf
import me.teble.xposed.autodaily.ui.composable.SmallTitle
import me.teble.xposed.autodaily.ui.composable.SwitchInfoDivideItem
import me.teble.xposed.autodaily.ui.composable.SwitchInfoItem
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.Activated
import me.teble.xposed.autodaily.ui.icon.icons.ChevronRight
import me.teble.xposed.autodaily.ui.icon.icons.More
import me.teble.xposed.autodaily.ui.icon.icons.QQ
import me.teble.xposed.autodaily.ui.icon.icons.TIM
import me.teble.xposed.autodaily.ui.icon.icons.XAutoDaily

import me.teble.xposed.autodaily.ui.theme.DefaultAlpha
import me.teble.xposed.autodaily.ui.theme.DisabledAlpha
import me.teble.xposed.autodaily.utils.TaskExecutor
import me.teble.xposed.autodaily.utils.toJsonString
import java.io.File


@Composable
fun ModuleScene() {

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .statusBarsPadding()
            .padding(horizontal = 16.dp)

    ) {
        TopBar()

        Column(
            Modifier
                .clip(SmootherShape(12.dp))
                .verticalScroll(rememberScrollState())
                .weight(weight = 1f, fill = false)
                .padding(bottom = 24.dp)
                .navigationBarsPadding()
        ) {
            ShizukuCard()
            EntryLayout()
            SettingLayout()
        }

    }

}

@Composable
private fun TopBar() {
    Row() {
        Image(
            imageVector = Icons.XAutoDaily,
            contentDescription = "logo",
            Modifier
                .padding(vertical = 20.dp)
                .padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(
            imageVector = Icons.More,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = 10.dp)
                .size(36.dp)
                .clip(CircleShape)
                .clickable(role = Role.Button) {

                }
                .padding(6.dp),
            contentDescription = "logo"
        )
    }
}

@Composable
private fun ShizukuCard(
    viewmodel: MainViewModel = viewModel()
) {
    Row(
        Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .background(Color(0xFF25CD8E))
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Image(
            imageVector = Icons.Activated,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(32.dp),
            contentDescription = "shizuku-icon"
        )
        Column(Modifier.padding(start = 16.dp)) {

            Text(
                text = "Shizuku 服务正在运行", style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFFFFF),
                )
            )
            Text(
                text = "Shizuku Api Version: 13", Modifier.padding(top = 4.dp), style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFFFFFFF),
                )
            )
            Text(
                text = "守护进程未在运行，点击运行", Modifier.padding(top = 4.dp), style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFFFFFFF),
                )
            )
        }
    }
}

@Composable
private fun EntryLayout(viewmodel: MainViewModel = viewModel()) {
    if (xaApp.qPackageState.containsKey(PACKAGE_NAME_TIM)
        || xaApp.qPackageState.containsKey(PACKAGE_NAME_QQ)
    ) {
        SmallTitle(
            title = "模块入口",
            modifier = Modifier
                .padding(bottom = 8.dp, start = 16.dp, top = 16.dp)
        )

        Column(
            Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp))
                .background(Color(0xFFFFFFFF))
        ) {
            val context = LocalContext.current
            if (xaApp.qPackageState.containsKey(PACKAGE_NAME_QQ)) {
                EntryItem(
                    icon = Icons.QQ,
                    contentDescription = "QQ Logo",
                    title = "QQ",
                    info = "QQ 侧滑 > 设置 > XAutoDaily",
                    onClick = {
                        viewmodel.openHostSetting(context, QQTypeEnum.QQ)
                    })
            }
            if (xaApp.qPackageState.containsKey(PACKAGE_NAME_TIM)) {
                EntryItem(
                    icon = Icons.TIM,
                    contentDescription = "Tim Logo",
                    title = "Tim",
                    info = "TIM 侧滑 > 设置 > XAutoDaily",
                    onClick = {
                        viewmodel.openHostSetting(context, QQTypeEnum.TIM)
                    })
            }
        }
    }
}


@Composable
private fun SettingLayout(
    viewmodel: MainViewModel = viewModel()
) {

    SmallTitle(
        title = "更多模块",
        modifier = Modifier
            .padding(bottom = 8.dp, start = 16.dp, top = 16.dp)
    )

    var shizukuEnable by remember { mutableStateOf(ShizukuApi.isPermissionGranted && ShizukuApi.isPermissionGranted) }
    LaunchedEffect(ShizukuApi.isPermissionGranted, ShizukuApi.isPermissionGranted) {
        shizukuEnable = ShizukuApi.isPermissionGranted && ShizukuApi.isPermissionGranted
    }

    val itemAlpha: Float by animateFloatAsState(
        targetValue = if (shizukuEnable) DefaultAlpha else DisabledAlpha,
        animationSpec = spring(), label = "switch item"
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val untrustedTouchEvents by viewmodel.untrustedTouchEvents.collectAsStateWithLifecycle(false)
        SwitchInfoItem(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth()
                .clip(SmootherShape(12.dp))
                .background(Color(0xFFFFFFFF).copy(alpha = itemAlpha)),
            enable = untrustedTouchEvents,
            text = "取消安卓 12 不受信触摸",
            infoText = "安卓 12 后启用对 Toast 弹窗等事件触摸不可穿透，勾选此项可关闭",
            clickEnabled = shizukuEnable,
            onClick = {
                viewmodel.updateUntrustedTouchEvents(it)
            }
        )
    }



    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .background(Color(0xFFFFFFFF).copy(alpha = itemAlpha))

    ) {
        val keepAlive by viewmodel.keepAlive.collectAsStateWithLifecycle(false)

        SwitchInfoItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            enable = keepAlive,
            text = "启用 Shizuku 保活机制",
            infoText = "通过 Shizuku 运行一个 Service，当监测到 QQ/TIM 被杀死后重新拉起进程",
            clickEnabled = shizukuEnable,
            onClick = {
                viewmodel.updateKeepAliveChecked(it)
            }
        )
        if (xaApp.qPackageState.containsKey(PACKAGE_NAME_QQ)) {

            val qqKeepAlive by viewmodel.qqKeepAlive.collectAsStateWithLifecycle(false)

            KeepTimeItem(
                title = "启用 QQ 保活",
                info = "单击此处尝试后台唤醒 QQ，如果模块 hook 生效将会显示一个气泡",
                keepAlive = keepAlive && shizukuEnable,
                hostPackage = PACKAGE_NAME_QQ,
                hostKeepAlive = qqKeepAlive,
                onClick = {
                    ShizukuApi.startService(
                        PACKAGE_NAME_QQ, DataMigrationService,
                        arrayOf(
                            "-e", TaskExecutor.CORE_SERVICE_FLAG, "$",
                            "-e", TaskExecutor.CORE_SERVICE_TOAST_FLAG, "$"
                        )
                    )

                },
                onChange = {
                    viewmodel.updateQQKeepAlive(it)
                }
            )
        }

        if (xaApp.qPackageState.containsKey(PACKAGE_NAME_TIM)) {
            val timKeepAlive by viewmodel.timKeepAlive.collectAsStateWithLifecycle(false)

            KeepTimeItem(
                title = "启用 TIM 保活",
                info = "单击此处尝试后台唤醒 TIM，如果模块 hook 生效将会显示一个气泡",
                keepAlive = keepAlive && shizukuEnable,
                hostPackage = PACKAGE_NAME_TIM,
                hostKeepAlive = timKeepAlive,
                onClick = {
                    ShizukuApi.startService(
                        PACKAGE_NAME_TIM, DataMigrationService,
                        arrayOf(
                            "-e", TaskExecutor.CORE_SERVICE_FLAG, "$",
                            "-e", TaskExecutor.CORE_SERVICE_TOAST_FLAG, "$"
                        )
                    )
                },
                onChange = {
                    viewmodel.updateTimKeepAlive(it)
                }
            )
        }

    }

}

@Composable
private fun EntryItem(
    icon: ImageVector,
    contentDescription: String,
    title: String,
    info: String,
    onClick: () -> Unit = {}
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .clickable(role = Role.Button) {
                onClick()
            }
            .padding(16.dp)
    ) {
        Image(
            imageVector = icon,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(40.dp),
            contentDescription = contentDescription
        )
        Column(
            Modifier
                .padding(start = 10.dp)
                .weight(1f)
        ) {

            Text(
                text = title, style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF202124),
                )
            )
            Text(
                text = info, Modifier.padding(top = 4.dp), style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF5F6368),
                )
            )
        }

        Icon(
            imageVector = Icons.ChevronRight,
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(24.dp),
            tint = Color(0xFFE6E6E6)
        )
    }
}

/**
 * @param hostPackage 被保活的包名
 *
 */
@Composable
private fun KeepTimeItem(
    title: String,
    info: String,
    keepAlive: Boolean,
    hostKeepAlive: Boolean,
    hostPackage: String,
    onClick: () -> Unit,
    onChange: (Boolean) -> Unit,
) {
    LaunchedEffect(hostKeepAlive, keepAlive) {
        val confDir = File(xaApp.getExternalFilesDir(null), "conf")
        if (confDir.isFile) {
            confDir.delete()
        }
        if (!confDir.exists()) {
            confDir.mkdirs()
        }
        val confFile = File(confDir, "conf.json")
        val conf = ShizukuConf(keepAlive,
            mutableMapOf<String, Boolean>().apply {
                put(hostPackage, hostKeepAlive)
            })
        val confString = conf.toJsonString()
        Log.d("XALog", confString)
        confFile.writeText(confString)
    }
    SwitchInfoDivideItem(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SmootherShape(12.dp)),
        enable = hostKeepAlive,
        text = title,
        infoText = info,
        clickEnabled = keepAlive,
        onClick = onClick,
        onChange = onChange
    )
}
