package me.teble.xposed.autodaily.ui.scene

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.utils.openUrl

@Stable
class DeveloperViewModel : ViewModel() {

    val snackbarHostState = SnackbarHostState()
    fun openAuthorGithub(context: Context): (String) -> Unit {
        return { url ->
            showSnackbar("正在跳转，请稍后")
            context.openUrl(url)
        }

    }

    /**
     * 展示对应 Snackbar
     * @param text 文本内容
     */
    private fun showSnackbar(text: String) {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(text)
        }
    }
}