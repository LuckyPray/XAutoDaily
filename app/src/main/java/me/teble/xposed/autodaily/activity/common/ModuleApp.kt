package me.teble.xposed.autodaily.activity.common

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ModuleApp(viewModel: ThemeViewModel) {

    val scope = rememberCoroutineScope()


    val scaffoldNavigator =
        rememberListDetailPaneScaffoldNavigator<Nothing>()

    BackHandler(scaffoldNavigator.canNavigateBack(), {
        scope.launch {
            scaffoldNavigator.navigateBack()
        }

    })



    ListDetailPaneScaffold(
        directive = scaffoldNavigator.scaffoldDirective,
        modifier = Modifier.background(color = colors.colorBgLayout),
        scaffoldState = scaffoldNavigator.scaffoldState,
        listPane = {
            AnimatedPane(modifier = Modifier) {
                ModuleScene(
                    onSettingClick = {
                        scope.launch {
                            scaffoldNavigator.navigateTo(pane = ListDetailPaneScaffoldRole.Detail)
                        }

                    },
                )
            }
        },
        detailPane = {
            AnimatedPane(Modifier) {
                SettingScene(
                    themeViewModel = viewModel,
                    hasBackProvider = scaffoldNavigator::canNavigateBack,
                    onBackClick = {
                        scope.launch {
                            scaffoldNavigator.navigateBack()
                        }

                    }
                )
            }
        }
    )


}
