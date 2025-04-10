package me.teble.xposed.autodaily.ui.scene

import android.content.Context
import androidx.compose.runtime.Stable
import me.teble.xposed.autodaily.ui.scene.base.BaseViewModel
import me.teble.xposed.autodaily.utils.openUrl

@Stable
class DeveloperViewModel : BaseViewModel() {
    fun openAuthorGithub(context: Context): (String) -> Unit {
        return { url ->
            showSnackbar("正在跳转，请稍后")
            context.openUrl(url)
        }

    }
}