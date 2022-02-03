package me.teble.xposed.autodaily.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import function.task.module.Task
import me.teble.xposed.autodaily.task.model.Friend
import me.teble.xposed.autodaily.task.model.TroopInfo

@Composable
fun FriendsCheckDialog(
    friends: MutableState<List<Friend>>,
    uinListStr: MutableState<String>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val keyword = remember { mutableStateOf<String?>(null) }
    val friendUinSet = friends.value.map { it.uin }.toSet()
    val uinSelectSet = remember {
        mutableStateOf(mutableSetOf<String>().apply {
            uinListStr.value.split(",").forEach {
                val uin = it.trim()
                if (uin.isNotEmpty() && friendUinSet.contains(uin)) {
                    add(it)
                }
            }
        })
    }
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colors.surface,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // TITLE
                Text(
                    text = "好友列表",
                    color = Color.Black,
                    fontSize = 23.sp,
                    modifier = Modifier.padding(8.dp)
                )
                OutlinedTextField(
                    value = keyword.value ?: "",
                    onValueChange = {
                        if (it.isEmpty()) {
                            keyword.value = null
                        } else {
                            keyword.value = it
                        }
                    },
                    label = { Text(text = "关键词") }
                )
                LineSpacer()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(weight = 1f, fill = false)
                        .padding(vertical = 16.dp)
                ) {
                    friends.value.forEach { friend ->
                        val show = mutableStateOf(true)
                        keyword.value?.let {
                            show.value = friend.uin.contains(it)
                                || friend.nike.lowercase().contains(it)
                                || friend.remark?.lowercase()?.contains(it) ?: false
                        }
                        if (keyword.value == null) {
                            show.value = true
                        }
                        if (show.value) {
                            item {
                                LineCheckBox(
                                    title = friend.remark ?: friend.nike,
                                    desc = friend.uin,
                                    checked = mutableStateOf(uinSelectSet.value.contains(friend.uin)),
                                    onChange = {
                                        if (it) {
                                            uinSelectSet.value.add(friend.uin)
                                        } else {
                                            uinSelectSet.value.remove(friend.uin)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text(text = "取消")
                    }
                    TextButton(onClick = {
                        uinListStr.value = uinSelectSet.value.joinToString(",")
                        onConfirm()
                    }) {
                        Text(text = "保存")
                    }
                }
            }
        }
    }
}

@Composable
fun TroopsCheckDialog(
    troops: MutableState<List<TroopInfo>>,
    uinListStr: MutableState<String>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val keyword = remember { mutableStateOf<String?>(null) }
    val groupUinSet = troops.value.map { it.uin }.toSet()
    val uinSelectSet = remember {
        mutableStateOf(mutableSetOf<String>().apply {
            uinListStr.value.split(",").forEach {
                val uin = it.trim()
                if (uin.isNotEmpty() && groupUinSet.contains(uin)) {
                    add(it)
                }
            }
        })
    }
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colors.surface,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // TITLE
                Text(
                    text = "群组列表",
                    color = Color.Black,
                    fontSize = 23.sp,
                    modifier = Modifier.padding(8.dp)
                )
                OutlinedTextField(
                    value = keyword.value ?: "",
                    onValueChange = {
                        if (it.isEmpty()) {
                            keyword.value = null
                        } else {
                            keyword.value = it
                        }
                    },
                    label = { Text(text = "关键词") }
                )
                LineSpacer()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(weight = 1f, fill = false)
                        .padding(vertical = 16.dp)
                ) {
                    troops.value.forEach { troop ->
                        val show = mutableStateOf(true)
                        keyword.value?.let {
                            show.value = troop.uin.contains(it)
                                || troop.name.lowercase().contains(it)
                        }
                        if (keyword.value == null) {
                            show.value = true
                        }
                        if (show.value) {
                            item {
                                LineCheckBox(
                                    title = troop.name,
                                    desc = troop.uin,
                                    checked = mutableStateOf(uinSelectSet.value.contains(troop.uin)),
                                    onChange = {
                                        if (it) {
                                            uinSelectSet.value.add(troop.uin)
                                        } else {
                                            uinSelectSet.value.remove(troop.uin)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text(text = "取消")
                    }
                    TextButton(onClick = {
                        uinListStr.value = uinSelectSet.value.joinToString(",")
                        onConfirm()
                    }) {
                        Text(text = "保存")
                    }
                }
            }
        }
    }
}

@Composable
fun UpdateDialog(
    title: String,
    text: String,
    onGithub: () -> Unit,
    onLanzou: () -> Unit,
    onDismiss: () -> Unit
) {

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colors.surface,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // TITLE
                Text(
                    text = title,
                    fontSize = 23.sp,
                    modifier = Modifier.padding(8.dp)
                )
                LineSpacer()
                Text(
                    text = text,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(8.dp)
                )
                LineSpacer()
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text(text = "返回")
                    }
                    TextButton(onClick = onLanzou) {
                        Text(text = "蓝奏云")
                    }
                    TextButton(onClick = onGithub) {
                        Text(text = "Github")
                    }
                }
            }
        }
    }
}

@Composable
fun EditVariableDialog(
    task: Task,
    onConfirmClicked: () -> Unit,
    onDismiss: () -> Unit
) {

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colors.surface,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // TITLE
                Text(
                    text = "自定义变量列表",
                    fontSize = 23.sp,
                    modifier = Modifier.padding(8.dp)
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = {

                    },
                    label = { Text(text = "关键词") }
                )
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
fun PreviewFriendsCheckDialog() {
    val showDialog = mutableStateOf(false)
    val friends = mutableListOf<Friend>().apply {
        for (i in 1..20) {
            add(
                Friend(
                    "$i",
                    "nick$i",
                    if (i % 2 == 0) "remarkremarkremarkremarkremarkremarkremark$i" else null
                )
            )
        }
    }
    val uinSelectSet = mutableStateOf(mutableSetOf("1", "3"))
    LazyColumn(
        modifier = Modifier
//                    .padding(13.dp)
            .height(600.dp)
    ) {
        friends.forEach { friend ->
            item {
                LineCheckBox(
                    title = friend.remark ?: friend.nike,
                    desc = friend.uin,
                    checked = mutableStateOf(uinSelectSet.value.contains(friend.uin)),
                    onChange = {
                        if (it) {
                            uinSelectSet.value.add(friend.uin)
                        } else {
                            uinSelectSet.value.remove(friend.uin)
                        }
                    }
                )
            }
        }
    }
}