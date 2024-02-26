package me.teble.xposed.autodaily.ui

import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.teble.xposed.autodaily.ui.scene.AboutScene
import me.teble.xposed.autodaily.ui.scene.DeveloperScene
import me.teble.xposed.autodaily.ui.scene.EditEnvScene
import me.teble.xposed.autodaily.ui.scene.LicenseScene
import me.teble.xposed.autodaily.ui.scene.MainScreen
import me.teble.xposed.autodaily.ui.scene.SettingScene
import me.teble.xposed.autodaily.ui.scene.SignScene
import me.teble.xposed.autodaily.ui.scene.SignStateScene
import me.teble.xposed.autodaily.utils.openUrl

object XAutoDailyApp {
    const val Main = "MainLayout"
    const val Sign = "SignLayout"
    const val Other = "OtherLayout"
    const val EditEnv = "EditEnvLayout"
}

@Keep
enum class Screen {
    Main,
    Sign,
    About,
    Setting,
    Developer,
    License,
    EditEnv,
    SignState
}

sealed class NavigationItem(val route: String) {
    data object Main : NavigationItem(Screen.Main.name)
    data object Sign : NavigationItem(Screen.Sign.name)
    data object About : NavigationItem(Screen.About.name)
    data object Setting : NavigationItem(Screen.Setting.name)
    data object Developer : NavigationItem(Screen.Developer.name)
    data object License : NavigationItem(Screen.License.name)

    data object SignState : NavigationItem(Screen.SignState.name)
    data class EditEnv(val taskGroup: String, val taskId: String) :
        NavigationItem("${Screen.EditEnv.name}/$taskGroup/$taskId")
}

fun NavController.navigate(item: NavigationItem, popUpToItem: NavigationItem) {
    this.navigate(item.route) {
        popUpTo(popUpToItem.route)
        launchSingleTop = true
    }
}

fun NavController.navigateUrl(uri: String) {
    context.openUrl(uri)
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
        composable(NavigationItem.About.route) {
            AboutScene(navController)
        }

        composable(NavigationItem.Setting.route) {
            SettingScene(navController)
        }

        composable(NavigationItem.Developer.route) {
            DeveloperScene(navController)
        }

        composable(NavigationItem.License.route) {
            LicenseScene(navController)
        }

        composable(NavigationItem.SignState.route) {
            SignStateScene(navController)
        }

        composable("${Screen.EditEnv.name}/{taskGroup}/{taskId}") { backStackEntry ->
            EditEnvScene(
                navController,
                backStackEntry.arguments!!.getString("taskGroup", ""),
                backStackEntry.arguments!!.getString("taskId", "")
            )
        }
    }
}