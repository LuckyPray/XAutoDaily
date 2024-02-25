package me.teble.xposed.autodaily.ui.scene

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dokar.sheets.rememberBottomSheetState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.hook.function.proxy.FunctionPool
import me.teble.xposed.autodaily.task.model.Friend
import me.teble.xposed.autodaily.ui.composable.DatePicker
import me.teble.xposed.autodaily.ui.composable.TopBar
import me.teble.xposed.autodaily.ui.dialog.CheckFriendsDialog
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.layout.verticalScrollPadding

@Composable
fun EditEnvScene(navController: NavController, groupId: String?, taskId: String) {


    Box {

        val state = rememberBottomSheetState()
        val scope = rememberCoroutineScope()

        val friendFlag = remember { mutableStateOf(true) }

        val envMap = remember { mutableStateOf("") }
        var friends by remember { mutableStateOf(emptyList<Friend>()) }
        LaunchedEffect(friends) {
            scope.launch(IO) {
                friends = FunctionPool.friendsManager.getFriends() ?: emptyList()

            }
        }
        Scaffold(
            snackbarHost = {
//                SnackbarHost(hostState = snackbarHostState) {
//                    RoundedSnackbar(it)
//                }
            },
            topBar = {
                TopBar(text = taskId, backClick = {
                    navController.popBackStack()
                })
            },
            backgroundColor = Color(0xFFFFFFFF)
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = 16.dp)
                    .clip(SmootherShape(12.dp))
                    .verticalScroll(rememberScrollState())
                    .verticalScrollPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Button(onClick = {
                    scope.launch {
                        state.expand()
                    }
                }) {
                    Text(text = "打开弹窗")
                }

                DatePicker(Modifier.width(328.dp)) {
                    Log.d("time", "EditEnvScene: ${it}")
                }
            }

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