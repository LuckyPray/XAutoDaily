package me.teble.xposed.autodaily.ui.scene

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.ui.composable.SmallTitle
import me.teble.xposed.autodaily.ui.composable.Text
import me.teble.xposed.autodaily.ui.composable.XaScaffold
import me.teble.xposed.autodaily.ui.enable
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.lastExecMsg
import me.teble.xposed.autodaily.ui.lastExecTime
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.nextShouldExecTime
import me.teble.xposed.autodaily.ui.reset
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors


@Composable
fun SignStateScene(
    backClick: () -> Unit
) {
    val colors = colors
    XaScaffold(
        text = "签到状态",
        backClick = backClick,
        containerColor = colors.colorBgLayout
    ) {

        val turnTaskState = remember {
            mutableStateListOf(
                *ConfigUtil.loadSaveConf().taskGroups.map { it.tasks }.flatten().toTypedArray()
            ).filter { it.enable }

        }
        AnimatedVisibility(
            turnTaskState.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Column(Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)) {
                SmallTitle(title = "已启用的项目", modifier = Modifier)
                Text(
                    text = "长按任务可重置执行记录",
                    modifier = Modifier.padding(top = 4.dp),
                    style = TextStyle(fontSize = 12.sp),
                    color = { colors.colorTextSecondary },
                )
            }
        }
        Column(
            modifier = Modifier
                .clip(SmootherShape(12.dp))
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .defaultNavigationBarPadding()
                .background(colors.colorBgContainer, SmootherShape(12.dp)),
        ) {

            turnTaskState.forEach { task ->
                var resetVersion by remember(task.id) { mutableIntStateOf(0) }
                // 执行记录保存在 MMKV 中，重置后通过版本变化立即刷新当前行。
                val lastExecTime = remember(task.id, resetVersion) {
                    task.lastExecTime ?: "从未执行"
                }
                val lastExecMsg = remember(task.id, resetVersion) {
                    task.lastExecMsg ?: "从未执行"
                }
                val nextShouldExecTime = remember(task.id, resetVersion) {
                    task.nextShouldExecTime ?: "从未执行"
                }

                StateItem(
                    text = task.id, infoText =
                    "上次: $lastExecTime, 响应: $lastExecMsg\n" +
                            "下次: $nextShouldExecTime",
                    onLongClick = {
                        task.reset()
                        resetVersion++
                        ToastUtil.send("已重置执行记录")
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StateItem(
    modifier: Modifier = Modifier,
    text: String,
    infoText: String,
    onLongClick: () -> Unit,
) {
    val colors = colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .combinedClickable(
                onClick = {},
                onLongClickLabel = "重置执行记录",
                onLongClick = onLongClick,
            )
            .padding(vertical = 20.dp, horizontal = 16.dp)
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
