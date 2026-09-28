package me.teble.xposed.autodaily.ui

import android.net.Uri
import androidx.annotation.Keep
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.navigation.BottomSheetNavigator
import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.material.navigation.bottomSheet
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import me.teble.xposed.autodaily.activity.common.ThemeViewModel
import me.teble.xposed.autodaily.ui.dialog.CheckTargetsOverlayUI
import me.teble.xposed.autodaily.ui.dialog.TASK_ENV_SELECTION_RESULT
import me.teble.xposed.autodaily.ui.dialog.NoticeOverlayUI
import me.teble.xposed.autodaily.ui.dialog.RestoreOverlayUI
import me.teble.xposed.autodaily.ui.dialog.ThemeOverlayUI
import me.teble.xposed.autodaily.ui.dialog.UpdateOverlayUI
import me.teble.xposed.autodaily.ui.scene.AboutScene
import me.teble.xposed.autodaily.ui.scene.DeveloperScene
import me.teble.xposed.autodaily.ui.scene.EditEnvScene
import me.teble.xposed.autodaily.ui.scene.LicenseScene
import me.teble.xposed.autodaily.ui.scene.MainScene
import me.teble.xposed.autodaily.ui.scene.SettingScene
import me.teble.xposed.autodaily.ui.scene.SignScene
import me.teble.xposed.autodaily.ui.scene.SignStateScene
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors
import me.teble.xposed.autodaily.utils.LogUtil

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
    Notice,
    Theme,
    CheckTargets,
    Update,
    Restore
}

@Stable
sealed class NavigationItem(val route: String)

@Stable
sealed class SceneItem(route: String) : NavigationItem(route) {
    data object Main : SceneItem(Screen.Main.name)
    data object Sign : SceneItem(Screen.Sign.name)
    data object About : SceneItem(Screen.About.name)
    data object Setting : SceneItem(Screen.Setting.name)
    data object Developer : SceneItem(Screen.Developer.name)
    data object License : SceneItem(Screen.License.name)

    data object SignState : SceneItem(Screen.SignState.name)
    data class EditEnv(val taskGroup: String, val taskId: String) :
        SceneItem("${Screen.EditEnv.name}/${Uri.encode(taskGroup)}/${Uri.encode(taskId)}")
}

@Stable
sealed class DialogItem(route: String) : NavigationItem(route) {
    data class Notice(
        val notice: String
    ) : DialogItem("${Dialog.Notice.name}/?notice=${Uri.encode(notice)}")

    data object Theme : DialogItem(Dialog.Theme.name)

    data class CheckTargets(
        val type: String,
        val name: String,
        val value: String,
        val limit: Int
    ) : DialogItem(
        "${Dialog.CheckTargets.name}/?type=${Uri.encode(type)}&name=${Uri.encode(name)}" +
            "&value=${Uri.encode(value)}&limit=$limit"
    )

    data class Update(
        val info: String
    ) : DialogItem("${Dialog.Update.name}/?info=${Uri.encode(info)}")

    data object Restore : DialogItem(Dialog.Restore.name)

}

fun NavController.navigateItem(item: NavigationItem) {
    runCatching {
        this.navigate(item.route)
    }.onFailure {
        LogUtil.e(it)
    }
}

/**
 * Create and remember a [BottomSheetNavigator]
 */
@Composable
private fun rememberBottomSheetNavigator(
    animationSpec: AnimationSpec<Float> = SpringSpec(),
    skipHalfExpanded: Boolean

): BottomSheetNavigator {
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        animationSpec = animationSpec,
        skipHalfExpanded = skipHalfExpanded,
    )
    return remember(sheetState) { BottomSheetNavigator(sheetState) }
}

@Composable
fun XAutoDailyApp(themeViewModel: ThemeViewModel) {
    val bottomSheetNavigator = rememberBottomSheetNavigator(skipHalfExpanded = true)
    val navController = rememberNavController(bottomSheetNavigator)
    ModalBottomSheetLayout(
        modifier = Modifier.imePadding(),
        sheetBackgroundColor = colors.colorBgDialog,
        bottomSheetNavigator = bottomSheetNavigator,
        scrimColor = colors.colorBgMask,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
    ) {
        XAutoDailyNavHost(navController, themeViewModel)
    }

}

@Composable
private fun XAutoDailyNavHost(
    navController: NavHostController,
    themeViewModel: ThemeViewModel
) {
    NavHost(
        navController = navController,
        startDestination = SceneItem.Main.route
    ) {
        addSceneGraph(navController)
        addBottomSheetGraph(navController, themeViewModel)

    }
}

fun NavGraphBuilder.addSceneGraph(
    navController: NavController,
) {
    composable(route = SceneItem.Main.route) {
        MainScene(
            onNavigateToNotice = {
                navController.navigateItem(DialogItem.Notice(it))
            },
            onNavigateToSign = {
                navController.navigateItem(SceneItem.Sign)
            },
            onNavigateToSetting = {
                navController.navigateItem(SceneItem.Setting)
            },
            onNavigateToAbout = {
                navController.navigateItem(SceneItem.About)
            },
        )
    }

    composable(route = SceneItem.Sign.route) {
        SignScene(
            backClick = navController::popBackStack,
            hasBackProvider = { true },
            onNavigateToEditEnvs = { groupId, taskId ->
                navController.navigateItem(SceneItem.EditEnv(groupId, taskId))
            })
    }

    composable(route = SceneItem.About.route) {
        AboutScene(
            hasBackProvider = { true },
            backClick = navController::popBackStack,
            onNavigateToUpdate = {
                navController.navigateItem(DialogItem.Update(it))
            },
            onNavigateToLicense = {
                navController.navigateItem(SceneItem.License)
            },
            onNavigateToDeveloper = {
                navController.navigateItem(SceneItem.Developer)
            },
        )
    }


    composable(route = SceneItem.Setting.route) {
        SettingScene(
            backClick = navController::popBackStack,
            onNavigateToRestore = {
                navController.navigateItem(DialogItem.Restore)
            },
            onNavigateToTheme = {
                navController.navigateItem(DialogItem.Theme)
            },
            onNavigateToSignState = {
                navController.navigateItem(SceneItem.SignState)
            },
            hasBackProvider = { true },
        )
    }



    composable(route = SceneItem.Developer.route) {
        DeveloperScene(
            backClick = navController::popBackStack,
        )
    }

    composable(route = SceneItem.License.route) {
        LicenseScene(backClick = navController::popBackStack)
    }

    composable(route = SceneItem.SignState.route) {
        SignStateScene(backClick = navController::popBackStack)
    }

    composable(route = "${Screen.EditEnv.name}/{taskGroup}/{taskId}") { backStackEntry ->
        val groupId = backStackEntry.arguments!!.getString("taskGroup")!!
        val taskId = backStackEntry.arguments!!.getString("taskId")!!

        EditEnvScene(
            onNavigateToSelection = { type, name, value, limit ->
                navController.navigateItem(DialogItem.CheckTargets(type, name, value, limit))
            },
            backClick = navController::popBackStack,
            savedStateHandle = backStackEntry.savedStateHandle,
            groupId = groupId,
            taskId = taskId,
        )
    }

}

fun NavGraphBuilder.addBottomSheetGraph(
    navController: NavController,
    themeViewModel: ThemeViewModel,
) {
    bottomSheet(
        route = "${Dialog.Notice.name}/?notice={notice}"
    ) { backStackEntry ->
        NoticeOverlayUI(
            infoText = backStackEntry.arguments!!.getString("notice", ""),
            onDismiss = navController::popBackStack
        )
    }

    bottomSheet(
        route = "${Dialog.Update.name}/?info={info}"
    ) { backStackEntry ->
        UpdateOverlayUI(
            info = { backStackEntry.arguments!!.getString("info", "") },
            onDismiss = navController::popBackStack
        )
    }

    bottomSheet(route = Dialog.Theme.name) {
        ThemeOverlayUI(
            targetTheme = themeViewModel::currentTheme,
            targetBlack = themeViewModel::blackTheme,
            onConfirm = themeViewModel::confirmTheme,
            onDismiss = navController::popBackStack
        )
    }

    bottomSheet(
        route = "${Dialog.CheckTargets.name}/?type={type}&name={name}&value={value}&limit={limit}",
        arguments = listOf(
            navArgument("type") { type = NavType.StringType },
            navArgument("name") { type = NavType.StringType },
            navArgument("value") { type = NavType.StringType; defaultValue = "" },
            navArgument("limit") { type = NavType.IntType; defaultValue = 0 }
        )
    ) { backStackEntry ->
        val arguments = backStackEntry.arguments!!
        val type = arguments.getString("type")!!
        val name = arguments.getString("name")!!
        CheckTargetsOverlayUI(
            type = type,
            initialValue = arguments.getString("value", ""),
            limit = arguments.getInt("limit"),
            onConfirm = { string ->
                navController.previousBackStackEntry?.savedStateHandle?.let {
                    it[TASK_ENV_SELECTION_RESULT] = arrayListOf(name, type, string)
                }
                navController.popBackStack()

            },
            onDismiss = navController::popBackStack
        )
    }

    bottomSheet(route = Dialog.Restore.name) { backStackEntry ->
        RestoreOverlayUI(
            onDismiss = navController::popBackStack
        )
    }

}
