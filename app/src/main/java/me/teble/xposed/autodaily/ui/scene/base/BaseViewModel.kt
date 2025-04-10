package me.teble.xposed.autodaily.ui.scene.base

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {

    val snackbarHostState = SnackbarHostState()


    /**
     * 展示对应 Snackbar
     * @param text 文本内容
     */
    protected fun showSnackbar(text: String) {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(text)
        }
    }
}