package me.teble.xposed.autodaily.ui.scene

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.task.model.Task
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.task.util.EnvFormatUtil
import me.teble.xposed.autodaily.ui.composable.DatePicker
import me.teble.xposed.autodaily.ui.composable.FloatingButton
import me.teble.xposed.autodaily.ui.composable.HintEditText
import me.teble.xposed.autodaily.ui.composable.IconEditText
import me.teble.xposed.autodaily.ui.composable.SmallTitle
import me.teble.xposed.autodaily.ui.composable.Text
import me.teble.xposed.autodaily.ui.composable.XaScaffold
import me.teble.xposed.autodaily.ui.dialog.TASK_ENV_SELECTION_RESULT
import me.teble.xposed.autodaily.ui.getVariable
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.nextShouldExecTime
import me.teble.xposed.autodaily.ui.setVariable
import me.teble.xposed.autodaily.ui.theme.DisabledAlpha
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors
import me.teble.xposed.autodaily.utils.LogUtil

@Composable
fun EditEnvScene(
    onNavigateToSelection: (type: String, name: String, value: String, limit: Int) -> Unit,
    backClick: () -> Unit,
    savedStateHandle: SavedStateHandle,
    groupId: String,
    taskId: String
) {
    var task by remember(groupId, taskId) { mutableStateOf<Task?>(null) }
    var defaults by remember(groupId, taskId) { mutableStateOf<Map<String, String>>(emptyMap()) }
    var values by rememberSaveable(groupId, taskId) {
        mutableStateOf<HashMap<String, String>?>(null)
    }
    var loading by remember(groupId, taskId) { mutableStateOf(true) }
    var loadError by remember(groupId, taskId) { mutableStateOf<String?>(null) }
    var saveAttempted by rememberSaveable(groupId, taskId) { mutableStateOf(false) }

    LaunchedEffect(groupId, taskId) {
        try {
            val loadedTask = withContext(Dispatchers.IO) {
                ConfigUtil.loadSaveConf().taskGroups.firstOrNull { it.id == groupId }
                    ?.tasks?.firstOrNull { it.id == taskId }
            }
            if (loadedTask == null) {
                loadError = "未找到该任务，请返回并更新配置"
            } else {
                val envs = loadedTask.envs.orEmpty().filter { it.isEditable }
                val (storedValues, defaultValues) = withContext(Dispatchers.IO) {
                    val stored = envs.associate { it.name to loadedTask.getVariable(it.name, "") }
                    val fallback = envs.associate { env ->
                        env.name to runCatching {
                            EnvFormatUtil.format(env.default, mutableMapOf())
                        }.getOrElse {
                            LogUtil.e(it, "解析变量默认值失败：${env.name}")
                            env.default
                        }
                    }
                    stored to fallback
                }
                defaults = defaultValues
                if (values == null) values = HashMap(storedValues)
                task = loadedTask
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LogUtil.e(e, "读取任务参数失败")
            loadError = "读取任务参数失败，请返回后重试"
        } finally {
            loading = false
        }
    }

    val selectionResult by savedStateHandle
        .getStateFlow<ArrayList<String>?>(TASK_ENV_SELECTION_RESULT, null).collectAsState()
    LaunchedEffect(selectionResult, task) {
        val result = selectionResult
        val currentTask = task
        if (result != null && currentTask != null && values != null) {
            values = HashMap(applyTaskEnvSelection(currentTask.envs.orEmpty(), values.orEmpty(), result))
            // Keep the result flow attached so a second selection can be delivered as well.
            savedStateHandle[TASK_ENV_SELECTION_RESULT] = null
        }
    }

    val envs = task?.envs.orEmpty().filter { it.isEditable }
    val time = if (task?.isCronTask == true) taskTime(envs, values.orEmpty(), defaults) else null
    val inputEnvs = if (time != null) {
        envs.filterNot { it.name == "hour" || it.name == "minute" }
    } else envs
    val palette = colors
    XaScaffold(
        text = taskId,
        backClick = backClick,
        floatingActionButton = {
            if (!loading && task != null && envs.isNotEmpty()) {
                FloatingButton(
                    modifier = Modifier.padding(end = 16.dp, bottom = 16.dp),
                    onClick = {
                        saveAttempted = true
                        val currentTask = task!!
                        val draft = values.orEmpty()
                        try {
                            val timeChanged = envs.filter { it.name == "hour" || it.name == "minute" }
                                .any { env ->
                                    val fallback = defaults[env.name].orEmpty()
                                    currentTask.getVariable(env.name, fallback) !=
                                        draft[env.name].orEmpty().ifEmpty { fallback }
                                }
                            val errors = saveTaskEnvValues(envs, draft) { name, value ->
                                currentTask.setVariable(name, value)
                            }
                            if (errors.isNotEmpty()) {
                                ToastUtil.send("请检查填写的参数")
                            } else {
                                values = HashMap(envs.associate {
                                    it.name to normalizeTaskEnvValue(it, draft[it.name].orEmpty())
                                })
                                if (currentTask.isCronTask && timeChanged) {
                                    currentTask.nextShouldExecTime = null
                                }
                                ToastUtil.send("保存成功")
                            }
                        } catch (e: Exception) {
                            LogUtil.e(e, "保存任务参数失败")
                            ToastUtil.send("保存失败，详情请查看日志")
                        }
                    }
                )
            }
        },
        containerColor = palette.colorBgContainer
    ) {
        when {
            loading -> Text("正在读取参数…", Modifier.padding(16.dp), color = palette.colorText)
            loadError != null -> Text(loadError!!, Modifier.padding(16.dp), color = palette.colorText)
            envs.isEmpty() -> Text("没有需要编辑的参数", Modifier.padding(16.dp), color = palette.colorText)
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().defaultNavigationBarPadding(),
                contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
            ) {
                if (inputEnvs.isNotEmpty()) {
                    item {
                        Text(
                            "留空使用默认值；没有默认值的项目为必填。",
                            Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = TextStyle(fontSize = 12.sp),
                            color = palette.colorTextSecondary
                        )
                    }
                }
                items(inputEnvs, key = { it.name }) { env ->
                    val value = values?.get(env.name).orEmpty()
                    val hint = defaults[env.name].orEmpty().ifEmpty { "请填写${env.name}" }
                    val error = taskEnvValueError(env, value)
                        .takeIf { saveAttempted || value.isNotEmpty() }
                    Column(Modifier.padding(bottom = 20.dp)) {
                        SmallTitle(
                            title = env.desc.ifBlank { env.name },
                            modifier = Modifier.padding(bottom = 8.dp, start = 16.dp, top = 8.dp)
                        )
                        val inputModifier = Modifier.fillMaxWidth()
                            .clip(SmootherShape(12.dp))
                            .drawBehind { drawRect(palette.colorBgEdit) }
                        val updateValue: (String) -> Unit = { updated ->
                            values = HashMap(values.orEmpty()).apply { put(env.name, updated) }
                        }
                        if (env.type in listOf("friend", "group", "guild")) {
                            IconEditText(
                                value = { value },
                                modifier = inputModifier,
                                hintText = hint,
                                iconClick = {
                                    onNavigateToSelection(
                                        env.type, env.name,
                                        value.ifEmpty { defaults[env.name].orEmpty() }, env.limit
                                    )
                                },
                                onValueChange = updateValue
                            )
                        } else {
                            HintEditText(
                                value = { value },
                                modifier = inputModifier.padding(vertical = 18.dp, horizontal = 16.dp),
                                textStyle = TextStyle(fontSize = 18.sp, color = palette.colorText),
                                singleLine = env.type == "num",
                                hintText = hint,
                                hintTextStyle = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = palette.colorText.copy(alpha = DisabledAlpha)
                                ),
                                onValueChange = updateValue
                            )
                        }
                        if (error != null) {
                            Text(
                                error, Modifier.padding(start = 16.dp, top = 6.dp),
                                style = TextStyle(fontSize = 12.sp), color = Color(0xFFFF4A4A)
                            )
                        }
                    }
                }
                if (time != null) {
                    item(key = "task-execution-time") {
                        SmallTitle("执行时间", Modifier.padding(start = 16.dp, top = 8.dp))
                        DatePicker(hour = time.first, minute = time.second) { duration ->
                            values = HashMap(values.orEmpty()).apply {
                                put("hour", duration.inWholeHours.toString())
                                put("minute", (duration.inWholeMinutes % 60).toString())
                            }
                        }
                        envs.filter { it.name == "hour" || it.name == "minute" }.forEach { env ->
                            val value = values?.get(env.name).orEmpty()
                            taskEnvValueError(env, value)
                                ?.takeIf { saveAttempted || value.isNotEmpty() }
                                ?.let { error ->
                                    Text(
                                        "${if (env.name == "hour") "小时" else "分钟"}：$error",
                                        Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                        style = TextStyle(fontSize = 12.sp),
                                        color = Color(0xFFFF4A4A)
                                    )
                                }
                        }
                    }
                }
            }
        }
    }
}
