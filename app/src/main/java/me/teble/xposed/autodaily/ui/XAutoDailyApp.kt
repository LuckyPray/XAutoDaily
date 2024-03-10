package me.teble.xposed.autodaily.ui

import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.teble.xposed.autodaily.activity.module.MainThemeViewModel
import me.teble.xposed.autodaily.ui.scene.AboutScene
import me.teble.xposed.autodaily.ui.scene.DeveloperScene
import me.teble.xposed.autodaily.ui.scene.EditEnvScene
import me.teble.xposed.autodaily.ui.scene.LicenseScene
import me.teble.xposed.autodaily.ui.scene.MainScreen
import me.teble.xposed.autodaily.ui.scene.SettingScene
import me.teble.xposed.autodaily.ui.scene.SignScene
import me.teble.xposed.autodaily.ui.scene.SignStateScene


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

fun NavController.navigate(item: NavigationItem) {
    this.navigate(item.route)
}


@Composable
fun XAutoDailyApp(themeViewModel: MainThemeViewModel) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = NavigationItem.Main.route
    ) {
        composable(route = NavigationItem.Main.route) {
            MainScreen(
                onNavigateToSign = {
                    navController.navigate(
                        NavigationItem.Sign
                    )
                },
                onNavigateToSetting = {
                    navController.navigate(
                        NavigationItem.Setting
                    )
                },
                onNavigateToAbout = {
                    navController.navigate(
                        NavigationItem.About
                    )
                }
            )
        }

        composable(route = NavigationItem.Sign.route) {
            SignScene(
                backClick = { navController.popBackStack() },
                onNavigateToEditEnvs = { groupId, taskId ->
                    navController.navigate(
                        NavigationItem.EditEnv(
                            groupId,
                            taskId
                        )
                    )
                })
        }
        composable(route = NavigationItem.About.route) {
            AboutScene(
                backClick = { navController.popBackStack() },
                onNavigateToLicense = {
                    navController.navigate(NavigationItem.License)
                },
                onNavigateToDeveloper = {
                    navController.navigate(NavigationItem.Developer)
                }
            )
        }

        composable(route = NavigationItem.Setting.route) {
            SettingScene(
                backClick = { navController.popBackStack() },
                onNavigateToSignState = {
                    navController.navigate(NavigationItem.SignState)
                }, themeViewModel
            )
        }

        composable(route = NavigationItem.Developer.route) {
            DeveloperScene(backClick = { navController.popBackStack() })
        }

        composable(route = NavigationItem.License.route) {
            LicenseScene(backClick = { navController.popBackStack() })
        }

        composable(route = NavigationItem.SignState.route) {
            SignStateScene(backClick = { navController.popBackStack() })
        }

        composable(route = "${Screen.EditEnv.name}/{taskGroup}/{taskId}") { backStackEntry ->
            EditEnvScene(
                backClick = { navController.popBackStack() },
                backStackEntry.arguments!!.getString("taskGroup", ""),
                backStackEntry.arguments!!.getString("taskId", "")
            )
        }
    }

}