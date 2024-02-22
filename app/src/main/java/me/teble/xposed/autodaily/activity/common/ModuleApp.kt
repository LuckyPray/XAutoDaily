package me.teble.xposed.autodaily.activity.common

import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Keep
enum class Screen {
    Module,
    Setting
}

sealed class NavigationItem(val route: String) {
    data object Module : NavigationItem(Screen.Module.name)
    data object Setting : NavigationItem(Screen.Setting.name)
}

fun NavController.navigate(item: NavigationItem, popUpToItem: NavigationItem) {
    this.navigate(item.route) {
        popUpTo(popUpToItem.route)
        launchSingleTop = true
    }

}

@Composable
fun ModuleApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = NavigationItem.Module.route
    ) {
        composable(NavigationItem.Module.route) {
            ModuleScene(navController)
        }

        composable(NavigationItem.Setting.route) {
            SettingScene(navController)
        }

    }
}