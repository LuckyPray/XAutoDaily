package me.teble.xposed.autodaily.ui.scene

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.config.ALIPAY_QRCODE
import me.teble.xposed.autodaily.hook.base.hostVersionCode
import me.teble.xposed.autodaily.hook.base.hostVersionName
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.ui.navigateUrl
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.TimeUtil
import kotlin.concurrent.thread

class AboutViewModel : ViewModel() {

    private val _moduleVersionName = MutableStateFlow("")
    val moduleVersionName = _moduleVersionName.asStateFlow()

    private val _moduleVersionCode = MutableStateFlow(0)
    val moduleVersionCode = _moduleVersionCode.asStateFlow()


    private val _qqVersionName = MutableStateFlow("")
    val qqVersionName = _qqVersionName.asStateFlow()

    private val _qqVersionCode = MutableStateFlow(0L)
    val qqVersionCode = _qqVersionCode.asStateFlow()

    private val _configVersion = MutableStateFlow(0)
    val configVersion = _configVersion.asStateFlow()

    private val _snackbarText = MutableSharedFlow<String>()
    val snackbarText = _snackbarText.asSharedFlow()

    private val _showUpdateDialog = MutableStateFlow(false)
    val showUpdateDialog = _showUpdateDialog.asStateFlow()

    private val _updateDialogText = MutableStateFlow("")
    val updateDialogText = _updateDialogText.asStateFlow()

    private val _hasUpdate = MutableStateFlow(false)
    val hasUpdate = _hasUpdate.asStateFlow()

    val lastClickTime = MutableStateFlow(0L)

    init {
        _moduleVersionName.value = BuildConfig.VERSION_NAME
        _moduleVersionCode.value = BuildConfig.VERSION_CODE
        _qqVersionName.value = hostVersionName
        _qqVersionCode.value = hostVersionCode
        _configVersion.value = ConfigUtil.loadSaveConf().version

        ConfigUtil.fetchMeta()?.let {
            // 检查新版本
            if (BuildConfig.VERSION_CODE < it.app.versionCode) {
                _hasUpdate.value = true
            }
        }
    }

    fun updateApp() {
        val time = TimeUtil.cnTimeMillis()
        if (time - lastClickTime.value < 15_000) {
            showSnackbar("不要频繁点击哦~")
            return
        }
        lastClickTime.value = time
        thread {
            showSnackbar("正在检测更新")


            viewModelScope.launch(Dispatchers.IO) {
                val info = ConfigUtil.fetchMeta()
                info?.let {
                    val currConfVer = ConfigUtil.loadSaveConf().version
                    if (currConfVer < info.config.version) {
                        if (BuildConfig.VERSION_CODE >= info.config.needAppVersion) {
                            return@launch
                        } else {
//                    XANotification.notify("插件版本过低，无法应用最新配置，推荐更新插件")
                            return@launch
                        }
                    }
                    if (BuildConfig.VERSION_CODE < info.app.versionCode) {
                        _showUpdateDialog.value = true
                        _updateDialogText.value =
                            ConfUnit.metaInfoCache?.app?.updateLog ?: ""
                        showSnackbar("插件版本存在更新")
                        return@launch
                    }
                }
                showSnackbar("当前插件与配置均是最新版本")

            }

            _configVersion.value = ConfigUtil.loadSaveConf().version
        }
    }

    fun openGithub(navController: NavController) {
        showSnackbar("正在跳转，请稍后")
        navController.navigateUrl("https://github.com/LuckyPray/XAutoDaily")
    }

    fun openTelegram(navController: NavController) {
        showSnackbar("正在跳转，请稍后")
        navController.navigateUrl("https://t.me/XAutoDailyChat")
    }

    fun openAliPay(navController: NavController) {
        try {
            showSnackbar("正在跳转，请稍后")
            navController.navigateUrl(
                "alipayqr://platformapi/startapp?saId=10000007&clientVersion=" +
                        "3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F$ALIPAY_QRCODE%3F_s%3Dweb-other"
            )
        } catch (e: Exception) {
            LogUtil.e(e, "open alipay qr error: ")
            navController.navigateUrl("https://mobilecodec.alipay.com/client_download.htm?qrcode=$ALIPAY_QRCODE")
        }
    }

    /**
     * 展示对应 Snackbar
     * @param text 文本内容
     */
    private fun showSnackbar(text: String) {
        viewModelScope.launch {
            _snackbarText.emit(text)
        }
    }
}