package me.teble.xposed.autodaily.ui.scene

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.config.ALIPAY_QRCODE
import me.teble.xposed.autodaily.config.GITHUB_RELEASE_URL
import me.teble.xposed.autodaily.config.PAN_URL
import me.teble.xposed.autodaily.hook.base.hostVersionCode
import me.teble.xposed.autodaily.hook.base.hostVersionName
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.ui.composable.Icon
import me.teble.xposed.autodaily.ui.composable.RoundedSnackbarHost
import me.teble.xposed.autodaily.ui.composable.Text
import me.teble.xposed.autodaily.ui.composable.TextInfoItem
import me.teble.xposed.autodaily.ui.composable.TextItem
import me.teble.xposed.autodaily.ui.composable.TopBar
import me.teble.xposed.autodaily.ui.composable.XaScaffold
import me.teble.xposed.autodaily.ui.dialog.UpdateType
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.ChevronRight
import me.teble.xposed.autodaily.ui.icon.icons.XAutoDaily
import me.teble.xposed.autodaily.ui.icon.icons.XAutoDailyRound
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.TimeUtil
import me.teble.xposed.autodaily.utils.openUrl

@Parcelize
data object AboutScreen : Screen {
    @Stable
    data class State(
        private val eventSink: (Event) -> Unit,

        val snackbarHostState: SnackbarHostState,

        val backClick: () -> Unit = { eventSink(Event.BackClicked) },
        val onNavigateToLicense: () -> Unit = { eventSink(Event.License) },
        val onNavigateToDeveloper: () -> Unit = { eventSink(Event.Developer) },

        val hasUpdateProvider: () -> Boolean,
        val moduleVersionNameProvider: () -> String,
        val moduleVersionCodeProvider: () -> Int,

        val qqVersionNameProvider: () -> String,
        val qqVersionCodeProvider: () -> Long,
        val configVersionProvider: () -> Int,

        val dialogStateProvider: () -> Boolean,
        val dismissDialog: () -> Unit,
        val showDialog: () -> Unit,

        val updateApp: () -> Unit,
        val updateConfirm: (UpdateType) -> Unit,
        val openGithub: () -> Unit,
        val openTelegram: () -> Unit,
        val openAliPay: () -> Unit,
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data object BackClicked : Event
        data object License : Event
        data object Developer : Event
    }
}

class AboutPresenter(
    private val screen: AboutScreen,
    private val navigator: Navigator,
) : Presenter<AboutScreen.State> {

    class Factory() : Presenter.Factory {
        override fun create(
            screen: Screen,
            navigator: Navigator,
            context: CircuitContext
        ): Presenter<*>? {
            return when (screen) {
                is AboutScreen -> return AboutPresenter(screen, navigator)
                else -> null
            }
        }
    }

    @Stable
    @Composable
    override fun present(): AboutScreen.State {

        var moduleVersionName by remember { mutableStateOf("") }
        var moduleVersionCode by remember { mutableIntStateOf(0) }

        var qqVersionName by remember { mutableStateOf("") }
        var qqVersionCode by remember { mutableLongStateOf(0L) }
        var configVersion by remember { mutableIntStateOf(0) }


        var hasUpdate by remember { mutableStateOf(false) }

        val (dialogState, updateState) = remember { mutableStateOf(false) }
        val (dialogText, updateDialogText) = remember { mutableStateOf("") }

        val lastClickTime = remember { mutableLongStateOf(0L) }

        val scope = rememberStableCoroutineScope()

        val snackbarHostState = remember { SnackbarHostState() }

        suspend fun showSnackbar(text: String) {
            snackbarHostState.showSnackbar(text)
        }

        val context = LocalContext.current
        LaunchedEffect(Unit) {
            moduleVersionName = BuildConfig.VERSION_NAME
            moduleVersionCode = BuildConfig.VERSION_CODE
            qqVersionName = hostVersionName
            qqVersionCode = hostVersionCode
            configVersion = ConfigUtil.loadSaveConf().version

            ConfigUtil.fetchMeta()?.let {
                // 检查新版本
                if (BuildConfig.VERSION_CODE < it.app.versionCode) {
                    hasUpdate = true
                }
            }
        }
        val dismissDialog = { updateState(false) }
        val showDialog = { updateState(true) }

        return AboutScreen.State(
            eventSink = { event ->
                when (event) {
                    AboutScreen.Event.BackClicked -> navigator.pop()
                    AboutScreen.Event.Developer -> navigator.goTo(DeveloperScreen)
                    AboutScreen.Event.License -> navigator.goTo(LicenseScreen)
                }
            },


            snackbarHostState = snackbarHostState,
            dialogStateProvider = { dialogState },
            dismissDialog = dismissDialog,
            showDialog = showDialog,

            hasUpdateProvider = { hasUpdate },
            moduleVersionNameProvider = { moduleVersionName },
            moduleVersionCodeProvider = { moduleVersionCode },

            qqVersionNameProvider = { qqVersionName },
            qqVersionCodeProvider = { qqVersionCode },
            configVersionProvider = { configVersion },
            updateApp = {
                scope.launch {
                    val time = TimeUtil.cnTimeMillis()
                    if (time - lastClickTime.longValue < 15_000) {
                        showSnackbar("不要频繁点击哦~")
                        return@launch
                    }
                    lastClickTime.longValue = time

                    showSnackbar("正在检测更新")
                    val info = ConfigUtil.fetchMeta()
                    info?.let {
                        val currConfVer = ConfigUtil.loadSaveConf().version
                        if (currConfVer < info.config.version) {
                            if (BuildConfig.VERSION_CODE >= info.config.needAppVersion) {
                                return@launch
                            } else {
//                    XANotification.notify("插件版本过低，无法应用最新配置，推荐更新插件")
                                return@launch
                            }
                        }
                        if (BuildConfig.VERSION_CODE < info.app.versionCode) {
                            hasUpdate = true
                            updateDialogText(ConfUnit.metaInfoCache?.app?.updateLog ?: "")

                            showDialog()

                            showSnackbar("插件版本存在更新")
                            return@launch
                        }
                    }
                    showSnackbar("当前插件与配置均是最新版本")

                }

                configVersion = ConfigUtil.loadSaveConf().version
            },
            updateConfirm = { type ->
                scope.launch {
                    when (type) {
                        UpdateType.Ignore -> {
                            dismissDialog()
                            ConfUnit.skipUpdateVersion =
                                "${ConfUnit.metaInfoCache?.app?.versionCode}"
                            showSnackbar("已忽略版本号为 ${ConfUnit.skipUpdateVersion} 的更新")
                        }

                        UpdateType.Drive -> {
                            dismissDialog()
                            showSnackbar("正在跳转，请稍后")
                            context.openUrl(PAN_URL)
                        }

                        UpdateType.Github -> {
                            dismissDialog()
                            showSnackbar("正在跳转，请稍后")
                            context.openUrl(GITHUB_RELEASE_URL)
                        }
                    }
                }
            },
            openTelegram = {
                scope.launch {
                    showSnackbar("正在跳转，请稍后")
                    context.openUrl("https://t.me/XAutoDailyChat")
                }
            },
            openGithub = {
                scope.launch {
                    showSnackbar("正在跳转，请稍后")
                    context.openUrl("https://github.com/LuckyPray/XAutoDaily")
                }
            },
            openAliPay = {
                scope.launch {
                    try {
                        showSnackbar("正在跳转，请稍后")
                        context.openUrl(
                            "alipayqr://platformapi/startapp?saId=10000007&clientVersion=" +
                                    "3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F$ALIPAY_QRCODE%3F_s%3Dweb-other"
                        )
                    } catch (e: Exception) {
                        LogUtil.e(e, "open alipay qr error: ")
                        context.openUrl("https://mobilecodec.alipay.com/client_download.htm?qrcode=$ALIPAY_QRCODE")
                    }
                }
            }

        )
    }
}

@Composable
fun AboutUI(
    snackbarHostState: SnackbarHostState,

    backClick: () -> Unit,
    onNavigateToLicense: () -> Unit,
    onNavigateToDeveloper: () -> Unit,

    hasUpdateProvider: () -> Boolean,
    moduleVersionNameProvider: () -> String,
    moduleVersionCodeProvider: () -> Int,

    qqVersionNameProvider: () -> String,
    qqVersionCodeProvider: () -> Long,
    configVersionProvider: () -> Int,

    dialogStateProvider: () -> Boolean,
    dismissDialog: () -> Unit,
    showDialog: () -> Unit,

    updateApp: () -> Unit,
    updateConfirm: (UpdateType) -> Unit,
    openGithub: () -> Unit,
    openTelegram: () -> Unit,
    openAliPay: () -> Unit,

    modifier: Modifier
) {


    XaScaffold(
        topBar = {
            TopBar(text = "关于", backClick = backClick)
        },
        snackbarHost = {
            RoundedSnackbarHost(hostState = snackbarHostState)
        },
        modifier = modifier,
        containerColor = colors.colorBgLayout,

        ) {
        Column(
            Modifier
                .fillMaxSize()
                .clip(SmootherShape(12.dp))
                .verticalScroll(rememberScrollState())
                .defaultNavigationBarPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BuildConfigLayout(
                moduleVersionName = moduleVersionNameProvider,
                moduleVersionCode = moduleVersionCodeProvider
            )
            UpdateLayout(
                qqVersionName = qqVersionNameProvider,
                qqVersionCode = qqVersionCodeProvider,
                configVersion = configVersionProvider,
                hasUpdate = hasUpdateProvider,
                updateApp = updateApp
            )
            LicenseLayout(onNavigateToLicense = onNavigateToLicense)
            OthterLayout(
                onNavigateToDeveloper = onNavigateToDeveloper,
                openGithub = openGithub,
                openTelegram = openTelegram,
                openAliPay = openAliPay
            )
        }
    }


//        UpdateDialog(
//            state = sheetState,
//            enable = { showUpdateDialog },
//            text = "新版本",
//            info = { updateDialogText },
//            onDismiss = viewmodel::dismissUpdateDialog,
//            onConfirm = {
//                viewmodel.updateConfirm(context, it)
//            }
//        )

}

@Composable
private fun BuildConfigLayout(
    moduleVersionName: () -> String,
    moduleVersionCode: () -> Int,
) {

    val colors = colors
    Image(
        Icons.XAutoDailyRound,
        contentDescription = "XAutoDaily Round Icon",
        Modifier
            .padding(top = 36.dp)
            .size(80.dp)
    )

    Icon(
        tint = { colors.colorText },
        imageVector = Icons.XAutoDaily,
        contentDescription = "text logo",
        modifier = Modifier.padding(top = 16.dp)
    )

    Text(
        text = { "v${moduleVersionName()} (${moduleVersionCode()}) " },
        modifier = Modifier.padding(top = 8.dp),
        style = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
        ),
        color = { colors.colorAboutText }
    )
}

@Composable
private fun UpdateLayout(
    qqVersionName: () -> String,
    qqVersionCode: () -> Long,
    configVersion: () -> Int,
    hasUpdate: () -> Boolean,
    updateApp: () -> Unit

) {


    val colors = colors
    Row(
        Modifier
            .padding(top = 24.dp)
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .drawBehind {
                drawRect(colors.colorBgContainer)
            }
            .clickable(role = Role.Button, onClick = updateApp)
            .padding(vertical = 20.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "检查更新",
                maxLines = 1,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = { colors.colorText }
            )

            Text(
                text = "当前宿主版本：${qqVersionName()} (${qqVersionCode()})\n"
                        + "当前配置版本：${configVersion()}",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.colorTextSecondary
                ),
                color = { colors.colorText }
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        if (hasUpdate()) {
            Text(
                text = "有新版本",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = { colors.themeColor }
            )
        }

        Icon(
            imageVector = Icons.ChevronRight,
            contentDescription = "",
            modifier = Modifier.size(24.dp),
            tint = { colors.colorIcon }
        )
    }
}

@Composable
private fun LicenseLayout(onNavigateToLicense: () -> Unit) {
    val colors = colors
    TextItem(
        text = "开放源代码许可",
        modifier = Modifier
            .padding(top = 24.dp)
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .drawBehind {
                drawRect(colors.colorBgContainer)
            },
        onClick = onNavigateToLicense
    )
}

@Composable
private fun OthterLayout(
    onNavigateToDeveloper: () -> Unit,
    openGithub: () -> Unit,
    openTelegram: () -> Unit,
    openAliPay: () -> Unit,
) {
    val colors = colors
    Column(
        modifier = Modifier
            .padding(top = 24.dp)
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .drawBehind {
                drawRect(colors.colorBgContainer)
            },
    ) {
        TextItem(
            text = "开发者",
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            onClick = onNavigateToDeveloper
        )

        TextItem(
            text = "Github",
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            onClick = openGithub
        )

        TextItem(
            text = "Telegram 频道",
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            onClick = openTelegram
        )

        TextInfoItem(
            text = "请作者吃辣条",
            infoText = "本模块完全免费开源，一切开发旨在学习，请勿用于非法用途。喜欢本模块的可以捐赠支持我，谢谢~~",
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp)),
            clickEnabled = { true },
            onClick = openAliPay
        )
    }

}