package me.teble.xposed.autodaily.ui.scene

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.dokar.sheets.rememberBottomSheetState
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.hook.function.proxy.FunctionPool
import me.teble.xposed.autodaily.task.model.Friend
import me.teble.xposed.autodaily.ui.FriendsCheckDialog
import me.teble.xposed.autodaily.ui.composable.TopBar
import me.teble.xposed.autodaily.ui.dialog.CheckFriendsDialog
import me.teble.xposed.autodaily.ui.getVariable
import kotlin.concurrent.thread

@Composable
fun EditEnvScene(navController: NavController, groupId: String?, taskId: String) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        val friendFlag = remember { mutableStateOf(true) }
        TopBar(text = taskId, backClick = {
            navController.popBackStack()
        })
        val envMap = remember { mutableStateOf("") }
        var friends by remember { mutableStateOf(emptyList<Friend>()) }
        LaunchedEffect(friends) {

            thread {
                friends = FunctionPool.friendsManager.getFriends() ?: emptyList()

            }

        }

        val state = rememberBottomSheetState()
        val scope = rememberCoroutineScope()
        Button(onClick = {
            scope.launch {
                state.expand()
            }
        }) {
            Text(text = "打开弹窗")
        }

        CheckFriendsDialog(
            state = state,
            friends = friends,
            uinListStr = envMap,

            onConfirm = {

            },
            onDismiss = { }
        )
    }
}