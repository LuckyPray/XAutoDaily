package me.teble.xposed.autodaily.ui.scene

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.utils.TaskExecutor

class HomeViewModel : ViewModel() {

    private var lastClickTime = 0L

    var execTaskNum by mutableIntStateOf(0)

    var noticeDialog by mutableStateOf(false)

    var noticeText by mutableStateOf("")


    private val _snackbarText = MutableSharedFlow<String>()
    val snackbarText = _snackbarText.asSharedFlow()

    init {
        initExecTaskNum()
        initNotice()
    }

    fun showNoticeDialog() {
        updateNoticeDialogState(true)
    }

    fun dismissNoticeDialog() {
        updateNoticeDialogState(false)
    }

    fun updateNoticeDialogState(boolean: Boolean) {
        if (noticeDialog != boolean) {
            noticeDialog = boolean
        }
    }

    private fun initNotice() {
        viewModelScope.launch(IO) {
            val meta = ConfUnit.metaInfoCache ?: ConfigUtil.fetchMeta()
            if (System.currentTimeMillis() - ConfUnit.lastFetchTime > 60 * 60 * 1000L) {
                ConfigUtil.fetchMeta()
            }
            meta?.let {
                withContext(Main) {
                    noticeText = it.notice?.trimEnd() ?: "暂无公告"
                }
            } ?: run {
                showSnackbar("拉取公告失败")
            }

        }
    }

    private fun initExecTaskNum() {
        viewModelScope.launch(IO) {
            try {
                val num = ConfigUtil.getCurrentExecTaskNum()
                for (i in 1..num) {
                    delay(15)
                    withContext(Main) {
                        execTaskNum += 1
                    }

                }
            } catch (e: Exception) {
                showSnackbar(e.stackTraceToString())
            }
        }
    }

    fun signClick() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < 5000L) {
            showSnackbar("点那么快怎么不上天呢")
            return
        }
        lastClickTime = currentTime
        TaskExecutor.handler.sendEmptyMessage(TaskExecutor.EXEC_TASK)
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