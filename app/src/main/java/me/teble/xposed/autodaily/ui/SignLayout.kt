package me.teble.xposed.autodaily.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import me.teble.xposed.autodaily.hook.config.Config.accountConfig
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.task.util.Const.ENABLE
import me.teble.xposed.autodaily.task.util.Const.LAST_EXEC_MSG
import me.teble.xposed.autodaily.task.util.Const.LAST_EXEC_TIME
import me.teble.xposed.autodaily.task.util.Const.NEXT_SHOULD_EXEC_TIME
import me.teble.xposed.autodaily.ui.XAutoDailyApp.EditEnv
import me.teble.xposed.autodaily.ui.XAutoDailyApp.Sign

@Composable
fun SignLayout(navController: NavHostController) {
    ActivityView(title = "签到设置", navController = navController) {
        val conf by remember {
            mutableStateOf(ConfigUtil.loadSaveConf())
        }
        LazyColumn(
            modifier = Modifier
                .padding(top = 13.dp)
                .padding(horizontal = 13.dp),
            // 绘制间隔
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            conf.taskGroups.forEach {taskGroup ->
                item {
                    GroupList(title = taskGroup.id) {
                        taskGroup.tasks.forEach { task ->
                            val checked = remember {mutableStateOf(
                                accountConfig.getBoolean("${task.id}#${ENABLE}", false)
                            )}
                            // 绘制分割线
                            Divider(color = Color(color = 0xFFF2F2F2), thickness = 1.dp)
                            val clickFlag = remember { task.envs != null && task.envs.isNotEmpty() }
                            LineSwitch(
                                title = buildString {
                                    append(task.id)
                                    if (clickFlag) {
                                        append(" (点击卡片打开配置)")
                                    }
                                },
                                desc = task.desc,
                                checked = checked,
                                onChange = {
                                    ConfigUtil.changeSignButton("${task.id}#${ENABLE}", it)
                                },
                                longPress = {
                                    ToastUtil.send("正在重置上次执行时间")
                                    accountConfig.remove("${task.id}#${LAST_EXEC_TIME}")
                                    accountConfig.remove("${task.id}#${LAST_EXEC_MSG}")
                                    accountConfig.remove("${task.id}#${NEXT_SHOULD_EXEC_TIME}")
                                },
                                otherInfoList = (mutableListOf<String>().apply {
                                    if (!checked.value) {
                                        return@apply
                                    }
                                    val lastExecTime =
                                        accountConfig.getString("${task.id}#${LAST_EXEC_TIME}")
                                            ?: "从未执行"
                                    val lastExecMsg =
                                        accountConfig.getString("${task.id}#${LAST_EXEC_MSG}")
                                    val nextShouldExecTime =
                                        accountConfig.getString("${task.id}#${NEXT_SHOULD_EXEC_TIME}")
                                            ?: "从未执行"
                                    add("上次: $lastExecTime, 响应: $lastExecMsg")
                                    add("下次: $nextShouldExecTime")
                                }),
                                onClick = {
                                    if (clickFlag) {
                                        navController.navigate(
                                            route = "${EditEnv}/${taskGroup.id}/${task.id}"
                                        ) {
                                            popUpTo(Sign)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSignLayout() {
    ActivityView(title = "签到设置", navController = rememberNavController()) {
        LazyColumn(
            modifier = Modifier.padding(13.dp)
        ) {
            item {
                GroupList(title = "续火相关") {
                    Divider(color = Color(color = 0xFFF2F2F2), thickness = 1.dp)
                    LineSwitch(
                        title = "好友自动续火",
                        desc = "点按打开好友续火配置",
                        checked = remember { mutableStateOf(false) }
                    )
                    Divider(color = Color(color = 0xFFF2F2F2), thickness = 1.dp)
                    LineSwitch(
                        title = "好友自动续火",
                        desc = "点按打开好友续火配置",
                        checked = remember { mutableStateOf(false) }
                    )
                }
            }
        }
    }
}