package me.teble.xposed.autodaily.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dokar.sheets.BottomSheet
import com.dokar.sheets.BottomSheetLayout
import com.dokar.sheets.BottomSheetState
import me.teble.xposed.autodaily.task.model.Friend
import me.teble.xposed.autodaily.ui.LineSpacer
import me.teble.xposed.autodaily.ui.composable.SelectInfoItem
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.Close
import me.teble.xposed.autodaily.ui.icon.icons.Search
import me.teble.xposed.autodaily.ui.layout.verticalScrollPadding
import me.teble.xposed.autodaily.ui.theme.DefaultDialogSheetBehaviors
import me.teble.xposed.autodaily.ui.theme.DefaultSheetBehaviors

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CheckFriendsDialog(
    state: BottomSheetState,
    friends: List<Friend>,
    uinListStr: MutableState<String>,

    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    BottomSheet(
        state = state,
        skipPeeked = true,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        backgroundColor = Color(0xFFFFFFFF),
        behaviors = DefaultDialogSheetBehaviors,
        showAboveKeyboard = true,
        dragHandle = {}
    ) {
        Column(
            Modifier.verticalScrollPadding()
        ) {

            Row(
                modifier = Modifier
                    .padding(start = 32.dp, end = 26.dp)
                    .padding(vertical = 21.dp)
            ) {
                Text(
                    text = "群组列表（${friends.size}）",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF202124),
                        textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Close, contentDescription = "关闭",
                    Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable(role = Role.Button, onClick = onDismiss)
                        .padding(6.dp)
                )
            }
            Column(
                Modifier
                    .padding(horizontal = 32.dp)
            ) {
                // 其他的有渲染问题，有时间解决
                Text(
                    text = searchText,
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(Color(0xFFF7F7F7))
                )

                // Color(0xFFF2F2F2)

                Row(
                    Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .clip(SmootherShape(100.dp))
                        .background(Color(0xFFF2F2F2))
                        .padding(vertical = 13.dp, horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Search,
                        tint = Color(0xFF919191),
                        contentDescription = ""
                    )
                    BasicTextField(
                        modifier = Modifier.padding(start = 12.dp),
                        value = searchText,
                        onValueChange = {
                            searchText = it
                        },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF202124),
                        ),

                        )
                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {

                    Text(
                        text = "反选",
                        modifier = Modifier
                            .clip(SmootherShape(4.dp))
                            .clickable(
                                role = Role.Button,
                                onClick = onConfirm
                            )
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF0095FF),
                        )
                    )

                    Text(
                        text = "全选",
                        modifier = Modifier
                            .clip(SmootherShape(4.dp))
                            .clickable(
                                role = Role.Button,
                                onClick = onConfirm
                            )
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF0095FF),
                        )
                    )
                }


                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    items(
                        items = friends,
                        key = { it.uin },
                        contentType = { it.remark == null }) { friend ->
                        SelectInfoItem(
                            text = friend.remark ?: friend.nike,
                            infoText = friend.uin,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(SmootherShape(12.dp))
                                .background(Color(0xFFFFFFFF)),
                            clickEnabled = true,
                            enable = false,
                            onClick = {

                            })
                    }

                }
                Text(
                    text = "保存",
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .clip(SmootherShape(12.dp))
                        .background(Color(0x0F0095FF))
                        .clickable(
                            role = Role.Button,
                            onClick = onConfirm
                        )
                        .padding(vertical = 16.dp),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0095FF),
                        textAlign = TextAlign.Center,
                    )
                )
            }
        }


    }
}

