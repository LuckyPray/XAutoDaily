package me.teble.xposed.autodaily.ui.scene

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.teble.xposed.autodaily.task.util.ConfigUtil
import me.teble.xposed.autodaily.ui.ConfUnit
import me.teble.xposed.autodaily.ui.enable
import me.teble.xposed.autodaily.utils.TaskExecutor

@Stable
class MainViewModel : ViewModel() {

    private var lastClickTime by mutableLongStateOf(0L)

    var execTaskNum by mutableIntStateOf(0)

    var noticeText by mutableStateOf("")
    var turnTaskSize by mutableIntStateOf(0)

    val snackbarHostState = SnackbarHostState()

    init {
        initExecTaskNum()
        initNotice()
        initTurnTaskSize()
    }


    private fun initNotice() {
        viewModelScope.launch(Dispatchers.IO) {
            val meta = ConfUnit.metaInfoCache ?: ConfigUtil.fetchMeta()
            if (System.currentTimeMillis() - ConfUnit.lastFetchTime > 60 * 60 * 1000L) {
                ConfigUtil.fetchMeta()
            }
            meta?.let {
                withContext(Dispatchers.Main) {
                    noticeText = it.notice?.trimEnd() ?: "暂无公告"
                }
            } ?: run {
                showSnackbar("拉取公告失败")
            }

        }
    }

    private fun initExecTaskNum() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val num = ConfigUtil.getCurrentExecTaskNum()
                for (i in 1..num) {
                    delay(15)
                    withContext(Dispatchers.Main) {
                        execTaskNum += 1
                    }

                }
            } catch (e: Exception) {
                showSnackbar(e.stackTraceToString())
            }
        }
    }

    fun initTurnTaskSize() {
        viewModelScope.launch(Dispatchers.IO) {
            val turnTaskState = ConfigUtil.loadSaveConf().taskGroups.map { it.tasks }.flatten()
                .filter { it.enable }
            withContext(Dispatchers.Main) {
                turnTaskSize = turnTaskState.size
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
            snackbarHostState.showSnackbar(text)
        }
    }

}