package me.teble.xposed.autodaily.ui.scene

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize
import me.teble.xposed.autodaily.data.Dependency
import me.teble.xposed.autodaily.ui.composable.Text
import me.teble.xposed.autodaily.ui.composable.XaScaffold
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme


@Parcelize
data object LicenseScreen : Screen {
    data class State(
        private val eventSink: (Event) -> Unit,
        val backClick: () -> Unit = { eventSink(Event.BackClicked) },
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data object BackClicked : Event()
    }
}

class LicensePresenter(
    private val screen: LicenseScreen,
    private val navigator: Navigator,
) : Presenter<LicenseScreen.State> {

    class Factory() : Presenter.Factory {
        override fun create(
            screen: Screen,
            navigator: Navigator,
            context: CircuitContext
        ): Presenter<*>? {
            return when (screen) {
                is LicenseScreen -> return LicensePresenter(screen, navigator)
                else -> null
            }
        }
    }

    @Composable
    override fun present(): LicenseScreen.State {
        return LicenseScreen.State(
            eventSink = { event ->
                when (event) {
                    LicenseScreen.Event.BackClicked -> navigator.pop()
                }
            }

        )
    }
}

@Composable
fun LicenseUI(
    backClick: () -> Unit,
    modifier: Modifier,
    viewmodel: LicenseViewModel = viewModel()
) {
    XaScaffold(
        text = "开放源代码许可",
        backClick = backClick,
        modifier = modifier,
        containerColor = XAutodailyTheme.colors.colorBgLayout
    ) {
        val dependencies = remember {
            viewmodel.dependencies
        }
        Column(
            modifier = Modifier
                .weight(1f, false)
                .padding(top = 16.dp)
                .clip(SmootherShape(12.dp))
                .verticalScroll(rememberScrollState())
                .defaultNavigationBarPadding(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            dependencies.forEach { dependency ->
                DependencyItem(dependency)
            }
        }


    }
}

@Composable
fun DependencyItem(dependency: Dependency) {

    val colors = XAutodailyTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .drawBehind {
                drawRect(colors.colorBgContainer)
            }
            .padding(vertical = 20.dp, horizontal = 16.dp),
    ) {
        Text(
            text = dependency.name,
            maxLines = 1,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            color = { colors.colorText }
        )

        Text(
            text = dependency.licenses.joinToString(",") {
                it.name
            },
            Modifier.padding(top = 6.dp),
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            ),
            color = {
                colors.colorTextSecondary
            }
        )

    }
}