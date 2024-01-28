package me.teble.xposed.autodaily.ui.scene

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.Info
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.ceil

@Composable
fun SignScene(navController: NavController, signViewModel: SignViewModel = viewModel()) {
    Column(

    ) {
        TopBar(text = "签到配置", hasBack = true, endIcon = Icons.Info)
        val globalEnable by signViewModel.globalEnable.collectAsState()
        SwitchTextItem(
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
            val groupTitle by remember{
                derivedStateOf {
                    taskGroup.id
                }
            }
            Text(
                text = groupTitle,
                modifier = Modifier
                    .padding(bottom = 8.dp, start = 16.dp),
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF7F98AF)
                )
            )

            val tasks by remember{
                derivedStateOf {
                    taskGroup.tasks
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(SmootherShape(12.dp))
                    .background(color = Color(0xffffffff)),
            ) {
                Log.d("TAG", "GroupColumn:")
                tasks.forEach { task ->

                    val checked = remember {
                        derivedStateOf {
                            task.enable
                        }
                    }
                    val clickFlag by remember {
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

                    if(desc.isNotBlank()){

                        if(clickFlag){
                            SwitchInfoDivideItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(SmootherShape(12.dp)),
                                enable = checked.value,
                                text = title,
                                infoText = desc,
                                onClick = {

                                }
                            )
                        }else{
                            SwitchInfoItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(SmootherShape(12.dp)),
                                enable = checked.value,
                                text = title,
                                infoText = desc,
                                onClick = {

                                }
                            )
                        }


                    }else{
                        if(clickFlag){
                            SwitchTextItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(SmootherShape(12.dp)),
                                enable = checked.value,
                                text = title,
                                onClick = {

                                }
                            )
                        }else{
                            SwitchTextDivideItem(
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
    }
}

@Composable
fun SwitchTextItem(
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
            modifier = Modifier.weight(1f),
            maxLines = 1,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF202124)
            )
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "占位", modifier = Modifier.defaultMinSize(minWidth = 36.dp, minHeight = 24.dp))
    }
}

@Composable
fun SwitchTextDivideItem(
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
            modifier = Modifier.weight(1f),
            maxLines = 1,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF202124)
            )
        )
        Spacer(modifier = Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .width(2.dp)
                .fillMaxHeight()
                .background(color = Color(0xff202124), SmootherShape(2.dp)),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "占位", modifier = Modifier.defaultMinSize(minWidth = 36.dp, minHeight = 24.dp))
    }
}

@Composable
fun SwitchInfoItem(
    modifier: Modifier = Modifier,
    text: String,
    infoText: String,
    onClick: (Boolean) -> Unit,
    enable: Boolean,

    ) {

    Row(
        modifier
            .clickable(role = Role.Switch, onClick = { onClick(!enable) })
            .padding(vertical = 20.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
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

            Text(
                text = infoText,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF4F5355)
                )
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "占位", modifier = Modifier.defaultMinSize(minWidth = 36.dp, minHeight = 24.dp))
    }
}

@Composable
fun SwitchInfoDivideItem(
    modifier: Modifier = Modifier,
    text: String,
    infoText: String,
    onClick: (Boolean) -> Unit,
    enable: Boolean,

    ) {

    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(role = Role.Switch, onClick = { onClick(!enable) })
                .padding(vertical = 20.dp, horizontal = 16.dp),
        ) {
            Text(
                modifier = Modifier.padding(end = 16.dp),
                text = text,
                maxLines = 1,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight(700),
                    color = Color(0xFF202124)
                )
            )

            Text(
                modifier = Modifier.padding(end = 16.dp),
                text = infoText,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF4F5355)
                )
            )
        }
        Text(
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
                .background(color = Color(0xFFE6E6E6), SmootherShape(1.dp)),
            text = ""
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "占位", modifier = Modifier
            .defaultMinSize(minWidth = 36.dp, minHeight = 24.dp)
            .padding(end = 16.dp))
    }
}