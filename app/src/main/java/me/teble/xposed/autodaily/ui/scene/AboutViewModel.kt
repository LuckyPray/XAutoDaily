package me.teble.xposed.autodaily.ui.scene

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.config.ALIPAY_QRCODE
import me.teble.xposed.autodaily.hook.base.hostVersionCode
import me.teble.xposed.autodaily.hook.base.hostVersionName
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.ui.scene.base.BaseViewModel
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.TimeUtil
import me.teble.xposed.autodaily.utils.openUrl

@Stable
class AboutViewModel : BaseViewModel() {

    var moduleVersionName by mutableStateOf("")

    var moduleVersionCode by mutableIntStateOf(0)


    var qqVersionName by mutableStateOf("")

    var qqVersionCode by mutableLongStateOf(0L)


    var configVersion by mutableIntStateOf(0)


    var updateDialogText by mutableStateOf("")


    var hasUpdate by mutableStateOf(false)

    private var lastClickTime by mutableLongStateOf(0L)

    init {
        moduleVersionName = BuildConfig.VERSION_NAME
        moduleVersionCode = BuildConfig.VERSION_CODE
        qqVersionName = hostVersionName
        qqVersionCode = hostVersionCode
        configVersion = ConfigUtil.loadSaveConf().version

        ConfigUtil.fetchMeta()?.let {
            // 检查新版本
            if (BuildConfig.VERSION_CODE < it.app.versionCode) {
                hasUpdate = true
            }
        }
    }


    fun updateApp(onNavigateToUpdate: (String) -> Unit) {
        val time = TimeUtil.cnTimeMillis()
        if (time - lastClickTime < 15_000) {
            showSnackbar("不要频繁点击哦~")
            return
        }
        lastClickTime = time

        showSnackbar("正在检测更新")
        viewModelScope.launch(IO) {
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
                    hasUpdate = true
                    updateDialogText =
                        ConfUnit.metaInfoCache?.app?.updateLog ?: ""
                    onNavigateToUpdate(updateDialogText)
                    showSnackbar("插件版本存在更新")
                    return@launch
                }
            }
            showSnackbar("当前插件与配置均是最新版本")


            configVersion = ConfigUtil.loadSaveConf().version
        }
    }




    fun openGithub(context: Context): () -> Unit {
        return {
            showSnackbar("正在跳转，请稍后")
            context.openUrl("https://github.com/LuckyPray/XAutoDaily")
        }

    }

    fun openTelegram(context: Context): () -> Unit {
        return {
            showSnackbar("正在跳转，请稍后")
            context.openUrl("https://t.me/XAutoDailyChat")
        }

    }

    fun openAliPay(context: Context): () -> Unit {
        return {
            try {
                showSnackbar("正在跳转，请稍后")
                context.openUrl(
                    "alipayqr://platformapi/startapp?saId=10000007&clientVersion=" +
                            "3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F$ALIPAY_QRCODE%3F_s%3Dweb-other"
                )
            } catch (e: Exception) {
                LogUtil.e(e, "open alipay qr error: ")
                context.openUrl("https://mobilecodec.alipay.com/client_download.htm?qrcode=$ALIPAY_QRCODE")
            }
        }
    }
}