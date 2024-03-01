package me.teble.xposed.autodaily.ui.scene

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.utils.TaskExecutor

class HomeViewModel : ViewModel() {

    private var lastClickTime = 0L

    // todo 替换为 mutableStateOf 后有问题
    private val _execTaskNum = MutableStateFlow(0)
    val execTaskNum = _execTaskNum.asStateFlow()

    var noticeDialog by mutableStateOf(false)

    // todo 替换为 mutableStateOf 后有问题
    private val _noticeText = MutableStateFlow("")
    val noticeText = _noticeText.asStateFlow()

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
//                noticeText = it.notice?.trimEnd() ?: "暂无公告"
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
                    _execTaskNum.value++
                }
            } catch (e: Exception) {
                ToastUtil.send(e.stackTraceToString())
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