package me.teble.xposed.autodaily.activity.common

import androidx.activity.compose.BackHandler
import androidx.annotation.Keep
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.adaptive.AnimatedPane
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.PaneScaffoldDirective
import androidx.compose.material3.adaptive.calculateDensePaneScaffoldDirective
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.teble.xposed.autodaily.ui.layout.contentWindowInsets

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

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ModuleApp() {
//    val navController = rememberNavController()
//    NavHost(
//        navController = navController,
//        startDestination = NavigationItem.Module.route
//    ) {
//        composable(NavigationItem.Module.route) {
//            ModuleScene(navController)
//        }
//
//        composable(NavigationItem.Setting.route) {
//            SettingScene(navController)
//        }
//
//    }


// Create the ListDetailPaneScaffoldState

    val systemDirective = calculateDensePaneScaffoldDirective(currentWindowAdaptiveInfo())


    val customDirective = PaneScaffoldDirective(
        contentPadding = PaddingValues(0.dp),
        maxHorizontalPartitions = systemDirective.maxHorizontalPartitions,
        horizontalPartitionSpacerSize = 0.dp,
        maxVerticalPartitions = systemDirective.maxVerticalPartitions,
        verticalPartitionSpacerSize = systemDirective.verticalPartitionSpacerSize,
        excludedBounds = systemDirective.excludedBounds
    )

    val scaffoldNavigator =
        rememberListDetailPaneScaffoldNavigator<Nothing>(customDirective)

    BackHandler(scaffoldNavigator.canNavigateBack()) {
        scaffoldNavigator.navigateBack()
    }

    ListDetailPaneScaffold(
        scaffoldState = scaffoldNavigator.scaffoldState,
        windowInsets = contentWindowInsets,
        listPane = {
            AnimatedPane(Modifier) {
                ModuleScene(
                    onSettingClick = {
                        scaffoldNavigator.navigateTo(pane = ListDetailPaneScaffoldRole.Detail)
                    }
                )
            }
        },
        detailPane = {
            AnimatedPane(Modifier) {
                SettingScene(
                    onBackClick = {
                        scaffoldNavigator.navigateBack()
                    }
                )

            }
        }
    )
}