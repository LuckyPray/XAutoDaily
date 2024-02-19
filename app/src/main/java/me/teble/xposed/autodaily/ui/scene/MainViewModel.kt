package me.teble.xposed.autodaily.ui.scene

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.utils.TaskExecutor

class MainViewModel : ViewModel() {

    private var lastClickTime = 0L

    private val _execTaskNum = MutableStateFlow(0)
    val execTaskNum = _execTaskNum.asStateFlow()

    private val _notice = MutableStateFlow("暂无公告")
    val notice = _notice.asStateFlow()


    private val _showUpdateDialog = MutableStateFlow(false)
    val showUpdateDialog = _showUpdateDialog.asStateFlow()
    private val _updateDialogText = MutableStateFlow("")
    val updateDialogText = _updateDialogText.asStateFlow()

    init {
        initExecTaskNum()
        initNotice()
        initUpdate()
    }

    fun dismissDialogState() {
        _showUpdateDialog.value = false
    }

    private fun initUpdate() {
        viewModelScope.launch(IO) {
            val res = ConfigUtil.checkUpdate(true)
            if (res) {
                _showUpdateDialog.value = true
                _updateDialogText.value = ConfUnit.metaInfoCache?.app?.updateLog ?: ""
            }
        }
    }


    private fun initNotice() {
        viewModelScope.launch(IO) {
            val meta = ConfUnit.metaInfoCache ?: ConfigUtil.fetchMeta()
            if (System.currentTimeMillis() - ConfUnit.lastFetchTime > 60 * 60 * 1000L) {
                ConfigUtil.fetchMeta()
            }
            meta?.let {
                _notice.value = it.notice?.trimEnd() ?: "暂无公告"
            } ?: run {
                ToastUtil.send("拉取公告失败")
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
            ToastUtil.send("点那么快怎么不上天呢")
            return
        }
        lastClickTime = currentTime
        TaskExecutor.handler.sendEmptyMessage(TaskExecutor.EXEC_TASK)
    }
}