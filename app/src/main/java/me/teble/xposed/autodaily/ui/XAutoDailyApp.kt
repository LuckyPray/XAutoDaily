package me.teble.xposed.autodaily.ui

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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.collections.immutable.toImmutableSet
import me.teble.xposed.autodaily.activity.common.ThemeViewModel
import me.teble.xposed.autodaily.ui.dialog.CheckFriendsOverlayUI
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
    CheckFriends,
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
        SceneItem("${Screen.EditEnv.name}/$taskGroup/$taskId")
}

@Stable
sealed class DialogItem(route: String) : NavigationItem(route) {
    data class Notice(
        val notice: String
    ) : DialogItem("${Dialog.Notice.name}/?notice=$notice")

    data object Theme : DialogItem(Dialog.Theme.name)

    data class CheckFriends(
        val uinListStr: String
    ) : DialogItem("${Dialog.CheckFriends.name}/?uinListStr=$uinListStr")

    data class Update(
        val info: String
    ) : DialogItem("${Dialog.Update.name}/?info=$info")

    data object Restore : DialogItem(Dialog.Restore.name)

}

fun NavController.navigate(item: NavigationItem) {
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
            },
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
            onNavigateToUpdate = {
                navController.navigate(DialogItem.Update(it))
            },
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
            onNavigateToRestore = {
                navController.navigate(DialogItem.Restore)
            },
            onNavigateToTheme = {
                navController.navigate(DialogItem.Theme)
            },
            onNavigateToSignState = {
                navController.navigate(SceneItem.SignState)
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
        EditEnvScene(
            onNavigateToCheckFriends = { navController.navigate(DialogItem.CheckFriends(it)) },
            backClick = navController::popBackStack,
            savedStateHandle = navController.currentBackStackEntry?.savedStateHandle,
            groupId = backStackEntry.arguments!!.getString("taskGroup", ""),
            taskId = backStackEntry.arguments!!.getString("taskId", "")
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

    bottomSheet(route = "${Dialog.CheckFriends.name}/?uinListStr={uinListStr}") { backStackEntry ->
        val uinListStr = backStackEntry.arguments!!.getString("uinListStr", "")
        val uinSet = uinListStr.split(",").toImmutableSet()

        CheckFriendsOverlayUI(
            uinListStr = uinSet,
            onConfirm = { string ->
                navController.previousBackStackEntry?.savedStateHandle?.let {
                    it["uinListStr"] = string
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