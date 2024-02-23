package me.teble.xposed.autodaily.ui.scene

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.ui.navigateUrl

class DeveloperViewModel : ViewModel() {

    private val _snackbarText = MutableSharedFlow<String>()
    val snackbarText = _snackbarText.asSharedFlow()
    fun openAuthorGithub(navController: NavController, url: String) {
        showSnackbar("正在跳转，请稍后")
        navController.navigateUrl("https://t.me/XAutoDailyChat")
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