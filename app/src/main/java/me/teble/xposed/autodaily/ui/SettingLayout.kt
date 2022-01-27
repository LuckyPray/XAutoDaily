package me.teble.xposed.autodaily.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import me.teble.xposed.autodaily.hook.utils.ToastUtil

@Composable
fun SettingLayout(navController: NavHostController) {
    ActivityView(title = "插件设置", navController = navController) {
        LazyColumn(
            modifier = Modifier.padding(13.dp)
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
                Divider(color = Color(color = 0xFFF2F2F2), thickness = 1.dp)
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