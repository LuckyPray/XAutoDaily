package me.teble.xposed.autodaily.ui.scene


import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.teble.xposed.autodaily.ui.composable.DatePicker
import me.teble.xposed.autodaily.ui.composable.FloatingButton
import me.teble.xposed.autodaily.ui.composable.HintEditText
import me.teble.xposed.autodaily.ui.composable.IconEditText
import me.teble.xposed.autodaily.ui.composable.SmallTitle
import me.teble.xposed.autodaily.ui.composable.XaScaffold
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.DisabledAlpha
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors

@Composable
fun EditEnvScene(
    onNavigateToCheckFriends: (String) -> Unit,
    backClick: () -> Unit,
    groupId: String?,
    taskId: String
) {


    val envMap = remember { mutableStateOf("") }

    XaScaffold(
        text = taskId,
        modifier = Modifier,
        backClick = backClick,
        floatingActionButton = {
            FloatingButton(
                modifier = Modifier.padding(end = 16.dp, bottom = 16.dp),
                onClick = {

                }
            )
        },
        containerColor = colors.colorBgContainer
    ) {

        val colors = colors
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(SmootherShape(12.dp))
                .verticalScroll(rememberScrollState())
                .defaultNavigationBarPadding()
        ) {
            var editText by remember { mutableStateOf("") }

            SmallTitle(
                title = "好友清单",
                modifier = Modifier
                    .padding(bottom = 8.dp, start = 16.dp, top = 16.dp)
            )
            IconEditText(
                value = { editText },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(SmootherShape(12.dp))
                    .drawBehind {
                        drawRect(colors.colorBgEdit)
                    },
                hintText = "好友清单",
                iconClick = {
                    onNavigateToCheckFriends("123")
                }
            ) {
                editText = it
            }


            var fireText by remember { mutableStateOf("") }
            SmallTitle(
                title = "续火文字（随机选择，多个文字之间使用 | 隔开）",
                modifier = Modifier
                    .padding(bottom = 8.dp, start = 16.dp, top = 24.dp)
            )

            HintEditText(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(SmootherShape(12.dp))
                    .drawBehind {
                        drawRect(colors.colorBgEdit)
                    }
                    .padding(vertical = 18.dp, horizontal = 16.dp),
                value = { fireText },
                onValueChange = {
                    fireText = it
                },
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.colorText
                ),
                singleLine = true,
                hintText = "例如 早啊|早安|早上好",
                hintTextStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.colorText.copy(alpha = DisabledAlpha)
                ),
            )

            SmallTitle(
                title = "执行时间",
                modifier = Modifier
                    .padding(bottom = 8.dp, start = 16.dp, top = 24.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                DatePicker(Modifier.width(328.dp)) {
                    Log.d("time", "EditEnvScene: ${it}")
                }
            }


        }

    }


}