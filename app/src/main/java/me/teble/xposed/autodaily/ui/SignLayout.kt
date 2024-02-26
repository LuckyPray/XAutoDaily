//package me.teble.xposed.autodaily.ui
//
//import android.annotation.SuppressLint
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material.Divider
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavHostController
//import me.teble.xposed.autodaily.hook.utils.ToastUtil
//import me.teble.xposed.autodaily.task.model.TaskProperties
//import me.teble.xposed.autodaily.task.util.ConfigUtil
//import me.teble.xposed.autodaily.ui.XAutoDailyApp.EditEnv
//import me.teble.xposed.autodaily.ui.XAutoDailyApp.Sign
//
//@SuppressLint("MutableCollectionMutableState")
//@Composable
//fun SignLayout(navController: NavHostController) {
//    ActivityView(title = "签到设置") {
//        var conf by remember {
//            mutableStateOf(TaskProperties(0, 0, emptyList(), emptyList()))
//        }
//        val errorMap by remember {
//            mutableStateOf(mutableMapOf<String, String>())
//        }
//        LaunchedEffect(errorMap) {
//            conf = ConfigUtil.loadSaveConf()
//            conf.taskGroups.forEach { taskGroup ->
//                taskGroup.tasks.forEach {
//                    if (it.errInfo.count >= 3) {
//                        if (it.retryCount >= 3) {
//                            errorMap[it.id] = "任务执行失败次数过多，今日暂停执行，可能存在功能不兼容"
//                        } else {
//                            errorMap[it.id] = "任务执行失败次数超过3次，本次启动暂停执行，可长按重置执行记录"
//                        }
//                    }
//                }
//            }
//        }
//        LazyColumn(
//            modifier = Modifier
//                .padding(top = 13.dp)
//                .padding(horizontal = 13.dp),
//            contentPadding = WindowInsets.Companion.navigationBars.asPaddingValues(),
//            // 绘制间隔
//            verticalArrangement = Arrangement.spacedBy(10.dp),
//        ) {
//            conf.taskGroups.forEach { taskGroup ->
//                item {
//                    GroupList(title = taskGroup.id) {
//                        taskGroup.tasks.forEach { task ->
//                            val checked = remember { mutableStateOf(task.enable) }
//                            val clickFlag = remember { !task.envs.isNullOrEmpty() }
//                            var desc by remember { mutableStateOf("") }
//                            var title by remember { mutableStateOf("") }
//                            var lastExecTime by remember { mutableStateOf("") }
//                            var lastExecMsg by remember { mutableStateOf("") }
//                            var nextShouldExecTime by remember { mutableStateOf("") }
//                            LaunchedEffect(checked) {
//                                title = buildString {
//                                    append(task.id)
//                                    if (clickFlag) {
//                                        append(" (点击卡片打开配置)")
//                                    }
//                                }
//                                desc = task.desc
//                                lastExecTime = task.lastExecTime ?: "从未执行"
//                                lastExecMsg = task.lastExecMsg ?: ""
//                                nextShouldExecTime = task.nextShouldExecTime ?: "从未执行"
//                            }
//                            // 绘制分割线
//                            Divider(color = Color(color = 0xFFF2F2F2), thickness = 1.dp)
//                            LineSwitch(
//                                title = title,
//                                desc = desc,
//                                checked = checked,
//                                onChange = {
//                                    task.enable = it
//                                },
//                                longPress = {
//                                    ToastUtil.send("正在重置上次执行时间")
//                                    task.reset()
//                                    lastExecTime = "从未执行"
//                                    lastExecMsg = ""
//                                    nextShouldExecTime = "从未执行"
//                                    if (errorMap.containsKey(task.id)) {
//                                        errorMap.remove(task.id)
//                                    }
//                                },
//                                otherInfoList = (mutableListOf<String>().apply {
//                                    if (!checked.value) {
//                                        return@apply
//                                    }
//                                    if (errorMap.containsKey(task.id)) {
//                                        add(errorMap[task.id]!!)
//                                    }
//                                    add("上次: $lastExecTime, 响应: $lastExecMsg")
//                                    add("下次: $nextShouldExecTime")
//                                }),
//                                onClick = {
//                                    if (clickFlag) {
//                                        navController.navigate(
//                                            route = "${EditEnv}/${taskGroup.id}/${task.id}"
//                                        ) {
//                                            popUpTo(Sign)
//                                        }
//                                    }
//                                }
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Preview
//@Composable
//fun PreviewSignLayout() {
//    ActivityView(title = "签到设置") {
//        LazyColumn(
//            modifier = Modifier.padding(13.dp)
//        ) {
//            item {
////                GroupList(title = "续火相关") {
////                    Divider(color = Color(color = 0xFFF2F2F2), thickness = 1.dp)
////                    LineSwitch(
////                        title = "好友自动续火",
////                        desc = "点按打开好友续火配置",
////                        checked = remember { mutableStateOf(false) }
////                    )
////                    Divider(color = Color(color = 0xFFF2F2F2), thickness = 1.dp)
////                    LineSwitch(
////                        title = "好友自动续火",
////                        desc = "点按打开好友续火配置",
////                        checked = remember { mutableStateOf(false) }
////                    )
////                }
//            }
//        }
//    }
//}