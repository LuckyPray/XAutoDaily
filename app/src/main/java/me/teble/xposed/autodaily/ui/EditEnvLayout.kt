package me.teble.xposed.autodaily.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import me.teble.xposed.autodaily.task.model.TaskEnv
import me.teble.xposed.autodaily.hook.config.Config.accountConfig
import me.teble.xposed.autodaily.hook.function.proxy.FunctionPool
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.task.model.Friend
import me.teble.xposed.autodaily.task.model.TroopInfo
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.task.util.Const.ENV_VARIABLE
import me.teble.xposed.autodaily.utils.LogUtil
import kotlin.concurrent.thread

@Composable
fun EditEnvLayout(
    navController: NavHostController,
    groupId: String?,
    taskId: String?
) {
    val envList = remember { mutableStateOf(emptyList<TaskEnv>()) }
    LaunchedEffect(envList) {
        LogUtil.log("--------------- init envList ---------------")
        val conf = ConfigUtil.loadSaveConf()
        conf.taskGroups.forEach {
            if (it.id == groupId) {
                it.tasks.forEach { task ->
                    if (task.id == taskId) {
                        envList.value = task.envs ?: emptyList()
                        return@LaunchedEffect
                    }
                }
            }
        }
        if (envList.value.isEmpty()) {
            ToastUtil.send("没有需要编辑的环境变量")
        }
    }
    val envMap = remember { HashMap<String, MutableState<String>>() }
    val friends = remember { mutableStateOf(emptyList<Friend>()) }
    val troops = remember { mutableStateOf(emptyList<TroopInfo>()) }
    BackHandler(onBack = {
        envMap.entries.forEach {
            if (it.value.value.isNotEmpty()) {
                accountConfig.putString("$taskId#$ENV_VARIABLE#${it.key}", it.value.value)
            } else {
                accountConfig.remove("$taskId#$ENV_VARIABLE#${it.key}")
            }
        }
        ToastUtil.send("保存成功")
        navController.popBackStack()
    })
    val friendFlag = remember { mutableStateOf(false) }
    val groupFlag = remember { mutableStateOf(false) }
    LaunchedEffect(envMap) {
        envList.value.forEach { env ->
            envMap[env.name] = mutableStateOf(
                accountConfig.getString("$taskId#$ENV_VARIABLE#${env.name}", env.default)
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
                friends.value = FunctionPool.friendsManager.getFriends() ?: emptyList()
                friendFlag.value = false
            }
        }
    }
    LaunchedEffect(troops) {
        if (groupFlag.value) {
            thread {
                troops.value = FunctionPool.troopManager.getTroopInfoList() ?: emptyList()
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
                    friends = friends.value,
                    uinListStr = envMap[taskEnv.value?.name]!!,
                    onConfirm = {
                        showDialog.value = false
                    },
                    onDismiss = { showDialog.value = false }
                )
            }
            "group" -> {
                TroopsCheckDialog(
                    troops = troops.value,
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
        LazyColumn(modifier = Modifier.padding(13.dp)) {
            envList.value.forEach { env ->
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(13.dp))
                            .padding(horizontal = 13.dp, vertical = 5.dp)
                            .click(
                                onClick = {
                                    taskEnv.value = env
                                    when (env.type) {
                                        "friend" -> {
                                            if (!friendFlag.value && friends.value.isNotEmpty()) {
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
                                            if (!groupFlag.value && troops.value.isNotEmpty()) {
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
                        val limitErr = remember {
                            mutableStateOf(false)
                        }
                        OutlinedTextField(
                            value = envMap[env.name]?.value ?: env.default,
                            onValueChange = {
                                if (env.limit >0) {
                                    when (env.type) {
                                        "string" -> {
                                            limitErr.value = it.length > env.limit
                                        }
                                        "num" -> {
                                            if (it.isNotEmpty()) {
                                                if (it.isDigitsOnly()) {
                                                    limitErr.value = it.toInt() > env.limit
                                                } else limitErr.value = true
                                            } else {
                                                limitErr.value = false
                                            }
                                        }
                                        else -> {
                                            limitErr.value = it.split(",").size > env.limit
                                        }
                                    }
                                }
                                if (!limitErr.value) {
                                    envMap[env.name]?.value = it
                                }
                            },
                            label = { Text(text = env.name) },
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            isError = limitErr.value
                        )
                        if (limitErr.value && env.limit > 0) {
                            Text(
                                text = "变量长度/大小限制为${env.limit}",
                                color = Color(0xFFFF4A4A),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                    LineSpacer(height = 15.dp)
                }
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