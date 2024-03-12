package me.teble.xposed.autodaily.ui.scene

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.parcelize.Parcelize
import me.teble.xposed.autodaily.task.model.TaskGroup
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.ui.composable.SmallTitle
import me.teble.xposed.autodaily.ui.composable.SwitchInfoDivideItem
import me.teble.xposed.autodaily.ui.composable.SwitchInfoItem
import me.teble.xposed.autodaily.ui.composable.SwitchTextDivideItem
import me.teble.xposed.autodaily.ui.composable.SwitchTextItem
import me.teble.xposed.autodaily.ui.composable.XaScaffold
import me.teble.xposed.autodaily.ui.enable
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.DefaultAlpha
import me.teble.xposed.autodaily.ui.theme.DisabledAlpha
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors

@Parcelize
data object SignScreen : Screen {
    data class State(
        val eventSink: (Event) -> Unit,
        val backClick: () -> Unit = { eventSink(Event.BackClicked) },
        val onNavigateToEditEnvs: (String?, String) -> Unit = { groupId, taskId ->
            eventSink(
                Event.EditEnv(
                    groupId,
                    taskId
                )
            )
        }
    ) : CircuitUiState

    sealed class Event : CircuitUiEvent {
        data object BackClicked : Event()
        data class EditEnv(
            val groupId: String?,
            val taskId: String
        ) : Event()
    }
}

class SignPresenter(
    private val screen: SignScreen,
    private val navigator: Navigator,
) : Presenter<SignScreen.State> {

    class Factory() : Presenter.Factory {
        override fun create(
            screen: Screen,
            navigator: Navigator,
            context: CircuitContext
        ): Presenter<*>? {
            return when (screen) {
                is SignScreen -> return SignPresenter(screen, navigator)
                else -> null
            }
        }
    }

    @Composable
    override fun present(): SignScreen.State {
        return SignScreen.State(
            eventSink = { event ->
                when (event) {
                    SignScreen.Event.BackClicked -> navigator.pop()
                    is SignScreen.Event.EditEnv -> navigator.goTo(
                        EditEnvScreen(
                            groupId = event.groupId,
                            taskId = event.taskId

                        )
                    )
                }
            }

        )
    }
}

@Composable
fun counterPresenter(
    events: Flow<Unit>
): () -> Boolean {
    var globalEnable by remember { mutableStateOf(ConfUnit.globalEnable) }

    LaunchedEffect(Unit) {
        events.collect {
            globalEnable = !globalEnable
            ConfUnit.globalEnable = globalEnable
        }
    }

    return { globalEnable }
}

@Composable
fun SignUI(
    backClick: () -> Unit,
    onNavigateToEditEnvs: (taskGroup: String, taskId: String) -> Unit,
    modifier: Modifier
) {
    val colors = colors
    XaScaffold(
        text = "签到配置",
        backClick = backClick,
        modifier = modifier,
        containerColor = colors.colorBgLayout
    ) {
        val channel = remember { Channel<Unit>() }

        val flow = remember(channel) { channel.consumeAsFlow() }
        val globalEnable = counterPresenter(flow)

        val taskGroupsState =
            remember { mutableStateListOf(*ConfigUtil.loadSaveConf().taskGroups.toTypedArray()) }
        SwitchTextItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp))
                .drawBehind {
                    drawRect(colors.colorBgContainer)
                },
            text = "总开关",
            onClick = { channel.trySend(Unit) },
            clickEnabled = { true },
            enable = globalEnable
        )
        GroupColumn(
            onNavigateToEditEnvs = onNavigateToEditEnvs,
            taskGroupList = { taskGroupsState },
            enable = globalEnable
        )
    }

}

@Composable
private fun ColumnScope.GroupColumn(
    onNavigateToEditEnvs: (taskGroup: String, taskId: String) -> Unit,
    taskGroupList: () -> List<TaskGroup>,
    enable: () -> Boolean
) {
    Column(
        modifier = Modifier
            .weight(1f, false)
            .padding(top = 16.dp)
            .clip(SmootherShape(12.dp))
            .verticalScroll(rememberScrollState())
            .defaultNavigationBarPadding(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        taskGroupList().forEach { taskGroup ->
            SignItem(onNavigateToEditEnvs, taskGroup, enable)
        }

    }
}

@Composable
private fun SignItem(
    onNavigateToEditEnvs: (taskGroup: String, taskId: String) -> Unit,
    taskGroup: TaskGroup,
    enable: () -> Boolean
) {
    val groupTitle by remember {
        derivedStateOf {
            taskGroup.id
        }
    }


    val tasks by remember {
        derivedStateOf {
            taskGroup.tasks
        }
    }

    val itemAlpha: Float by animateFloatAsState(
        targetValue = if (enable()) DefaultAlpha else DisabledAlpha,
        animationSpec = spring(), label = "switch item"
    )

    val colors = colors

    Column {
        SmallTitle(
            title = groupTitle,
            modifier = Modifier
                .padding(bottom = 8.dp, start = 16.dp),
        )


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SmootherShape(12.dp))
                .drawBehind {
                    drawRect(colors.colorBgContainer)
                }
                .graphicsLayer {
                    alpha = itemAlpha
                }
        ) {
            tasks.forEach { task ->

                var checked by remember { mutableStateOf(task.enable) }
                val clickFlag by remember {
                    derivedStateOf { !task.envs.isNullOrEmpty() }
                }
                val desc by remember { derivedStateOf { task.desc } }
                val title by remember { derivedStateOf { task.id } }

                if (desc.isNotBlank()) {
                    if (clickFlag) {
                        SwitchInfoDivideItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(SmootherShape(12.dp)),
                            enable = { checked },
                            text = title,
                            infoText = desc,
                            clickEnabled = enable,
                            onClick = {
                                onNavigateToEditEnvs(taskGroup.id, task.id)
                            },
                            onChange = {
                                checked = it
                                task.enable = it

                            }
                        )
                    } else {
                        SwitchInfoItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(SmootherShape(12.dp)),
                            enable = { checked },
                            clickEnabled = enable,
                            text = title,
                            infoText = desc,
                            onClick = {
                                checked = it
                                task.enable = it
                            }
                        )
                    }


                } else {
                    if (clickFlag) {
                        SwitchTextItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(SmootherShape(12.dp)),
                            enable = { checked },
                            text = title,
                            clickEnabled = enable,
                            onClick = {
                                checked = it
                                task.enable = it
                            }
                        )
                    } else {
                        SwitchTextDivideItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(SmootherShape(12.dp)),
                            enable = { checked },
                            text = title,
                            clickEnabled = enable,
                            onClick = {
                                checked = it
                                task.enable = it
                            }
                        )
                    }


                }
            }

        }
    }
}
