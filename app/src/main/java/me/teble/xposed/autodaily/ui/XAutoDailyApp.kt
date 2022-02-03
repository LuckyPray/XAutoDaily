package me.teble.xposed.autodaily.ui

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import me.teble.xposed.autodaily.ui.XAutoDailyApp.EditEnv
import me.teble.xposed.autodaily.ui.XAutoDailyApp.Main
import me.teble.xposed.autodaily.ui.XAutoDailyApp.Setting
import me.teble.xposed.autodaily.ui.XAutoDailyApp.Sign

object XAutoDailyApp {
    const val Main = "MainLayout"
    const val Sign = "SignLayout"
    const val Setting = "SettingLayout"
    const val EditEnv = "EditEnvLayout"
}

@Composable
fun XAutoDailyApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Main
    ) {
        composable(Main) {
            MainLayout(navController = navController)
        }
        composable(Sign) {
            SignLayout(navController = navController)
        }
        composable(Setting) {
            SettingLayout(navController = navController)
        }
        composable(
            route = "$EditEnv/{groupId}/{taskId}",
            arguments = listOf(
                navArgument("groupId") {
                    type = NavType.StringType
                },
                navArgument("taskId") {
                    type = NavType.StringType
                }
            )
        ) {
            EditEnvLayout(
                navController = navController,
                groupId = it.arguments?.getString("groupId"),
                taskId = it.arguments?.getString("taskId")
            )
        }
    }
}

@Composable
fun BackHandler(enabled: Boolean = true, onBack: () -> Unit) {
    val currentOnBack by rememberUpdatedState(onBack)
    val backCallback = remember {
        object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                currentOnBack()
            }
        }
    }
    SideEffect {
        backCallback.isEnabled = enabled
    }
    val backDispatcher = checkNotNull(LocalOnBackPressedDispatcherOwner.current) {
        "No OnBackPressedDispatcherOwner was provided via LocalOnBackPressedDispatcherOwner"
    }.onBackPressedDispatcher
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, backDispatcher) {
        backDispatcher.addCallback(lifecycleOwner, backCallback)
        onDispose {
            backCallback.remove()
        }
    }
}