package me.teble.xposed.autodaily.ui.scene


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slack.circuit.overlay.OverlayEffect
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuitx.overlays.DialogResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import me.teble.xposed.autodaily.R
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.ui.composable.Icon
import me.teble.xposed.autodaily.ui.composable.RoundedSnackbarHost
import me.teble.xposed.autodaily.ui.composable.Text
import me.teble.xposed.autodaily.ui.composable.XAutoDailyTopBar
import me.teble.xposed.autodaily.ui.composable.XaScaffold
import me.teble.xposed.autodaily.ui.dialog.showNoticeOverlay
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.About
import me.teble.xposed.autodaily.ui.icon.icons.ChevronRight
import me.teble.xposed.autodaily.ui.icon.icons.Configuration
import me.teble.xposed.autodaily.ui.icon.icons.Notice
import me.teble.xposed.autodaily.ui.icon.icons.Script
import me.teble.xposed.autodaily.ui.icon.icons.Setting
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.CardDisabledAlpha
import me.teble.xposed.autodaily.ui.theme.DefaultAlpha
import me.teble.xposed.autodaily.ui.theme.DisabledAlpha
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors
import me.teble.xposed.autodaily.utils.TaskExecutor

@Parcelize
data object MainScreen : Screen {
    @Stable
    data class State(
        private val eventSink: (Event) -> Unit,

        val snackbarHostState: SnackbarHostState,
        val noticeProvider: () -> String,
        val execTaskTaskNumProvider: () -> String,

        val signClick: () -> Unit,

        val dialogStateProvider: () -> Boolean,
        val dismissDialog: () -> Unit,
        val showDialog: () -> Unit,

        val onNavigateToSign: () -> Unit = { eventSink(Event.Sign) },
        val onNavigateToSetting: () -> Unit = { eventSink(Event.Setting) },
        val onNavigateToAbout: () -> Unit = { eventSink(Event.About) },
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data object Sign : Event
        data object Setting : Event
        data object About : Event
    }
}


class MainPresenter(private val navigator: Navigator) : Presenter<MainScreen.State> {
    class Factory : Presenter.Factory {
        override fun create(
            screen: Screen,
            navigator: Navigator,
            context: CircuitContext
        ): Presenter<*>? {
            return when (screen) {
                MainScreen -> return MainPresenter(navigator)
                else -> null
            }
        }
    }

    @Stable
    @Composable
    override fun present(): MainScreen.State {
        val (dialogState, updateState) = remember { mutableStateOf(false) }

        val (execTaskNum, updateNum) = remember { mutableIntStateOf(0) }

        val (noticeText, updateText) = remember { mutableStateOf("") }

        var lastClickTime by remember { mutableLongStateOf(0L) }

        val scope = rememberStableCoroutineScope()

        val snackbarHostState = remember { SnackbarHostState() }

        suspend fun showSnackbar(text: String) {
            snackbarHostState.showSnackbar(text)
        }
        LaunchedEffect(Unit) {
            val meta = ConfUnit.metaInfoCache ?: ConfigUtil.fetchMeta()
            if (System.currentTimeMillis() - ConfUnit.lastFetchTime > 60 * 60 * 1000L) {
                ConfigUtil.fetchMeta()
            }
            meta?.let {
                withContext(Dispatchers.Main) {
                    updateText(it.notice?.trimEnd() ?: "暂无公告")
                }
            } ?: run {
                showSnackbar("拉取公告失败")
            }
        }


        LaunchedEffect(Unit) {
            try {
                val num = ConfigUtil.getCurrentExecTaskNum()
                for (i in 1..num) {
                    delay(15)
                    withContext(Dispatchers.Main) {
                        updateNum(execTaskNum + 1)
                    }

                }
            } catch (e: Exception) {
                showSnackbar(e.stackTraceToString())
            }
        }



        return MainScreen.State(
            snackbarHostState = snackbarHostState,
            noticeProvider = { noticeText },
            execTaskTaskNumProvider = { execTaskNum.toString() },

            signClick = {
                scope.launch {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastClickTime < 5000L) {
                        showSnackbar("点那么快怎么不上天呢")
                    } else {
                        lastClickTime = currentTime
                        TaskExecutor.handler.sendEmptyMessage(TaskExecutor.EXEC_TASK)
                    }
                }
            },
            dialogStateProvider = { dialogState },
            dismissDialog = { updateState(false) },
            showDialog = { updateState(true) },

            eventSink = { event ->
                when (event) {
                    // Navigate to the detail screen when an email is clicked
                    is MainScreen.Event.Sign -> navigator.goTo(SignScreen)
                    MainScreen.Event.About -> navigator.goTo(AboutScreen)
                    MainScreen.Event.Setting -> navigator.goTo(SettingScreen)
                }
            }

        )
    }
}

@Composable
fun MainUI(
    snackbarHostState: SnackbarHostState,
    noticeProvider: () -> String,
    execTaskTaskNumProvider: () -> String,

    signClick: () -> Unit,

    dialogStateProvider: () -> Boolean,
    dismissDialog: () -> Unit,
    showDialog: () -> Unit,

    onNavigateToSign: () -> Unit,
    onNavigateToSetting: () -> Unit,
    onNavigateToAbout: () -> Unit,

    modifier: Modifier
) {


    XaScaffold(
        modifier = modifier,
        snackbarHost = {
            RoundedSnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            XAutoDailyTopBar(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 20.dp)
                    .padding(start = 16.dp),
                icon = Icons.Notice,
                contentDescription = "公告",
                iconClick = showDialog
            )
        }, containerColor = colors.colorBgLayout
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(SmootherShape(12.dp))
                .verticalScroll(rememberScrollState())
                .defaultNavigationBarPadding()
        ) {

            Banner(execTaskTaskNumProvider, signClick = signClick)
            GridLayout(
                onNavigateToSign = onNavigateToSign,
                onNavigateToSetting = onNavigateToSetting,
                onNavigateToAbout = onNavigateToAbout,
                execTaskNum = execTaskTaskNumProvider
            )
        }

    }

    NoticeOverlay(dialogStateProvider, noticeProvider, dismissDialog)
}

@Composable
fun NoticeOverlay(
    dialogStateProvider: () -> Boolean,
    noticeProvider: () -> String,
    dismissDialog: () -> Unit
) {
    val colors = colors
    if (dialogStateProvider()) {
        OverlayEffect { host ->
            val result = host.showNoticeOverlay(colors = colors, noticeProvider)
            when (result) {
                DialogResult.Confirm -> dismissDialog()
                DialogResult.Cancel -> dismissDialog()
                DialogResult.Dismiss -> dismissDialog()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.Banner(execTaskNum: () -> String, signClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(top = 24.dp)
            .align(alignment = Alignment.CenterHorizontally),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val colors = colors

        Text(
            text = { execTaskNum().toString() },
            style = TextStyle(
                fontSize = 64.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFF2ECC71),
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.tcloud_number_vf))
            )
        )
        Text(
            text = "今日执行", style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            ),
            color = { colors.colorTextSecondary }
        )

        val fabColor = RippleConfiguration(
            color = colors.themeColor, rippleAlpha = RippleAlpha(
                pressedAlpha = 0.32f,
                focusedAlpha = 0.32f,
                draggedAlpha = 0.24f,
                hoveredAlpha = 0.16f
            )
        )
        CompositionLocalProvider(LocalRippleConfiguration provides fabColor) {
            Text(
                text = "立即签到",
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clip(shape = SmootherShape(radius = 24.dp))
                    .drawBehind {
                        drawRect(colors.themeColor.copy(alpha = 0.08f))
                    }
                    .clickable(
                        role = Role.Button,
                        onClick = signClick
                    )
                    .padding(start = 32.dp, top = 10.dp, end = 32.dp, bottom = 10.dp),
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                color = { colors.themeColor }
            )
        }


    }
}

@Composable
private fun GridLayout(
    onNavigateToSign: () -> Unit,
    onNavigateToSetting: () -> Unit,
    onNavigateToAbout: () -> Unit,
    execTaskNum: () -> String
) {
    val colors = colors
    Column(
        modifier = Modifier.padding(top = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CardItem(
                iconColor = Color(0xFF47B6FF),
                Icons.Configuration,
                "签到配置",
                { "已启用 ${execTaskNum()} 项" },
                true,
                onClick = onNavigateToSign
            )
            CardItem(
                iconColor = Color(0xFF8286FF), Icons.Script, "自定义脚本", { "敬请期待" }, false
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp))
                .drawBehind {
                    drawRect(colors.colorBgContainer)
                },
        ) {
            TextItem(iconColor = Color(0xFF60D893), Icons.Setting, "设置", onNavigateToSetting)
            TextItem(iconColor = Color(0xFFFFBC04), Icons.About, "关于", onNavigateToAbout)

        }
    }


}


@Composable
private fun RowScope.CardItem(
    iconColor: Color,
    imageVector: ImageVector,
    text: String,
    subText: () -> String,
    enable: Boolean = true,
    onClick: () -> Unit = {}
) {
    val colors = colors
    val cardColorAlpha: Float by animateFloatAsState(
        targetValue = if (enable) DefaultAlpha else CardDisabledAlpha,
        animationSpec = spring(),
        label = "switch item"
    )
    val textColorAlpha: Float by animateFloatAsState(
        targetValue = if (enable) DefaultAlpha else DisabledAlpha,
        animationSpec = spring(),
        label = "switch item color"
    )

    Column(
        Modifier
            .weight(1f)
            .clip(SmootherShape(12.dp))
            .drawBehind {
                drawRect(colors.colorBgContainer.copy(alpha = cardColorAlpha))
            }
            .clickable(role = Role.Button, enabled = enable, onClick = onClick)
            .padding(top = 24.dp, start = 16.dp, bottom = 24.dp)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "",
            modifier = Modifier
                .size(32.dp)
                .drawBehind {
                    drawCircle(iconColor.copy(cardColorAlpha))
                },
            tint = { Color(0xffffffff) }
        )

        Text(
            text = text,
            Modifier
                .padding(top = 16.dp)
                .graphicsLayer {
                    alpha = textColorAlpha
                },
            style = TextStyle(
                fontSize = 18.sp,
                lineHeight = 21.6.sp,
                fontWeight = FontWeight.Bold
            ),
            color = { colors.colorText },
        )
        Text(
            text = subText,
            Modifier
                .padding(top = 4.dp)
                .graphicsLayer {
                    alpha = textColorAlpha
                },
            style = TextStyle(
                fontSize = 12.sp,
                lineHeight = 14.4.sp,
                fontWeight = FontWeight.Normal
            ),
            color = { colors.colorTextSecondary }
        )
    }
}

@Composable
private fun TextItem(
    iconColor: Color,
    imageVector: ImageVector,
    text: String, onClick: () -> Unit
) {
    val colors = colors
    Row(
        Modifier
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = 15.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "",
            modifier = Modifier
                .size(32.dp)
                .drawBehind {
                    drawCircle(iconColor)
                },
            tint = { Color(0xffffffff) }
        )

        Text(
            text = text, Modifier.padding(start = 16.dp), style = TextStyle(
                fontSize = 18.sp, fontWeight = FontWeight.Bold
            ),
            color = { colors.colorText }
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.ChevronRight,
            contentDescription = "",
            modifier = Modifier.size(24.dp),
            tint = { colors.colorIcon }
        )
    }
}

