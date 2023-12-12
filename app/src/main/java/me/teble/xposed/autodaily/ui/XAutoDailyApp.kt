package me.teble.xposed.autodaily.ui

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.teble.xposed.autodaily.ui.XAutoDailyApp.Main
import me.teble.xposed.autodaily.ui.scene.MainScreen

object XAutoDailyApp {
    const val Main = "MainLayout"
    const val Sign = "SignLayout"
    const val Other = "OtherLayout"
    const val EditEnv = "EditEnvLayout"
}

@Composable
fun XAutoDailyApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Main
    ) {
        composable(Main) {
            MainScreen(navController = navController)
        }

    }
}