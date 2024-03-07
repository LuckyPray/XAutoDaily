package me.teble.xposed.autodaily.ui.scene

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.utils.openUrl

class DeveloperViewModel : ViewModel() {

    private val _snackbarText = MutableSharedFlow<String>()
    val snackbarText = _snackbarText.asSharedFlow()
    fun openAuthorGithub(context: Context, url: String) {
        showSnackbar("正在跳转，请稍后")
        context.openUrl("https://t.me/XAutoDailyChat")
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