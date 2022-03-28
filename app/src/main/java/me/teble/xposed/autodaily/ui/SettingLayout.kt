package me.teble.xposed.autodaily.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.utils.FileUtil

@Composable
fun SettingLayout(navController: NavHostController) {
    ActivityView(title = "插件设置") {
        LazyColumn(
            modifier = Modifier
                .padding(top = 13.dp)
                .padding(horizontal = 13.dp),
            contentPadding = rememberInsetsPaddingValues(LocalWindowInsets.current.navigationBars),
            // 绘制间隔
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                LineSwitch(
                    title = "免打扰",
                    desc = "关闭签到提醒",
                    checked = mutableStateOf(false),
                    onChange = {
                        ToastUtil.send("暂未开发")
                    }
                )
            }
            item {
                LineSwitch(
                    title = "多线程执行",
                    desc = "是否启用线程池执行任务，加快任务执行速度",
                    checked = mutableStateOf(false),
                    onChange = {
                        ToastUtil.send("暂未开发")
                    }
                )
            }
            item {
                LineButton(
                    title = "日志导出",
                    desc = "保存日志文件到 /Download 目录",
                    onClick = {
                        MainScope().launch(IO) {
                            FileUtil.saveLogs()
                            ToastUtil.send("保存成功")
                        }
                    },
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
fun PreviewSettingLayout() {
    SettingLayout(rememberNavController())
}