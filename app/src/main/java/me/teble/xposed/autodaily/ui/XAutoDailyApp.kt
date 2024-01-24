package me.teble.xposed.autodaily.ui

import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.teble.xposed.autodaily.ui.XAutoDailyApp.Main
import me.teble.xposed.autodaily.ui.XAutoDailyApp.Sign
import me.teble.xposed.autodaily.ui.scene.MainScreen
import me.teble.xposed.autodaily.ui.scene.SignScene

object XAutoDailyApp {
    const val Main = "MainLayout"
    const val Sign = "SignLayout"
    const val Other = "OtherLayout"
    const val EditEnv = "EditEnvLayout"
}

enum class Screen {
    Main,
    Sign
}
sealed class NavigationItem(val route: String) {
    data object Main : NavigationItem(Screen.Main.name)
    data object Sign : NavigationItem(Screen.Sign.name)
}

fun NavController.navigate(item : NavigationItem){
    this.navigate(item.route){
        popUpTo(graph.startDestinationId)
        launchSingleTop = true
    }

}

@Composable
fun XAutoDailyApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = NavigationItem.Main.route
    ) {
        composable(NavigationItem.Main.route) {
            MainScreen(navController)
        }

        composable(NavigationItem.Sign.route) {
            SignScene(navController)
        }

    }
}
