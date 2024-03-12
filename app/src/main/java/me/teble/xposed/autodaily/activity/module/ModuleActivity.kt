package me.teble.xposed.autodaily.activity.module

import android.content.res.Configuration
import android.os.Bundle
import android.view.Window
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import com.agoines.system.common.navigationBarLightMode
import com.agoines.system.common.navigationBarLightOldMode
import com.agoines.system.common.setNavigationBarTranslation
import com.agoines.system.common.setStatusBarTranslation
import com.agoines.system.common.statusBarLightMode
import com.agoines.system.common.statusBarLightOldMode
import com.slack.circuit.foundation.Circuit
import me.teble.xposed.autodaily.hook.proxy.activity.BaseActivity
import me.teble.xposed.autodaily.ui.XAutoDailyApp
import me.teble.xposed.autodaily.ui.scene.AboutPresenter
import me.teble.xposed.autodaily.ui.scene.AboutScreen
import me.teble.xposed.autodaily.ui.scene.AboutUI
import me.teble.xposed.autodaily.ui.scene.DeveloperPresenter
import me.teble.xposed.autodaily.ui.scene.DeveloperScreen
import me.teble.xposed.autodaily.ui.scene.DeveloperUI
import me.teble.xposed.autodaily.ui.scene.EditEnvPresenter
import me.teble.xposed.autodaily.ui.scene.EditEnvScreen
import me.teble.xposed.autodaily.ui.scene.EditEnvUI
import me.teble.xposed.autodaily.ui.scene.LicensePresenter
import me.teble.xposed.autodaily.ui.scene.LicenseScreen
import me.teble.xposed.autodaily.ui.scene.LicenseUI
import me.teble.xposed.autodaily.ui.scene.MainPresenter
import me.teble.xposed.autodaily.ui.scene.MainScreen
import me.teble.xposed.autodaily.ui.scene.MainUI
import me.teble.xposed.autodaily.ui.scene.SettingPresenter
import me.teble.xposed.autodaily.ui.scene.SettingScreen
import me.teble.xposed.autodaily.ui.scene.SettingUI
import me.teble.xposed.autodaily.ui.scene.SignPresenter
import me.teble.xposed.autodaily.ui.scene.SignScreen
import me.teble.xposed.autodaily.ui.scene.SignStatePresenter
import me.teble.xposed.autodaily.ui.scene.SignStateScreen
import me.teble.xposed.autodaily.ui.scene.SignStateUI
import me.teble.xposed.autodaily.ui.scene.SignUI
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme

class ModuleActivity : BaseActivity() {

    private val viewModel: MainThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatusBarTranslation()
        setNavigationBarTranslation()

        when (XAutodailyTheme.getCurrentTheme()) {
            XAutodailyTheme.Theme.Light -> {
                window.statusBarLightOldMode()
                window.navigationBarLightOldMode()
            }

            XAutodailyTheme.Theme.Dark -> {
                window.statusBarLightOldMode(false)
                window.navigationBarLightOldMode(false)
            }

            XAutodailyTheme.Theme.System -> {
                window.statusBarLightOldMode(!isNightMode())
                window.navigationBarLightOldMode(!isNightMode())
            }
        }


        super.onCreate(savedInstanceState)
        // 状态栏和导航栏沉浸
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.setDecorFitsSystemWindows(window, false)


        val circuit: Circuit =
            Circuit.Builder()
                .addPresenterFactory(MainPresenter.Factory())
                .addUi<MainScreen, MainScreen.State> { state, modifier ->
                    MainUI(
                        snackbarHostState = state.snackbarHostState,
                        noticeProvider = state.noticeProvider,
                        execTaskTaskNumProvider = state.execTaskTaskNumProvider,

                        signClick = state.signClick,
                        dialogStateProvider = state.dialogStateProvider,
                        dismissDialog = state.dismissDialog,
                        showDialog = state.showDialog,

                        onNavigateToSign = state.onNavigateToSign,
                        onNavigateToSetting = state.onNavigateToSetting,
                        onNavigateToAbout = state.onNavigateToAbout,
                        modifier = modifier
                    )
                }
                .addPresenterFactory(SignPresenter.Factory())
                .addUi<SignScreen, SignScreen.State> { state, modifier ->
                    SignUI(
                        backClick = state.backClick,
                        onNavigateToEditEnvs = state.onNavigateToEditEnvs,
                        taskGroupsState = state.taskGroupsState,

                        globalEnableProvider = state.globalEnableProvider,
                        updateGlobalEnable = state.updateGlobalEnable,
                        modifier = modifier
                    )
                }
                .addPresenterFactory(EditEnvPresenter.Factory())
                .addUi<EditEnvScreen, EditEnvScreen.State> { state, modifier ->
                    EditEnvUI(
                        backClick = state.backClick,
                        groupId = state.groupId, taskId = state.taskId,
                        modifier = modifier
                    )
                }


                .addPresenterFactory(SettingPresenter.Factory())
                .addUi<SettingScreen, SettingScreen.State> { state, modifier ->
                    SettingUI(
                        showTaskToastProvider = state.showTaskToastProvider,
                        usedThreadPoolProvider = state.usedThreadPoolProvider,
                        taskNotificationProvider = state.taskNotificationProvider,
                        taskExceptionNotificationProvider = state.taskExceptionNotificationProvider,
                        logToXposedProvider = state.logToXposedProvider,
                        debugLogProvider = state.debugLogProvider,

                        updateShowTaskToast = state.updateShowTaskToast,
                        updateUsedThreadPool = state.updateUsedThreadPool,
                        updateTaskNotification = state.updateTaskNotification,
                        updateTaskExceptionNotification = state.updateTaskExceptionNotification,
                        updateLogToXposed = state.updateLogToXposed,
                        updateDebugLog = state.updateDebugLog,
                        backClick = state.backClick,
                        onNavigateToSignState = state.onNavigateToSignState,

                        dialogStateProvider = state.dialogStateProvider,
                        dismissDialog = state.dismissDialog,
                        showDialog = state.showDialog,
                        modifier = modifier
                    )
                }
                .addPresenterFactory(SignStatePresenter.Factory())
                .addUi<SignStateScreen, SignStateScreen.State> { state, modifier ->
                    SignStateUI(
                        backClick = state.backClick,
                        modifier = modifier
                    )
                }
                .addPresenterFactory(LicensePresenter.Factory())


                .addPresenterFactory(AboutPresenter.Factory())
                .addUi<AboutScreen, AboutScreen.State> { state, modifier ->
                    AboutUI(
                        snackbarHostState = state.snackbarHostState,

                        backClick = state.backClick,
                        onNavigateToLicense = state.onNavigateToLicense,
                        onNavigateToDeveloper = state.onNavigateToDeveloper,

                        hasUpdateProvider = state.hasUpdateProvider,
                        moduleVersionNameProvider = state.moduleVersionNameProvider,
                        moduleVersionCodeProvider = state.moduleVersionCodeProvider,

                        qqVersionNameProvider = state.qqVersionNameProvider,
                        qqVersionCodeProvider = state.qqVersionCodeProvider,
                        configVersionProvider = state.configVersionProvider,

                        dialogStateProvider = state.dialogStateProvider,
                        dismissDialog = state.dismissDialog,
                        showDialog = state.showDialog,

                        updateApp = state.updateApp,
                        updateConfirm = state.updateConfirm,
                        openGithub = state.openGithub,
                        openTelegram = state.openTelegram,
                        openAliPay = state.openAliPay,
                        modifier = modifier
                    )
                }
                .addUi<LicenseScreen, LicenseScreen.State> { state, modifier ->
                    LicenseUI(
                        backClick = state.backClick,
                        dependencies = state.dependencies,
                        modifier = modifier
                    )
                }
                .addPresenterFactory(DeveloperPresenter.Factory())
                .addUi<DeveloperScreen, DeveloperScreen.State> { state, modifier ->
                    DeveloperUI(
                        backClick = state.backClick,
                        snackbarHostState = state.snackbarHostState,
                        openAuthorGithub = state.openAuthorGithub,
                        modifier = modifier
                    )
                }
                .build()




        setContent {
            val theme by remember { viewModel.currentTheme }
            val isBlack by remember { viewModel.blackTheme }

            when (theme) {
                XAutodailyTheme.Theme.Light -> {
                    window.statusBarLightMode()
                    window.navigationBarLightMode()
                }

                XAutodailyTheme.Theme.Dark -> {
                    window.statusBarLightMode(false)
                    window.navigationBarLightMode(false)
                }

                XAutodailyTheme.Theme.System -> {
                    window.statusBarLightMode(!isNightMode())
                    window.navigationBarLightMode(!isNightMode())
                }

            }

            XAutodailyTheme(isBlack = isBlack, colorTheme = theme) {
                XAutoDailyApp(circuit)

            }

        }
    }

    /**
     * 判断当前是否是暗色模式
     * @return 当前是否是暗色模式
     */
    private fun isNightMode(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }
}