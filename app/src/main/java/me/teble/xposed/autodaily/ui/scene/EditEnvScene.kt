package me.teble.xposed.autodaily.ui.scene

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.teble.xposed.autodaily.ui.composable.DatePicker
import me.teble.xposed.autodaily.ui.composable.TopBar
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.layout.verticalScrollPadding

@Composable
fun EditEnvScene(navController: NavController, groupId: String?, taskId: String) {


    Box {
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
                    .verticalScrollPadding()
            ) {

                DatePicker(Modifier.width(328.dp))
            }

        }
    }

//    Column(
//        Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF7F7F7))
//    ) {
//        val friendFlag = remember { mutableStateOf(true) }
//
//        val envMap = remember { mutableStateOf("") }
//        var friends by remember { mutableStateOf(emptyList<Friend>()) }
//        LaunchedEffect(friends) {
//
//            thread {
//                friends = FunctionPool.friendsManager.getFriends() ?: emptyList()
//
//            }
//
//        }
//
//        val state = rememberBottomSheetState()
//        val scope = rememberCoroutineScope()
//        Button(onClick = {
//            scope.launch {
//                state.expand()
//            }
//        }) {
//            Text(text = "打开弹窗")
//        }
//
//        CheckFriendsDialog(
//            state = state,
//            friends = friends,
//            uinListStr = envMap,
//
//            onConfirm = {
//
//            },
//            onDismiss = { }
//        )
//    }
}