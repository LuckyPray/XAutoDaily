//package me.teble.xposed.autodaily.ui
//
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.combinedClickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.WindowInsets
//import androidx.compose.foundation.layout.asPaddingValues
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.navigationBars
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.OutlinedTextField
//import androidx.compose.material.Text
//import androidx.compose.material.icons.materialIcon
//import androidx.compose.material.icons.materialPath
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.ColorFilter
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.core.text.isDigitsOnly
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.rememberNavController
//import me.teble.xposed.autodaily.hook.function.proxy.FunctionPool
//import me.teble.xposed.autodaily.hook.utils.ToastUtil
//import me.teble.xposed.autodaily.task.model.Friend
//import me.teble.xposed.autodaily.task.model.Task
//import me.teble.xposed.autodaily.task.model.TaskEnv
//import me.teble.xposed.autodaily.task.model.TroopInfo
//import me.teble.xposed.autodaily.task.util.ConfigUtil
//import me.teble.xposed.autodaily.task.util.EnvFormatUtil
//import me.teble.xposed.autodaily.utils.LogUtil
//import kotlin.concurrent.thread
//
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun EditEnvLayout(
//    navController: NavHostController,
//    groupId: String?,
//    taskId: String?
//) {
//    var envList by remember { mutableStateOf(emptyList<TaskEnv>()) }
//    var task by remember { mutableStateOf<Task?>(null) }
//    LaunchedEffect(task) {
//        val conf = ConfigUtil.loadSaveConf()
//        conf.taskGroups.forEach { taskGroup ->
//            if (taskGroup.id == groupId) {
//                taskGroup.tasks.forEach {
//                    if (it.id == taskId) {
//                        envList = it.envs ?: emptyList()
//                        task = it
//                        return@LaunchedEffect
//                    }
//                }
//            }
//        }
//        if (envList.isEmpty()) {
//            ToastUtil.send("没有需要编辑的环境变量")
//        }
//    }
//    val envMap = remember { HashMap<String, MutableState<String>>() }
//    var friends by remember { mutableStateOf(emptyList<Friend>()) }
//    var troops by remember { mutableStateOf(emptyList<TroopInfo>()) }
//    val friendFlag = remember { mutableStateOf(false) }
//    val groupFlag = remember { mutableStateOf(false) }
//    LaunchedEffect(envMap) {
//        envList.forEach { env ->
//            envMap[env.name] = mutableStateOf(
//                task!!.getVariable(env.name, EnvFormatUtil.format(env.default, HashMap()))
//            )
//            if (env.type == "friend") {
//                friendFlag.value = true
//            }
//            if (env.type == "group") {
//                groupFlag.value = true
//            }
//        }
//    }
//    LaunchedEffect(friends) {
//        if (friendFlag.value) {
//            thread {
//                friends = FunctionPool.friendsManager.getFriends() ?: emptyList()
//                friendFlag.value = false
//            }
//        }
//    }
//    LaunchedEffect(troops) {
//        if (groupFlag.value) {
//            thread {
//                troops = runCatching {
//                    FunctionPool.troopManager.getTroopInfoList()
//                }.onFailure {
//                    LogUtil.e(it, "获取群列表失败")
//                }.getOrNull() ?: emptyList()
//                groupFlag.value = false
//            }
//        }
//    }
//    val showDialog = remember { mutableStateOf(false) }
//    val taskEnv = remember { mutableStateOf<TaskEnv?>(null) }
//    if (showDialog.value) {
//        when (taskEnv.value?.type) {
//            "friend" -> {
//                FriendsCheckDialog(
//                    friends = friends,
//                    uinListStr = envMap[taskEnv.value?.name]!!,
//                    onConfirm = {
//                        showDialog.value = false
//                    },
//                    onDismiss = { showDialog.value = false }
//                )
//            }
//            "group" -> {
//                TroopsCheckDialog(
//                    troops = troops,
//                    uinListStr = envMap[taskEnv.value?.name]!!,
//                    onConfirm = {
//                        showDialog.value = false
//                    },
//                    onDismiss = { showDialog.value = false }
//                )
//            }
//        }
//    }
//    ActivityView(title = "${taskId}-变量编辑") {
//        Box(Modifier.fillMaxSize()){
//
//            LazyColumn(
//                modifier = Modifier
//                    .padding(top = 13.dp)
//                    .padding(horizontal = 13.dp),
//                contentPadding = WindowInsets.Companion.navigationBars.asPaddingValues(),
//                verticalArrangement = Arrangement.spacedBy(15.dp)
//            ) {
//                envList.forEach { env ->
//                    item {
//                        Column(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .clip(RoundedCornerShape(13.dp))
//                                .background(Color.White)
//                                .combinedClickable(
//                                    onClick = {
//                                        taskEnv.value = env
//                                        when (env.type) {
//                                            "friend" -> {
//                                                if (!friendFlag.value && friends.isNotEmpty()) {
//                                                    showDialog.value = true
//                                                } else {
//                                                    if (friendFlag.value) {
//                                                        ToastUtil.send("好友列表正在获取中，请稍后重试")
//                                                    } else {
//                                                        ToastUtil.send("好友列表为空/获取好友列表失败")
//                                                    }
//                                                }
//                                            }
//                                            "group" -> {
//                                                if (!groupFlag.value && troops.isNotEmpty()) {
//                                                    showDialog.value = true
//                                                } else {
//                                                    if (groupFlag.value) {
//                                                        ToastUtil.send("群组列表正在获取中，请稍后重试")
//                                                    } else {
//                                                        ToastUtil.send("群组列表为空/获取好友列表失败")
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                )
//                                .padding(horizontal = 13.dp, vertical = 5.dp)
//                        ) {
//                            Text(
//                                text = buildString {
//                                    append("变量：${env.name}")
//                                    if (env.type == "friend") {
//                                        append(" (点击打开好友列表)")
//                                    }
//                                    if (env.type == "group") {
//                                        append(" (点击打开群组列表)")
//                                    }
//                                },
//                                fontSize = 18.sp,
//                                modifier = Modifier.padding(8.dp)
//                            )
//                            Text(
//                                text = env.desc,
//                                fontSize = 14.sp,
//                                color = Color(0xFFFF4A4A),
//                                modifier = Modifier.padding(horizontal = 8.dp)
//                            )
//                            var limitErr by remember {
//                                mutableStateOf(false)
//                            }
//                            OutlinedTextField(
//                                value = envMap[env.name]!!.value,
//                                onValueChange = {
//                                    if (env.limit >0) {
//                                        when (env.type) {
//                                            "string" -> {
//                                                limitErr = it.length > env.limit
//                                            }
//                                            "randString" -> {
//                                                var flag = false
//                                                it.split("|").forEach {
//                                                    if (it.length > env.limit) {
//                                                        flag = true
//                                                    }
//                                                }
//                                                limitErr = flag
//                                            }
//                                            "num" -> {
//                                                limitErr = if (it.isNotEmpty()) {
//                                                    if (it.isDigitsOnly()) {
//                                                        it.toInt() > env.limit
//                                                    } else true
//                                                } else {
//                                                    false
//                                                }
//                                            }
//                                            else -> {
//                                                limitErr = it.split(",").size > env.limit
//                                            }
//                                        }
//                                    }
//                                    if (!limitErr) {
//                                        envMap[env.name]?.value = it
//                                    }
//                                },
//                                label = { Text(text = env.name) },
//                                modifier = Modifier
//                                    .padding(horizontal = 10.dp)
//                                    .fillMaxWidth()
//                                    .padding(bottom = 10.dp),
//                                isError = limitErr
//                            )
//                            if (limitErr && env.limit > 0) {
//                                Text(
//                                    text = "变量长度/大小/单段文字限制为${env.limit}",
//                                    color = Color(0xFFFF4A4A),
//                                    fontSize = 14.sp,
//                                    modifier = Modifier.padding(8.dp)
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//
//            Box(modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(30.dp)
//                .padding(bottom = 60.dp)
//                .size(60.dp)
//                .clip(CircleShape)
//                .background(Color(0xFF409EFF))
//                .combinedClickable {
//                    try {
//                        envMap.entries.forEach {
//                            if (it.value.value.isNotEmpty()) {
//                                task?.setVariable(it.key, it.value.value)
//                            } else {
//                                task?.setVariable(it.key, null)
//                            }
//                        }
//                        ToastUtil.send("保存成功")
//                    } catch (e: Throwable) {
//                        ToastUtil.send("保存失败，详情查看日志")
//                        LogUtil.e(e, "保存变量失败：")
//                    }
//                }
//            ) {
//                Image(
//                    Save, "保存",
//                    colorFilter = ColorFilter.tint(Color.White),
//                    modifier = Modifier
//                        .align(Alignment.Center)
//                        .size(35.dp)
//                )
//            }
//        }
//    }
//}
//
//public val Save: ImageVector
//    get() {
//        if (_save != null) {
//            return _save!!
//        }
//        _save = materialIcon(name = "Filled.Save") {
//            materialPath {
//                moveTo(17.0f, 3.0f)
//                lineTo(5.0f, 3.0f)
//                curveToRelative(-1.11f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
//                verticalLineToRelative(14.0f)
//                curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 2.0f, 2.0f)
//                horizontalLineToRelative(14.0f)
//                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
//                lineTo(21.0f, 7.0f)
//                lineToRelative(-4.0f, -4.0f)
//                close()
//                moveTo(12.0f, 19.0f)
//                curveToRelative(-1.66f, 0.0f, -3.0f, -1.34f, -3.0f, -3.0f)
//                reflectiveCurveToRelative(1.34f, -3.0f, 3.0f, -3.0f)
//                reflectiveCurveToRelative(3.0f, 1.34f, 3.0f, 3.0f)
//                reflectiveCurveToRelative(-1.34f, 3.0f, -3.0f, 3.0f)
//                close()
//                moveTo(15.0f, 9.0f)
//                lineTo(5.0f, 9.0f)
//                lineTo(5.0f, 5.0f)
//                horizontalLineToRelative(10.0f)
//                verticalLineToRelative(4.0f)
//                close()
//            }
//        }
//        return _save!!
//    }
//
//private var _save: ImageVector? = null
//
//@Preview
//@Composable
//fun PreviewEditEnvLayout() {
//    EditEnvLayout(
//        navController = rememberNavController(),
//        groupId = "会员相关",
//        taskId = "会员排行榜"
//    )
//}