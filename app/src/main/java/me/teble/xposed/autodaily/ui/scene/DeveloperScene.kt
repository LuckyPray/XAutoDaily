package me.teble.xposed.autodaily.ui.scene

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import me.teble.xposed.autodaily.R
import me.teble.xposed.autodaily.ui.composable.ImageItem
import me.teble.xposed.autodaily.ui.composable.RoundedSnackbarHost
import me.teble.xposed.autodaily.ui.composable.TopBar
import me.teble.xposed.autodaily.ui.composable.XaScaffold
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors
import me.teble.xposed.autodaily.utils.openUrl

@Parcelize
data object DeveloperScreen : Screen {
    @Stable
    data class State(
        private val eventSink: (Event) -> Unit,

        val snackbarHostState: SnackbarHostState,
        val backClick: () -> Unit = { eventSink(Event.BackClicked) },

        val openAuthorGithub: (String) -> Unit,
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data object BackClicked : Event
    }
}

class DeveloperPresenter(
    private val screen: DeveloperScreen,
    private val navigator: Navigator,
) : Presenter<DeveloperScreen.State> {

    class Factory() : Presenter.Factory {
        override fun create(
            screen: Screen,
            navigator: Navigator,
            context: CircuitContext
        ): Presenter<*>? {
            return when (screen) {
                is DeveloperScreen -> return DeveloperPresenter(screen, navigator)
                else -> null
            }
        }
    }

    @Stable
    @Composable
    override fun present(): DeveloperScreen.State {
        val context = LocalContext.current
        val scope = rememberStableCoroutineScope()

        val snackbarHostState = remember { SnackbarHostState() }

        suspend fun showSnackbar(text: String) {
            snackbarHostState.showSnackbar(text)
        }
        return DeveloperScreen.State(
            snackbarHostState = snackbarHostState,
            eventSink = { event ->
                when (event) {
                    DeveloperScreen.Event.BackClicked -> navigator.pop()
                }
            },
            openAuthorGithub = {
                scope.launch {
                    showSnackbar("正在跳转，请稍后")
                    context.openUrl(it)
                }

            }

        )
    }
}

@Composable
fun DeveloperUI(
    backClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    openAuthorGithub: (String) -> Unit,
    modifier: Modifier,
) {

    XaScaffold(
        topBar = {
            TopBar(text = "开发者", backClick = backClick)
        },
        modifier = modifier,
        snackbarHost = {
            RoundedSnackbarHost(hostState = snackbarHostState)
        },
        containerColor = colors.colorBgLayout
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .clip(SmootherShape(12.dp))
                .verticalScroll(rememberScrollState())
                .defaultNavigationBarPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthorLayout(
                openAuthorGithub = openAuthorGithub
            )
        }
    }
}


@Composable
private fun AuthorLayout(
    openAuthorGithub: (String) -> Unit
) {

    val colors = colors
    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .drawBehind {
                drawRect(colors.colorBgContainer)
            },
    ) {
        AuthorItem(
            icon = painterResource(id = R.drawable.teble),
            contentDescription = "韵の祈",
            title = "韵の祈",
            info = "主要开发者"
        ) {
            openAuthorGithub("https://github.com/teble")
        }
        AuthorItem(
            icon = painterResource(id = R.drawable.lagrio),
            contentDescription = "MaiTungTM",
            title = "MaiTungTM",
            info = "UI & Icon 设计师"
        ) {
            openAuthorGithub("https://github.com/Lagrio")
        }
        AuthorItem(
            icon = painterResource(id = R.drawable.agoines),
            contentDescription = "agoines",
            title = "Agoines",
            info = "UI 开发者"
        ) {
            openAuthorGithub("https://github.com/agoines")
        }
    }
}

@Composable
private fun AuthorItem(
    icon: Painter,
    contentDescription: String,
    title: String,
    info: String,
    onClick: () -> Unit = {}
) {
    ImageItem(
        icon = icon,
        contentDescription = contentDescription,
        title = title,
        info = info,
        clickEnabled = { true },
        shape = SmootherShape(12.dp),
        onClick = onClick

    )
}