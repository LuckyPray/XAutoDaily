package me.teble.xposed.autodaily.activity.common

import android.content.res.Configuration
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.agoines.system.common.navigationBarLightMode
import com.agoines.system.common.navigationBarLightOldMode
import com.agoines.system.common.setNavigationBarTranslation
import com.agoines.system.common.setStatusBarTranslation
import com.agoines.system.common.statusBarLightMode
import com.agoines.system.common.statusBarLightOldMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.application.HostAppAccess
import me.teble.xposed.autodaily.application.fetchAppList
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ThemeViewModel by viewModels()
    private val hostPermissionState: HostPermissionState by viewModels()
    private var hostRefreshJob: Job? = null
    private var permissionRequestInFlight = false
    private val permissionPreferences by lazy {
        getSharedPreferences("host_app_access", MODE_PRIVATE)
    }
    private val hostPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        permissionRequestInFlight = false
        // 空结果表示权限请求被中断或取消，不能视为用户已明确拒绝。
        if (results.containsKey(HostAppAccess.PERMISSION)) {
            permissionPreferences.edit()
                .putBoolean("decision_received", true)
                .remove("requested")
                .apply()
        }
        refreshHostApps()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        setStatusBarTranslation()
        setNavigationBarTranslation()
        enableEdgeToEdge()
        when (XAutodailyTheme.getAppTheme()) {
            XAutodailyTheme.Theme.Light -> setSystemBarOldMode()
            XAutodailyTheme.Theme.Dark -> setSystemBarOldMode(false)
            XAutodailyTheme.Theme.System -> setSystemBarOldMode(!isNightMode())

        }
        super.onCreate(savedInstanceState)
        // 状态栏和导航栏沉浸
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.setDecorFitsSystemWindows(window, false)


        setContent {

            when (viewModel.currentTheme) {
                XAutodailyTheme.Theme.Light -> setSystemBarMode()
                XAutodailyTheme.Theme.Dark -> setSystemBarMode(false)
                XAutodailyTheme.Theme.System -> setSystemBarMode(!isNightMode())
            }

            XAutodailyTheme(
                isBlack = viewModel.blackTheme,
                colorTheme = viewModel.currentTheme
            ) {
                ModuleApp(
                    viewModel,
                    onRequestHostAccess = ::requestHostAccess,
                    onOpenHostAccessSettings = ::openHostAccessSettings,
                )
            }

        }
    }

    override fun onPostResume() {
        super.onPostResume()
        // 首页展示读取宿主列表所需权限的说明，并在页面可见后自动请求一次。
        window.decorView.post {
            if (isFinishing || isDestroyed ||
                !lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
            ) return@post
            refreshHostApps()
            if (shouldAutomaticallyRequestHostAccess()) {
                // 先完成首页渲染，再打开系统权限申请页面。
                window.decorView.doOnPreDraw {
                    window.decorView.post {
                        if (shouldAutomaticallyRequestHostAccess()) requestHostAccess()
                    }
                }
                window.decorView.invalidate()
            }
        }
    }

    private fun shouldAutomaticallyRequestHostAccess(): Boolean =
        !isFinishing && !isDestroyed && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) &&
            !permissionRequestInFlight && !hostPermissionState.automaticRequestAttempted &&
            !permissionPreferences.getBoolean("decision_received", false) &&
            HostAppAccess.needsPermission(this)

    override fun onStop() {
        hostRefreshJob?.cancel()
        super.onStop()
    }

    private fun refreshHostApps() {
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) return
        hostRefreshJob?.cancel()
        hostRefreshJob = lifecycleScope.launch { fetchAppList() }
    }

    private fun requestHostAccess() {
        if (permissionRequestInFlight) return
        if (!HostAppAccess.needsPermission(this)) {
            refreshHostApps()
            return
        }
        if (permissionPreferences.getBoolean("decision_received", false) &&
            !ActivityCompat.shouldShowRequestPermissionRationale(this, HostAppAccess.PERMISSION)
        ) {
            openHostAccessSettings()
            return
        }
        hostPermissionState.automaticRequestAttempted = true
        permissionRequestInFlight = true
        runCatching { hostPermissionLauncher.launch(arrayOf(HostAppAccess.PERMISSION)) }.onFailure {
            permissionRequestInFlight = false
            Log.w("XALog", "无法弹出应用列表授权框", it)
            Toast.makeText(this, "无法弹出授权框，请点击应用权限设置", Toast.LENGTH_LONG).show()
        }
    }

    private fun openHostAccessSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
        runCatching { startActivity(intent) }.onFailure {
            Log.w("XALog", "无法打开应用权限设置", it)
            Toast.makeText(this, "请在系统设置中打开 XAutoDaily 的权限管理", Toast.LENGTH_LONG).show()
        }
    }

    private fun setSystemBarOldMode(enable: Boolean = true) {
        window.statusBarLightOldMode(enable)
        window.navigationBarLightOldMode(enable)
    }

    private fun setSystemBarMode(enable: Boolean = true) {
        window.statusBarLightMode(enable)
        window.navigationBarLightMode(enable)
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
