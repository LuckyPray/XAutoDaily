package me.teble.xposed.autodaily.ui

import androidx.annotation.Keep
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import me.teble.xposed.autodaily.activity.module.MainThemeViewModel
import me.teble.xposed.autodaily.ui.dialog.NoticeOverlayUI
import me.teble.xposed.autodaily.ui.scene.AboutScene
import me.teble.xposed.autodaily.ui.scene.DeveloperScene
import me.teble.xposed.autodaily.ui.scene.EditEnvScene
import me.teble.xposed.autodaily.ui.scene.LicenseScene
import me.teble.xposed.autodaily.ui.scene.MainScene
import me.teble.xposed.autodaily.ui.scene.SettingScene
import me.teble.xposed.autodaily.ui.scene.SignScene
import me.teble.xposed.autodaily.ui.scene.SignStateScene
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors

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

@Keep
enum class Dialog {
    Notice
}

sealed class NavigationItem(val route: String)
sealed class SceneItem(route: String) : NavigationItem(route) {
    data object Main : SceneItem(Screen.Main.name)
    data object Sign : SceneItem(Screen.Sign.name)
    data object About : SceneItem(Screen.About.name)
    data object Setting : SceneItem(Screen.Setting.name)
    data object Developer : SceneItem(Screen.Developer.name)
    data object License : SceneItem(Screen.License.name)

    data object SignState : SceneItem(Screen.SignState.name)
    data class EditEnv(val taskGroup: String, val taskId: String) :
        SceneItem("${Screen.EditEnv.name}/$taskGroup/$taskId")
}

sealed class DialogItem(route: String) : NavigationItem(route) {
    data class Notice(val infoText: String) : SceneItem("${Dialog.Notice.name}/$infoText")

}

fun NavController.navigate(item: NavigationItem) {
    this.navigate(item.route)
}


@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun XAutoDailyApp(themeViewModel: MainThemeViewModel) {

    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)
    CompositionLocalProvider(
        LocalFriendList provides XAutodailyConstants.FriendList,
        LocalDependencies provides XAutodailyConstants.DependencyList,
        LocalTaskGroupsState provides XAutodailyConstants.TaskGroupsList,
        LocalTaskState provides XAutodailyConstants.TaskState,
    ) {
        ModalBottomSheetLayout(
            sheetBackgroundColor = colors.colorBgDialog,
            bottomSheetNavigator = bottomSheetNavigator,
            scrimColor = colors.colorBgMask,
            sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        ) {
            NavHost(
                navController = navController,
                startDestination = SceneItem.Main.route
            ) {
                composable(route = SceneItem.Main.route) {
                    MainScene(
                        onNavigateToNotice = {
                            navController.navigate(DialogItem.Notice(it))
                        },
                        onNavigateToSign = {
                            navController.navigate(SceneItem.Sign)
                        },
                        onNavigateToSetting = {
                            navController.navigate(SceneItem.Setting)
                        },
                        onNavigateToAbout = {
                            navController.navigate(SceneItem.About)
                        }
                    )
                }

                bottomSheet(route = "${Dialog.Notice.name}/{infoText}") { backStackEntry ->
                    NoticeOverlayUI(
                        infoText = backStackEntry.arguments!!.getString("infoText", ""),
                        onDismiss = navController::popBackStack
                    )
                }

                composable(route = SceneItem.Sign.route) {
                    SignScene(
                        backClick = navController::popBackStack,
                        hasBackProvider = { true },
                        onNavigateToEditEnvs = { groupId, taskId ->
                            navController.navigate(
                                SceneItem.EditEnv(
                                    groupId,
                                    taskId
                                )
                            )
                        })
                }
                composable(route = SceneItem.About.route) {
                    AboutScene(
                        hasBackProvider = { true },
                        backClick = navController::popBackStack,
                        onNavigateToLicense = {
                            navController.navigate(SceneItem.License)
                        },
                        onNavigateToDeveloper = {
                            navController.navigate(SceneItem.Developer)
                        },
                    )
                }

                composable(route = SceneItem.Setting.route) {
                    SettingScene(
                        backClick = navController::popBackStack,
                        onNavigateToSignState = {
                            navController.navigate(SceneItem.SignState)
                        },
                        hasBackProvider = { true },
                        themeViewModel = themeViewModel
                    )
                }

                composable(route = SceneItem.Developer.route) {
                    DeveloperScene(backClick = navController::popBackStack)
                }

                composable(route = SceneItem.License.route) {
                    LicenseScene(backClick = navController::popBackStack)
                }

                composable(route = SceneItem.SignState.route) {
                    SignStateScene(backClick = navController::popBackStack)
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
    }

}