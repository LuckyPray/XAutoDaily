package me.teble.xposed.autodaily.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import me.teble.xposed.autodaily.task.model.TaskEnv
import me.teble.xposed.autodaily.hook.function.proxy.FunctionPool
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.task.model.Friend
import me.teble.xposed.autodaily.task.model.TroopInfo
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.task.util.Const.ENV_VARIABLE
import me.teble.xposed.autodaily.ui.Cache.currConf
import me.teble.xposed.autodaily.utils.LogUtil
import kotlin.concurrent.thread

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditEnvLayout(
    navController: NavHostController,
    groupId: String?,
    taskId: String?
) {
    var envList by remember { mutableStateOf(emptyList<TaskEnv>()) }
    LaunchedEffect(envList) {
        LogUtil.log("--------------- init envList ---------------")
        val conf = ConfigUtil.loadSaveConf()
        conf.taskGroups.forEach {
            if (it.id == groupId) {
                it.tasks.forEach { task ->
                    if (task.id == taskId) {
                        envList = task.envs ?: emptyList()
                        return@LaunchedEffect
                    }
                }
            }
        }
        if (envList.isEmpty()) {
            ToastUtil.send("没有需要编辑的环境变量")
        }
    }
    val envMap = remember { HashMap<String, MutableState<String>>() }
    var friends by remember { mutableStateOf(emptyList<Friend>()) }
    var troops by remember { mutableStateOf(emptyList<TroopInfo>()) }
    BackHandler(onBack = {
        envMap.entries.forEach {
            if (it.value.value.isNotEmpty()) {
                currConf.putString("$taskId#$ENV_VARIABLE#${it.key}", it.value.value)
            } else {
                currConf.remove("$taskId#$ENV_VARIABLE#${it.key}")
            }
        }
        ToastUtil.send("保存成功")
        navController.popBackStack()
    })
    val friendFlag = remember { mutableStateOf(false) }
    val groupFlag = remember { mutableStateOf(false) }
    LaunchedEffect(envMap) {
        envList.forEach { env ->
            envMap[env.name] = mutableStateOf(
                currConf.getString("$taskId#$ENV_VARIABLE#${env.name}", env.default)
            )
            if (env.type == "friend") {
                friendFlag.value = true
            }
            if (env.type == "group") {
                groupFlag.value = true
            }
        }
    }
    LaunchedEffect(friends) {
        if (friendFlag.value) {
            thread {
                friends = FunctionPool.friendsManager.getFriends() ?: emptyList()
                friendFlag.value = false
            }
        }
    }
    LaunchedEffect(troops) {
        if (groupFlag.value) {
            thread {
                troops = FunctionPool.troopManager.getTroopInfoList() ?: emptyList()
                groupFlag.value = false
            }
        }
    }
    val showDialog = remember { mutableStateOf(false) }
    val taskEnv = remember { mutableStateOf<TaskEnv?>(null) }
    if (showDialog.value) {
        when (taskEnv.value?.type) {
            "friend" -> {
                FriendsCheckDialog(
                    friends = friends,
                    uinListStr = envMap[taskEnv.value?.name]!!,
                    onConfirm = {
                        showDialog.value = false
                    },
                    onDismiss = { showDialog.value = false }
                )
            }
            "group" -> {
                TroopsCheckDialog(
                    troops = troops,
                    uinListStr = envMap[taskEnv.value?.name]!!,
                    onConfirm = {
                        showDialog.value = false
                    },
                    onDismiss = { showDialog.value = false }
                )
            }
        }
    }
    ActivityView(
        title = "${taskId}-变量编辑",
        navController = navController
    ) {
        Box(Modifier.fillMaxSize()){

            LazyColumn(
                modifier = Modifier
                    .padding(top = 13.dp)
                    .padding(horizontal = 13.dp),
                contentPadding = rememberInsetsPaddingValues(LocalWindowInsets.current.navigationBars),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                envList.forEach { env ->
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(13.dp))
                                .background(Color.White)
                                .combinedClickable(
                                    onClick = {
                                        taskEnv.value = env
                                        when (env.type) {
                                            "friend" -> {
                                                if (!friendFlag.value && friends.isNotEmpty()) {
                                                    showDialog.value = true
                                                } else {
                                                    if (friendFlag.value) {
                                                        ToastUtil.send("好友列表正在获取中，请稍后重试")
                                                    } else {
                                                        ToastUtil.send("好友列表为空/获取好友列表失败")
                                                    }
                                                }
                                            }
                                            "group" -> {
                                                if (!groupFlag.value && troops.isNotEmpty()) {
                                                    showDialog.value = true
                                                } else {
                                                    if (groupFlag.value) {
                                                        ToastUtil.send("群组列表正在获取中，请稍后重试")
                                                    } else {
                                                        ToastUtil.send("群组列表为空/获取好友列表失败")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                )
                                .padding(horizontal = 13.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = buildString {
                                    append("变量：${env.name}")
                                    if (env.type == "friend") {
                                        append(" (点击打开好友列表)")
                                    }
                                    if (env.type == "group") {
                                        append(" (点击打开群组列表)")
                                    }
                                },
                                fontSize = 18.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                            Text(
                                text = env.desc,
                                fontSize = 14.sp,
                                color = Color(0xFFFF4A4A),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            var limitErr by remember {
                                mutableStateOf(false)
                            }
                            OutlinedTextField(
                                value = envMap[env.name]?.value ?: env.default,
                                onValueChange = {
                                    if (env.limit >0) {
                                        when (env.type) {
                                            "string" -> {
                                                limitErr = it.length > env.limit
                                            }
                                            "num" -> {
                                                limitErr = if (it.isNotEmpty()) {
                                                    if (it.isDigitsOnly()) {
                                                        it.toInt() > env.limit
                                                    } else true
                                                } else {
                                                    false
                                                }
                                            }
                                            else -> {
                                                limitErr = it.split(",").size > env.limit
                                            }
                                        }
                                    }
                                    if (!limitErr) {
                                        envMap[env.name]?.value = it
                                    }
                                },
                                label = { Text(text = env.name) },
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp),
                                isError = limitErr
                            )
                            if (limitErr && env.limit > 0) {
                                Text(
                                    text = "变量长度/大小限制为${env.limit}",
                                    color = Color(0xFFFF4A4A),
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }

            Box(modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(40.dp)
                .height(56.dp)
                .width(120.dp)
                .clip(RoundedCornerShape(80.dp))
                .background(Color.White)) {
                Text(text = "按钮", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Preview
@Composable
fun PreviewEditEnvLayout() {
    EditEnvLayout(
        navController = rememberNavController(),
        groupId = "会员相关",
        taskId = "会员排行榜"
    )
}