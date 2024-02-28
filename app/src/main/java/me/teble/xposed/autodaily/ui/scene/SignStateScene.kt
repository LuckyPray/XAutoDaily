package me.teble.xposed.autodaily.ui.scene

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import me.teble.xposed.autodaily.ui.composable.SmallTitle
import me.teble.xposed.autodaily.ui.composable.TopBar
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.lastExecMsg
import me.teble.xposed.autodaily.ui.lastExecTime
import me.teble.xposed.autodaily.ui.layout.bottomPaddingValue
import me.teble.xposed.autodaily.ui.nextShouldExecTime
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme

@Composable
fun SignStateScene(
    navController: NavController,
    signStateViewModel: SignStateViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopBar(
                text = "签到状态",
                backClick = {
                    navController.popBackStack()
                })
        },
        containerColor = XAutodailyTheme.colors.colorBgLayout
    ) { contentPadding ->
        val tasksState by signStateViewModel.tasksState.collectAsState()


        Column(modifier = Modifier.padding(horizontal = 16.dp)) {

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

            LazyColumn(
                modifier = Modifier
                    .padding(contentPadding)
                    .clip(SmootherShape(12.dp))
                    .background(XAutodailyTheme.colors.colorBgContainer),
                contentPadding = bottomPaddingValue,
            ) {

                items(tasksState, key = { it.id }, contentType = { it.id }) { task ->
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
}

@Composable
private fun StateItem(
    modifier: Modifier = Modifier,
    text: String,
    infoText: String,
) {
    Column(
        modifier = modifier.padding(vertical = 20.dp, horizontal = 16.dp)
    ) {
        Text(
            text = text,
            maxLines = 1,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = XAutodailyTheme.colors.colorText
            )
        )

        Text(
            text = infoText,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = XAutodailyTheme.colors.colorTextSecondary
            )
        )
    }
}