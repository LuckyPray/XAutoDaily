package me.teble.xposed.autodaily.ui.scene

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import me.teble.xposed.autodaily.ui.composable.TopBar

@Composable
fun EditEnvScene(navController: NavController,groupId: String, taskId: String) {
    Column(

    ) {
        TopBar(text = taskId, hasBack = true, backClick = {
            navController.popBackStack()
        })
    }
}