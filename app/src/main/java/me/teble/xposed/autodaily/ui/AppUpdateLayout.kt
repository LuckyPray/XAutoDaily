package me.teble.xposed.autodaily.ui

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

@Composable
fun AppUpdateLayout(dialog: CustomDialog) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F2F5))
        ) {
            TopAppBar(
                elevation = 0.dp,
                title = {
                    Text("XAutoDaily检测到新版本", color = Color.White)
                },
                backgroundColor = Color(0xFF409EFF),
                modifier = Modifier
                    .background(Color(0xFF409EFF))
            )
            var updateLog by remember { mutableStateOf(emptyList<String>()) }
            LaunchedEffect(updateLog) {
                updateLog = Cache.versionInfoCache?.updateLog?: emptyList()
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
                    Text(
                        text = "${index + 1}. " + updateLog[index],
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
        }
    }
}

@Preview
@Composable
fun AppUpdateLayoutPreview() {
    AppUpdateLayout(CustomDialog(LocalContext.current))
}