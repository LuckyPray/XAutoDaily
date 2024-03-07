package me.teble.xposed.autodaily.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.teble.xposed.autodaily.activity.module.MainThemeViewModel
import me.teble.xposed.autodaily.ui.layout.contentWindowInsets
import me.teble.xposed.autodaily.ui.scene.AboutScene
import me.teble.xposed.autodaily.ui.scene.DeveloperScene
import me.teble.xposed.autodaily.ui.scene.EditEnvScene
import me.teble.xposed.autodaily.ui.scene.LicenseScene
import me.teble.xposed.autodaily.ui.scene.MainScreen
import me.teble.xposed.autodaily.ui.scene.SettingScene
import me.teble.xposed.autodaily.ui.scene.SignScene
import me.teble.xposed.autodaily.ui.scene.SignStateScene
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors


sealed class NavigationItem {
    data object Sign : NavigationItem()
    data object About : NavigationItem()
    data object Setting : NavigationItem()
    data object Developer : NavigationItem()
    data object License : NavigationItem()

    data object SignState : NavigationItem()
    data class EditEnv(val taskGroup: String, val taskId: String) :
        NavigationItem()
}


class XAutoDailyItem(val route: NavigationItem) {
    companion object {
        val Saver: Saver<XAutoDailyItem?, NavigationItem> = Saver(
            { it?.route },
            ::XAutoDailyItem
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun XAutoDailyApp(themeViewModel: MainThemeViewModel) {
    val systemDirective = calculateDensePaneScaffoldDirective(currentWindowAdaptiveInfo())
    val customDirective = PaneScaffoldDirective(
        contentPadding = PaddingValues(0.dp),
        maxHorizontalPartitions = systemDirective.maxHorizontalPartitions,
        horizontalPartitionSpacerSize = 0.dp,
        maxVerticalPartitions = systemDirective.maxVerticalPartitions,
        verticalPartitionSpacerSize = systemDirective.verticalPartitionSpacerSize,
        excludedBounds = systemDirective.excludedBounds
    )

    val navigator =
        rememberListDetailPaneScaffoldNavigator<Nothing>(scaffoldDirective = customDirective)


    var selectedItem: XAutoDailyItem? by rememberSaveable(stateSaver = XAutoDailyItem.Saver) {
        mutableStateOf(null)
    }


    val navigateStacks = remember {
        mutableStateListOf<NavigationItem>()
    }

    val backEvent: () -> Unit = {
        if (navigateStacks.size == 1) {
            navigator.navigateBack()
        }
        navigateStacks.removeLast()
        selectedItem = if (navigateStacks.isNotEmpty()) {
            XAutoDailyItem(navigateStacks.last())
        } else {
            null
        }
    }

    BackHandler(selectedItem != null || navigator.canNavigateBack()) {
        backEvent()

    }

    ListDetailPaneScaffold(
        scaffoldState = navigator.scaffoldState,
        windowInsets = contentWindowInsets,
        modifier = Modifier.background(colors.colorBgLayout),
        listPane = {
            AnimatedPane(Modifier) {
                MainScreen(
                    onItemClick = {
                        navigator.navigateTo(pane = ListDetailPaneScaffoldRole.Detail)
                        selectedItem = XAutoDailyItem(it)
                        navigateStacks.clear()
                        navigateStacks.add(it)
                    })
            }
        },
        detailPane = {
            AnimatedPane(Modifier) {
                selectedItem?.route?.let { route ->
                    when (route) {
                        NavigationItem.About -> {
                            AboutScene(backClick = backEvent, onItemClick = {
                                navigateStacks.add(it)
                                selectedItem = XAutoDailyItem(it)
                            })
                        }

                        NavigationItem.Developer -> {
                            DeveloperScene(
                                backClick = backEvent
                            )
                        }

                        is NavigationItem.EditEnv -> {
                            EditEnvScene(
                                backClick = backEvent,
                                route.taskGroup,
                                route.taskId
                            )
                        }

                        NavigationItem.License -> {
                            LicenseScene(
                                backClick = backEvent,
                            )
                        }


                        NavigationItem.Setting -> {
                            SettingScene(
                                backClick = backEvent,
                                onItemClick = {
                                    selectedItem = XAutoDailyItem(it)
                                    navigateStacks.add(it)
                                },
                                themeViewModel = themeViewModel
                            )
                        }

                        NavigationItem.Sign -> {
                            SignScene(
                                backClick = backEvent,
                                onItemClick = {
                                    selectedItem = XAutoDailyItem(it)
                                    navigateStacks.add(it)
                                }
                            )
                        }

                        NavigationItem.SignState -> {
                            SignStateScene(
                                backClick = backEvent
                            )
                        }

                    }
                }

            }
        }
    )

}