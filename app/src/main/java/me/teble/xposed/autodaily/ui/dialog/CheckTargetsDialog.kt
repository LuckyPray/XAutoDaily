package me.teble.xposed.autodaily.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.teble.xposed.autodaily.hook.function.proxy.FunctionPool
import me.teble.xposed.autodaily.ui.composable.DialogButton
import me.teble.xposed.autodaily.ui.composable.DialogTopBar
import me.teble.xposed.autodaily.ui.composable.HintEditText
import me.teble.xposed.autodaily.ui.composable.SelectInfoItem
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.Search
import me.teble.xposed.autodaily.ui.layout.DialogHorizontalPadding
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors
import me.teble.xposed.autodaily.utils.LogUtil

@Composable
fun CheckTargetsOverlayUI(
    type: String,
    initialValue: String,
    limit: Int,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val label = when (type) {
        "friend" -> "好友"
        "group" -> "群组"
        "guild" -> "频道"
        else -> "对象"
    }
    var targets by remember(type) { mutableStateOf<List<SelectionTarget>>(emptyList()) }
    var loading by remember(type) { mutableStateOf(true) }
    var loadError by remember(type) { mutableStateOf<String?>(null) }
    var loadAttempt by remember(type) { mutableIntStateOf(0) }
    var selected by rememberSaveable(type, initialValue) {
        mutableStateOf(ArrayList(parseSelectedIds(initialValue)))
    }
    var searchText by rememberSaveable(type) { mutableStateOf("") }
    var selectionError by remember(type) { mutableStateOf<String?>(null) }

    LaunchedEffect(type, loadAttempt) {
        loading = true
        loadError = null
        try {
            targets = withContext(Dispatchers.IO) { loadSelectionTargets(type).distinctBy { it.id } }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LogUtil.e(e, "获取${label}列表失败")
            loadError = "获取${label}列表失败，请重试"
        } finally {
            loading = false
        }
    }

    val availableTargets = remember(targets, initialValue) {
        includeSavedSelectionTargets(targets, parseSelectedIds(initialValue))
    }
    val visible = remember(availableTargets, searchText) {
        filterSelectionTargets(availableTargets, searchText)
    }
    val updateSelection: (List<String>) -> Unit = { updated ->
        if (limit > 0 && updated.size > limit && updated.size >= selected.size) {
            selectionError = "最多选择 $limit 项"
        } else {
            selected = ArrayList(updated)
            selectionError = null
        }
    }
    val configuration = LocalConfiguration.current
    Column(Modifier.heightIn(max = (configuration.screenHeightDp * 0.9f).dp)) {
        DialogTopBar(text = "${label}列表（${availableTargets.size}）", iconClick = onDismiss)
        HorizontalDivider(
            color = colors.colorDialogDivider,
            modifier = Modifier.padding(DialogHorizontalPadding).padding(bottom = 16.dp)
        )
        Column(Modifier.padding(DialogHorizontalPadding)) {
            SearchBar(text = { searchText }, onValueChange = { searchText = it })
            Row(
                Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton("清空") { updateSelection(emptyList()) }
                TextButton(if (searchText.isBlank()) "反选" else "反选结果") {
                    updateSelection(updateTargetSelection(selected, visible.map { it.id }, invert = true))
                }
                TextButton(if (searchText.isBlank()) "全选" else "全选结果") {
                    updateSelection(updateTargetSelection(selected, visible.map { it.id }, invert = false))
                }
            }
            Text(
                text = "已选 ${selected.size} 项" + if (limit > 0) "，最多 $limit 项" else "",
                color = colors.colorTextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            selectionError?.let {
                Text(it, color = Color(0xFFFF4A4A), fontSize = 12.sp)
            }
            when {
                loading -> Text("正在获取${label}列表…", color = colors.colorText)
                loadError != null -> {
                    Text(loadError!!, color = colors.colorText)
                    TextButton("重试") { loadAttempt++ }
                }
                visible.isEmpty() -> Text(
                    if (searchText.isBlank()) "${label}列表为空" else "没有匹配的${label}",
                    color = colors.colorTextSecondary
                )
            }
            LazyColumn(
                modifier = Modifier.weight(1f, false).fillMaxWidth().clip(SmootherShape(12.dp))
            ) {
                items(visible, key = { it.id }) { target ->
                    SelectInfoItem(
                        text = target.title,
                        infoText = target.description,
                        modifier = Modifier.fillMaxWidth().clip(SmootherShape(12.dp)),
                        clickEnabled = { !loading && loadError == null },
                        enable = { target.id in selected },
                        onClick = {
                            updateSelection(updateTargetSelection(selected, listOf(target.id), invert = true))
                        }
                    )
                }
            }
            DialogButton(
                text = "确定",
                modifier = Modifier.defaultNavigationBarPadding().padding(top = 24.dp)
                    .align(Alignment.CenterHorizontally).fillMaxWidth(),
                clickEnabled = { !loading && loadError == null && (limit <= 0 || selected.size <= limit) },
                onClick = { onConfirm(selected.joinToString(",")) }
            )
        }
    }
}

private fun loadSelectionTargets(type: String): List<SelectionTarget> = when (type) {
    "friend" -> FunctionPool.friendsManager.getFriends().orEmpty().map {
        SelectionTarget(
            id = it.uin,
            title = it.remark?.takeIf(String::isNotBlank) ?: it.nike.ifBlank { it.uin },
            keywords = "${it.nike} ${it.remark.orEmpty()}"
        )
    }
    "group" -> FunctionPool.troopManager.getTroopInfoList().orEmpty().map {
        SelectionTarget(it.uin, it.name.ifBlank { it.uin })
    }
    "guild" -> FunctionPool.guildManager.getGuildInfoList().orEmpty().map {
        SelectionTarget(it.uin, it.name.ifBlank { it.uin })
    }
    else -> error("不支持的选择类型：$type")
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TextButton(text: String, onClick: () -> Unit) {
    val fabColor = RippleConfiguration(
        color = colors.themeColor, rippleAlpha = RippleAlpha(
            pressedAlpha = 0.36f,
            focusedAlpha = 0.36f,
            draggedAlpha = 0.24f,
            hoveredAlpha = 0.16f
        )
    )

    CompositionLocalProvider(LocalRippleConfiguration provides fabColor) {
        Text(
            text = text,
            modifier = Modifier
                .clip(SmootherShape(4.dp))
                .clickable(
                    role = Role.Button,
                    onClick = onClick
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = colors.themeColor,
            )
        )
    }
}

@Composable
private fun SearchBar(text: () -> String, onValueChange: (String) -> Unit) {
    Row(
        Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .clip(SmootherShape(100.dp))
            .background(colors.colorBgSearch)
            .padding(vertical = 13.dp, horizontal = 12.dp)
    ) {
        Icon(
            imageVector = Icons.Search,
            tint = colors.colorTextSearch,
            contentDescription = ""
        )
        HintEditText(
            modifier = Modifier.padding(start = 12.dp).weight(1f),
            value = text,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = colors.colorText,
            ),
            hintText = "关键词",
            hintTextStyle = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = colors.colorTextSearch,
            )
        )
    }
}
