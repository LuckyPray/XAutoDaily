package me.teble.xposed.autodaily.ui.scene

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import me.teble.xposed.autodaily.ui.NavigationItem
import me.teble.xposed.autodaily.ui.composable.SmallTitle
import me.teble.xposed.autodaily.ui.composable.SwitchInfoDivideItem
import me.teble.xposed.autodaily.ui.composable.SwitchInfoItem
import me.teble.xposed.autodaily.ui.composable.SwitchTextDivideItem
import me.teble.xposed.autodaily.ui.composable.SwitchTextItem
import me.teble.xposed.autodaily.ui.composable.TopBar
import me.teble.xposed.autodaily.ui.enable
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.layout.bottomPaddingValue
import me.teble.xposed.autodaily.ui.navigate
import me.teble.xposed.autodaily.ui.theme.DefaultAlpha
import me.teble.xposed.autodaily.ui.theme.DisabledAlpha

@Composable
fun SignScene(navController: NavController, signViewModel: SignViewModel = viewModel()) {
    Scaffold(
        topBar = {
            TopBar(
                text = "签到配置",
                backClick = {
                    navController.popBackStack()
                })
        },
        backgroundColor = Color(0xFFF7F7F7)
    ) { contentPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F7F7))
                .padding(contentPadding)
        ) {
            val globalEnable by signViewModel.globalEnable.collectAsState()
            SwitchTextItem(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clip(SmootherShape(12.dp))
                    .background(color = Color(0xffffffff)),
                text = "总开关",
                onClick = {
                    signViewModel.updateGlobalEnable(it)
                },
                clickEnabled = true,
                enable = globalEnable
            )
            GroupColumn(navController, enable = globalEnable)
        }
    }
}

@Composable
private fun GroupColumn(
    navController: NavController,
    signViewModel: SignViewModel = viewModel(),
    enable: Boolean
) {

    val taskGroups by signViewModel.taskGroupsState.collectAsState()
    LazyColumn(
        modifier = Modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .clip(SmootherShape(12.dp)),
        contentPadding = bottomPaddingValue,
        // 绘制间隔
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {

        items(items = taskGroups, key = { it.id }, contentType = { it.id }) { taskGroup ->

            val groupTitle by remember {
                derivedStateOf {
                    taskGroup.id
                }
            }

            SmallTitle(
                title = groupTitle,
                modifier = Modifier
                    .padding(bottom = 8.dp, start = 16.dp),
            )

            val tasks by remember {
                derivedStateOf {
                    taskGroup.tasks
                }
            }

            val itemAlpha: Float by animateFloatAsState(
                targetValue = if (enable) DefaultAlpha else DisabledAlpha,
                animationSpec = spring(), label = "switch item"
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(SmootherShape(12.dp))
                    .background(color = Color(0xffffffff).copy(alpha = itemAlpha)),
            ) {
                tasks.forEach { task ->

                    val checked = remember {
                        derivedStateOf {
                            task.enable
                        }
                    }
                    val clickFlag by remember {
                        derivedStateOf {
                            !task.envs.isNullOrEmpty()
                        }
                    }
                    val desc by remember {
                        derivedStateOf {
                            task.desc
                        }
                    }
                    val title by remember {
                        derivedStateOf {
                            task.id
                        }
                    }

                    if (desc.isNotBlank()) {
                        if (clickFlag) {
                            SwitchInfoDivideItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(SmootherShape(12.dp)),
                                enable = checked.value,
                                text = title,
                                infoText = desc,
                                clickEnabled = enable,
                                onClick = {
                                    navController.navigate(
                                        NavigationItem.EditEnv(
                                            taskGroup.id,
                                            task.id
                                        ),
                                        NavigationItem.Sign
                                    )
                                },
                                onChange = {

                                }
                            )
                        } else {
                            SwitchInfoItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(SmootherShape(12.dp)),
                                enable = checked.value,
                                clickEnabled = enable,
                                text = title,
                                infoText = desc,
                                onClick = {

                                }
                            )
                        }


                    } else {
                        if (clickFlag) {
                            SwitchTextItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(SmootherShape(12.dp)),
                                enable = checked.value,
                                text = title,
                                clickEnabled = enable,
                                onClick = {

                                }
                            )
                        } else {
                            SwitchTextDivideItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(SmootherShape(12.dp)),
                                enable = checked.value,
                                text = title,
                                clickEnabled = enable,
                                onClick = {

                                }
                            )
                        }


                    }


                }
            }

        }
    }

}