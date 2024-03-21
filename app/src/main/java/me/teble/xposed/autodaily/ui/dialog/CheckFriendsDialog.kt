package me.teble.xposed.autodaily.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.teble.xposed.autodaily.ui.composable.DialogButton
import me.teble.xposed.autodaily.ui.composable.DialogTopBar
import me.teble.xposed.autodaily.ui.composable.HintEditText
import me.teble.xposed.autodaily.ui.composable.SelectInfoItem
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.Search
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors


@Composable
fun CheckFriendsOverlayUI(
    uinListStr: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    viewmodel: FriendViewModel = viewModel(),
) {
    var searchText by remember { mutableStateOf("") }
    DialogTopBar(
        text = "好友列表（${viewmodel.friendsState.size}）",
        iconClick = onDismiss
    )

    HorizontalDivider(
        color = colors.colorDialogDivider,
        thickness = 1.dp,
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .padding(bottom = 16.dp)
    )

    Column(
        Modifier.padding(horizontal = 32.dp)
    ) {

        SearchBar(
            text = { searchText },
        ) {
            searchText = it
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {

            TextButton(
                text = "反选"
            ) {

            }

            TextButton(
                text = "全选"
            ) {

            }

        }

        LazyColumn(
            modifier = Modifier
                .weight(1f, false)
                .fillMaxWidth()

        ) {
            items(
                items = viewmodel.friendsState,
                key = { it.uin },
                contentType = { it.remark == null }) { friend ->
                var itemEnable by remember {
                    mutableStateOf(false)
                }
                SelectInfoItem(
                    text = friend.remark ?: friend.nike,
                    infoText = friend.uin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(SmootherShape(12.dp)),
                    clickEnabled = { true },
                    enable = { itemEnable },
                    onClick = {
                        itemEnable = !itemEnable
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
            modifier = Modifier.padding(start = 12.dp),
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
