package me.teble.xposed.autodaily.ui

import android.content.Intent
import android.net.Uri
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
import me.teble.xposed.autodaily.config.Constants
import me.teble.xposed.autodaily.task.util.formatDate
import java.util.*

@Composable
fun AppUpdateLayout(dialog: CustomDialog) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F2F5))
                .padding(bottom = 60.dp)
        ) {
            TopAppBar(
                elevation = 0.dp,
                title = {
                    Text("检测到新版本", color = Color.White)
                },
                backgroundColor = Color(0xFF409EFF),
                modifier = Modifier
                    .background(Color(0xFF409EFF))
            )
            var updateLog by remember { mutableStateOf(emptyList<String>()) }
            LaunchedEffect(updateLog) {
                updateLog = ConfUnit.versionInfoCache?.updateLog?: emptyList()
            }
            LazyColumn(
                modifier = Modifier
                    .padding(top = 13.dp)
                    .padding(horizontal = 13.dp),
                contentPadding = WindowInsets.Companion.navigationBars.asPaddingValues(),
                // 绘制间隔
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(updateLog.size) { index ->
                    Text(
                        text = updateLog[index],
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
                .padding(12.dp),
            horizontalArrangement = Arrangement.End
        ) {
            val context = LocalContext.current
            TextButton(onClick = {
                dialog.dismiss()
                ConfUnit.blockUpdateOneDay = Date().formatDate()
            }) {
                Text(text = "今日不再提醒")
            }

            TextButton(onClick = {
                context.startActivity(Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse(Constants.PAN_URL)
                })
            }) {
                Text(text = "蓝奏云")
            }
            TextButton(onClick = {
                context.startActivity(Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse(Constants.GITHUB_RELEASE_URL)
                })
            }) {
                Text(text = "Github")
            }
        }
    }
}

@Preview
@Composable
fun AppUpdateLayoutPreview() {
    AppUpdateLayout(CustomDialog(LocalContext.current))
}