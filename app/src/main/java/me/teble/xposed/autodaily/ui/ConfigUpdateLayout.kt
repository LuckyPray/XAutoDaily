package me.teble.xposed.autodaily.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import me.teble.xposed.autodaily.activity.module.ModuleActivity
import me.teble.xposed.autodaily.task.model.UpdateInfo
import me.teble.xposed.autodaily.task.util.ConfigUtil

@SuppressLint("MutableCollectionMutableState")
@Composable
fun ConfigUpdateLayout(dialog: CustomDialog) {
    val context = LocalContext.current
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F2F5))
        ) {
            TopAppBar(
                elevation = 0.dp,
                title = {
                    Text("配置更新日志", color = Color.White)
                },
                backgroundColor = Color(0xFF409EFF),
                modifier = Modifier
                    .background(Color(0xFF409EFF))
            )
            var updateLog by remember { mutableStateOf(emptyList<UpdateInfo>()) }
            LaunchedEffect(updateLog) {
                updateLog = ConfigUtil.loadSaveConf().updateLogs
            }
            LazyColumn(
                modifier = Modifier
                    .padding(top = 13.dp)
                    .padding(horizontal = 13.dp),
                contentPadding = rememberInsetsPaddingValues(LocalWindowInsets.current.navigationBars),
                // 绘制间隔
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(updateLog.size) { index ->
                    val updateInfo = updateLog[updateLog.size - index - 1]
                    Text(
                        text = "v${updateInfo.version}:",
                        fontSize = 18.sp,
                        color = Color(0xFF424242),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(
                        text = updateInfo.desc,
                        fontSize = 18.sp,
                        color = Color(0xFF424242),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomEnd)
                .padding(15.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {
                dialog.dismiss()
            }) {
                Text(text = "确定")
            }
            TextButton(onClick = {
                val intent = Intent(context, ModuleActivity::class.java)
                context.startActivity(intent)
                dialog.dismiss()
            }) {
                Text(text = "前往配置")
            }
        }
    }
}

@Preview
@Composable
fun ConfigUpdateLayoutPreview() {
    ConfigUpdateLayout(CustomDialog(LocalContext.current))
}