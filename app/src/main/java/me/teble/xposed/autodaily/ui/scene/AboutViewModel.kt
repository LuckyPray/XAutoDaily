package me.teble.xposed.autodaily.ui.scene

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.teble.xposed.autodaily.BuildConfig
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


    var checkingUpdate by mutableStateOf(false)
        private set

    var hasUpdate by mutableStateOf(false)

    private var lastClickTime by mutableLongStateOf(0L)

    init {
        moduleVersionName = BuildConfig.VERSION_NAME
        moduleVersionCode = BuildConfig.VERSION_CODE
        qqVersionName = hostVersionName
        qqVersionCode = hostVersionCode
        viewModelScope.launch {
            configVersion = withContext(IO) { ConfigUtil.loadSaveConf().version }
            withContext(IO) { ConfUnit.metaInfoCache ?: ConfigUtil.fetchMeta() }?.let {
                if (lastClickTime == 0L) hasUpdate = BuildConfig.VERSION_CODE < it.app.versionCode
            }
        }
    }


    fun updateApp(onNavigateToUpdate: (String) -> Unit) {
        if (checkingUpdate) return
        val time = TimeUtil.cnTimeMillis()
        if (time - lastClickTime < 15_000) {
            showSnackbar("请稍后再检查")
            return
        }
        lastClickTime = time

        checkingUpdate = true
        viewModelScope.launch {
            try {
                val result = withContext(IO) { ConfigUtil.checkUpdate(notifyConfigUpdate = false) }
                if (result == null) {
                    showSnackbar("获取更新信息失败，请稍后重试")
                    return@launch
                }
                configVersion = result.configVersion
                hasUpdate = result.appUpdate != null
                showSnackbar(result.message)
                result.appUpdate?.let { onNavigateToUpdate(it.updateLog.orEmpty()) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                LogUtil.e(e, "检查更新失败")
                showSnackbar("检查更新失败，请查看日志")
            } finally {
                checkingUpdate = false
            }
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

}
