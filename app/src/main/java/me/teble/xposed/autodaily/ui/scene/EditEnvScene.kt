package me.teble.xposed.autodaily.ui.scene

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import me.teble.xposed.autodaily.ui.composable.TopBar

@Composable
fun EditEnvScene(navController: NavController, groupId: String, taskId: String) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        TopBar(text = taskId, hasBack = true, backClick = {
            navController.popBackStack()
        })
    }
}