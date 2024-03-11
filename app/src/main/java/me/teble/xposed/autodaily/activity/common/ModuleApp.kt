package me.teble.xposed.autodaily.activity.common

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.teble.xposed.autodaily.ui.layout.contentWindowInsets
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ModuleApp(viewModel: ModuleThemeViewModel) {

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
        rememberListDetailPaneScaffoldNavigator<Nothing>(scaffoldDirective = customDirective)

    BackHandler(scaffoldNavigator.canNavigateBack(), scaffoldNavigator::navigateBack)

    ListDetailPaneScaffold(
        scaffoldState = scaffoldNavigator.scaffoldState,
        modifier = Modifier.background(color = colors.colorBgLayout),
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
                    themeViewModel = viewModel,
                    onBackClick = scaffoldNavigator::navigateBack
                )

            }
        }
    )
}