package me.teble.xposed.autodaily.ui.scene

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize
import me.teble.xposed.autodaily.task.model.Task
import me.teble.xposed.autodaily.ui.composable.SmallTitle
import me.teble.xposed.autodaily.ui.composable.Text
import me.teble.xposed.autodaily.ui.composable.XaScaffold
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.lastExecMsg
import me.teble.xposed.autodaily.ui.lastExecTime
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.nextShouldExecTime
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors


@Parcelize
data object SignStateScreen : Screen {
    data class State(
        val eventSink: (Event) -> Unit,
        val backClick: () -> Unit = { eventSink(Event.BackClicked) },
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data object BackClicked : Event()
    }
}

class SignStatePresenter(
    private val screen: SignStateScreen,
    private val navigator: Navigator,
) : Presenter<SignStateScreen.State> {

    class Factory() : Presenter.Factory {
        override fun create(
            screen: Screen,
            navigator: Navigator,
            context: CircuitContext
        ): Presenter<*>? {
            return when (screen) {
                is SignStateScreen -> return SignStatePresenter(screen, navigator)
                else -> null
            }
        }
    }

    @Composable
    override fun present(): SignStateScreen.State {
        return SignStateScreen.State(
            eventSink = { event ->
                when (event) {
                    SignStateScreen.Event.BackClicked -> navigator.pop()
                }
            }

        )
    }
}

@Composable
fun SignStateUI(
    backClick: () -> Unit,
    modifier: Modifier
) {
    XaScaffold(
        text = "签到状态",
        backClick = backClick,
        modifier = modifier,
        containerColor = colors.colorBgLayout
    ) {
        val tasksState = remember {
            mutableStateListOf<Task>()
        }


        AnimatedVisibility(
            tasksState.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            SmallTitle(
                title = "已启用的项目",
                modifier = Modifier
                    .padding(bottom = 8.dp, start = 16.dp, top = 8.dp)
            )
        }
        val colors = colors

        Column(
            modifier = Modifier
                .clip(SmootherShape(12.dp))
                .drawBehind {
                    drawRect(colors.colorBgContainer)
                }
                .verticalScroll(rememberScrollState())
                .defaultNavigationBarPadding(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {

            tasksState.forEach { task ->
                val title by remember {
                    derivedStateOf { task.id }
                }

                val lastExecTime by remember {
                    derivedStateOf { task.lastExecTime ?: "从未执行" }
                }


                val lastExecMsg by remember {
                    derivedStateOf { task.lastExecMsg ?: "从未执行" }
                }

                val nextShouldExecTime by remember {
                    derivedStateOf { task.nextShouldExecTime ?: "从未执行" }
                }

                StateItem(
                    text = title, infoText =
                    "上次: $lastExecTime, 响应: $lastExecMsg\n" +
                            "下次: $nextShouldExecTime"
                )
            }
        }
    }
}

@Composable
private fun StateItem(
    modifier: Modifier = Modifier,
    text: String,
    infoText: String,
) {
    val colors = colors
    Column(
        modifier = modifier.padding(vertical = 20.dp, horizontal = 16.dp)
    ) {
        Text(
            text = text,
            maxLines = 1,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            color = { colors.colorText }
        )

        Text(
            text = infoText,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            ),
            color = { colors.colorTextSecondary }
        )
    }
}