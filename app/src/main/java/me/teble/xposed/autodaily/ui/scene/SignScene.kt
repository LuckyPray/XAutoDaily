package me.teble.xposed.autodaily.ui.scene

import androidx.collection.mutableFloatListOf
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import me.teble.xposed.autodaily.ui.composable.TopBar
import me.teble.xposed.autodaily.ui.enable
import me.teble.xposed.autodaily.ui.errInfo
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.Info
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SignScene(navController: NavController, signViewModel: SignViewModel = viewModel()) {
    Column(

    ) {
        TopBar(text = "签到配置", hasBack = true, endIcon = Icons.Info)
        val globalEnable by signViewModel.globalEnable.collectAsState()
        SwitchItem(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(SmootherShape(12.dp))
                .background(color = Color(0xffffffff)),
            text = "总开关",
            onClick = {
                signViewModel.updateGlobalEnable(it)
            },
            enable = globalEnable
        )
        GroupColumn()
    }

}

@Composable
fun GroupColumn(signViewModel: SignViewModel = viewModel()) {

    val taskGroups by signViewModel.taskGroupsState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .clip(SmootherShape(12.dp)),
        contentPadding = WindowInsets.navigationBars.asPaddingValues(),
        // 绘制间隔
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {

        items(items = taskGroups, key = { it.id }, contentType = { it.type }) { taskGroup ->
            Text(
                text = taskGroup.id,
                modifier = Modifier
                    .padding(bottom = 8.dp, start = 16.dp),
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF7F98AF)
                )
            )


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(SmootherShape(12.dp))
                    .background(color = Color(0xffffffff)),
            ) {
                taskGroup.tasks.forEach { task ->
                    val checked = remember {
                        derivedStateOf {
                            task.enable
                        }
                    }
                    val clickFlag = remember {
                        derivedStateOf {
                            !task.envs.isNullOrEmpty()
                        }
                    }
                    val desc by remember {
                        derivedStateOf {
                            task.desc
                        }
                    }
                    val title by remember {
                        derivedStateOf {
                            task.id
                        }
                    }

                    SwitchItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(SmootherShape(12.dp)),
                        enable = checked.value,
                        text = title,
                        onClick = {

                        }

                    )
                }

            }

        }
    }
}

@Composable
fun SwitchItem(
    modifier: Modifier = Modifier,
    text: String,
    onClick: (Boolean) -> Unit,
    enable: Boolean,

    ) {
    Row(
        modifier
            .clickable(role = Role.Switch, onClick = { onClick(!enable) })
            .padding(vertical = 20.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            maxLines = 1,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF202124)
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "占位", modifier = Modifier.height(24.dp))
    }
}