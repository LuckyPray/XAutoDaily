package me.teble.xposed.autodaily.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dokar.sheets.BottomSheet
import com.dokar.sheets.BottomSheetState
import me.teble.xposed.autodaily.task.model.Friend
import me.teble.xposed.autodaily.ui.composable.DialogButton
import me.teble.xposed.autodaily.ui.composable.DialogTopBar
import me.teble.xposed.autodaily.ui.composable.HintEditText
import me.teble.xposed.autodaily.ui.composable.SelectInfoItem
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.Search
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.DefaultDialogSheetBehaviors
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme

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
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        backgroundColor = Color(0xFFFFFFFF),
        behaviors = DefaultDialogSheetBehaviors,
        showAboveKeyboard = true,
        dragHandle = {}
    ) {
        Column {

            DialogTopBar(
                text = "好友列表（${friends.size}）",
                iconClick = onDismiss
            )

            Divider(
                color = Color(0xFFF7F7F7),
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 16.dp)
                    .height(1.dp)
                    .fillMaxWidth()
            )

            Column(
                Modifier
                    .padding(horizontal = 32.dp)

            ) {

                SearchBar(
                    text = searchText,
                ) {
                    searchText = it
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
                            color = XAutodailyTheme.colors.themeColor,
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
                            color = XAutodailyTheme.colors.themeColor,
                        )
                    )
                }




                LazyColumn(
                    modifier = Modifier
                        .weight(1f, false)
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

                DialogButton(
                    text = "保存",
                    Modifier
                        .defaultNavigationBarPadding()
                        .padding(top = 24.dp)
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(),
                    onClick = onConfirm
                )

            }
        }


    }
}

@Composable
private fun SearchBar(text: String, onValueChange: (String) -> Unit) {
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
        HintEditText(
            modifier = Modifier.padding(start = 12.dp),
            value = text,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF202124),
            ),
            hintText = "你好",
            hintTextStyle = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF919191),
            )
        )
    }
}
