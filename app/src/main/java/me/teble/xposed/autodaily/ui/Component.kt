//package me.teble.xposed.autodaily.ui
//
//import android.annotation.SuppressLint
//import android.content.ComponentName
//import android.content.pm.PackageManager.*
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.combinedClickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.MoreVert
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.ParagraphStyle
//import androidx.compose.ui.text.SpanStyle
//import androidx.compose.ui.text.buildAnnotatedString
//import androidx.compose.ui.text.style.TextIndent
//import androidx.compose.ui.text.withStyle
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import me.teble.xposed.autodaily.R
//import me.teble.xposed.autodaily.activity.common.MainActivity
//import me.teble.xposed.autodaily.application.xaApp
//import me.teble.xposed.autodaily.application.xaAppIsInit
//
//@Composable
//fun VerticaLine(
//    modifier: Modifier = Modifier,
//    color: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
//    width: Dp = 1.dp,
//    topIndent: Dp = 0.dp
//) {
//    val indentMod = if (topIndent.value != 0f) {
//        Modifier.padding(top = topIndent)
//    } else {
//        Modifier
//    }
//    Box(
//        modifier
//            .then(indentMod)
//            .fillMaxHeight()
//            .width(width)
//            .background(color = color)
//    )
//}
//
//@Composable
//fun AppBar(title: String) {
//    var appInit by remember { mutableStateOf(false) }
//    LaunchedEffect(Unit) {
//        appInit = xaAppIsInit
//    }
//    TopAppBar(
//        elevation = 0.dp,
//        title = {
//            Text(title, color = Color.White)
//        },
//        actions = {
//            if (appInit) {
//                var showMenu by remember { mutableStateOf(false) }
//                var hiddenAppIcon by remember { mutableStateOf(false) }
//                LaunchedEffect(Unit) {
//                    hiddenAppIcon = xaApp.prefs.getBoolean("hidden_app_icon", false)
//                }
//                LaunchedEffect(hiddenAppIcon) {
//                    xaApp.packageManager.setComponentEnabledSetting(
//                        ComponentName(xaApp, MainActivity::class.java.name + "Alias"),
//                        if (hiddenAppIcon) COMPONENT_ENABLED_STATE_DISABLED else COMPONENT_ENABLED_STATE_ENABLED,
//                        DONT_KILL_APP
//                    )
//                    xaApp.prefs.edit()
//                        .putBoolean("hidden_app_icon", hiddenAppIcon)
//                        .apply()
//                }
//                IconButton(onClick = {
//                    showMenu = !showMenu
//                }) {
//                    Icon(Icons.Filled.MoreVert, "menu")
//                    DropdownMenu(
//                        expanded = showMenu,
//                        onDismissRequest = { showMenu = !showMenu }
//                    ) {
//                        DropdownMenuItem(onClick = {
//                            hiddenAppIcon = !hiddenAppIcon
//                        }) {
//                            Row(
//                                verticalAlignment = Alignment.CenterVertically,
//                                horizontalArrangement = Arrangement.SpaceBetween
//                            ) {
//                                Text(text = "隐藏桌面图标")
//                                Checkbox(
//                                    checked = hiddenAppIcon,
//                                    onCheckedChange = {
//                                        hiddenAppIcon = it
//                                    }
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        },
//        backgroundColor = Color(0xFF409EFF),
//        modifier = Modifier
//            .background(Color(0xFF409EFF))
//            .statusBarsPadding(),
//    )
//}
//
//
//@Composable
//fun ActivityView(
//    title: String = "XAutoDaily",
//    unit: @Composable () -> Unit = {}
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF0F2F5))
//    ) {
//        AppBar(title)
//        unit()
//    }
//}
//
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun LineButton(
//    modifier: Modifier = Modifier,
//    title: String,
//    desc: String? = null,
//    otherInfoList: List<String>? = null,
//    onClick: (() -> Unit)? = null,
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(13.dp))
//            .background(Color.White)
//            .combinedClickable(
//                onClick = { onClick?.invoke() }
//            )
//            .then(modifier)
//            .padding(horizontal = 13.dp, vertical = 5.dp)
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween,
//        ) {
//            Column(
//                modifier = Modifier.fillMaxWidth(0.87f)
//            ) {
//                Text(
//                    text = title,
//                    color = Color(0xFF424242),
//                    fontSize = 18.sp,
//                )
//                desc?.let {
//                    Text(
//                        text = it,
//                        fontSize = 14.sp,
//                        color = Color(0xFFC0C4CC)
//                    )
//                }
//                otherInfoList?.forEach {
//                    Text(
//                        text = it,
//                        fontSize = 14.sp,
//                        color = Color(0xFFC0C4CC)
//                    )
//                }
//            }
//            Column(
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally,
//            ) {
//                Image(
//                    painter = painterResource(R.drawable.ic_right),
//                    contentDescription = "",
//                    modifier = Modifier
//                        .width(16.dp)
//                        .height(16.dp),
//                )
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun LineSwitch(
//    modifier: Modifier = Modifier,
//    checked: Boolean,
//    title: String,
//    enabled: Boolean = true,
//    desc: String? = null,
//    otherInfoList: List<String>? = null,
//    onChange: (Boolean) -> Unit = {},
//    onClick: (() -> Unit)? = null,
//    longPress: (() -> Unit)? = null,
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(13.dp))
//            .background(Color.White)
//            .combinedClickable(
//                onClick = {
//                    if (!enabled) return@combinedClickable
//                    if (onClick == null) {
//                        onChange(!checked)
//                    } else {
//                        onClick.invoke()
//                    }
//                },
//                onLongClick = longPress
//            )
//            .then(modifier)
//            .padding(horizontal = 13.dp, vertical = 5.dp)
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween,
//        ) {
//            Column(
//                modifier = Modifier.fillMaxWidth(0.87f)
//            ) {
//                Text(
//                    text = title,
//                    color = Color(0xFF424242),
//                    fontSize = 18.sp,
//                )
//                desc?.let {
//                    Text(
//                        text = it,
//                        fontSize = 14.sp,
//                        color = Color(0xFFC0C4CC)
//                    )
//                }
//                otherInfoList?.forEach {
//                    Text(
//                        text = it,
//                        fontSize = 12.sp,
//                        color = Color(0xFFFF4A4A)
//                    )
//                }
//            }
//            Column(
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally,
//            ) {
//                Switch(
////                    modifier = Modifier.fillMaxHeight(),
//                    checked = checked,
//                    enabled = enabled,
//                    onCheckedChange = {
//                        onChange(it)
//                    }
//                )
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun LineCheckBox(
//    modifier: Modifier = Modifier,
//    checked: Boolean,
//    title: String,
//    enabled: Boolean = true,
//    desc: String? = null,
//    otherInfoList: List<String>? = null,
//    onChange: (Boolean) -> Unit = {},
//    onClick: (() -> Unit)? = null,
//    longPress: (() -> Unit)? = null,
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color.White)
//            .clip(RoundedCornerShape(13.dp))
//            .combinedClickable(
//                onClick = {
//                    if (!enabled) return@combinedClickable
//                    if (onClick == null) {
//                        onChange(!checked)
//                    } else {
//                        onClick.invoke()
//                    }
//                },
//                onLongClick = longPress
//            )
//            .then(modifier)
//            .padding(horizontal = 13.dp, vertical = 5.dp)
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween,
//        ) {
//            Column(
//                modifier = Modifier.fillMaxWidth(0.87f)
//            ) {
//                Text(
//                    text = title,
//                    color = Color(0xFF424242),
//                    fontSize = 18.sp,
//                )
//                desc?.let {
//                    Text(
//                        text = desc,
//                        fontSize = 14.sp,
//                        color = Color(0xFFC0C4CC)
//                    )
//                }
//                otherInfoList?.forEach {
//                    Text(
//                        text = it,
//                        fontSize = 12.sp,
//                        color = Color.Red
//                    )
//                }
//            }
//            Column(
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally,
//            ) {
//                Checkbox(
//                    checked = checked,
//                    enabled = enabled,
//                    onCheckedChange = {
//                        onChange(checked)
//                    },
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun Announcement(
//    modifier: Modifier = Modifier,
//    post: MutableState<String> = mutableStateOf(""),
//) {
//    if (post.value.isNotEmpty()) {
//        Column(
//            verticalArrangement = Arrangement.Top,
//            horizontalAlignment = Alignment.Start,
//            modifier = Modifier
//                .then(modifier)
//                .fillMaxWidth()
//                .background(Color(0xFFF0F2F5))
//                .background(Color.White, RoundedCornerShape(13.dp))
//        ) {
//            Text(
//                text = "公告",
//                color = Color.White,
//                fontSize = 18.sp,
//                modifier = Modifier
//                    .padding(10.dp)
//                    .background(Color(0XFFF56C6C), RoundedCornerShape(50.dp))
//                    .padding(horizontal = 6.dp, vertical = 1.dp)
//            )
//            Divider(color = Color(color = 0xFFF2F2F2), thickness = 1.dp)
//            Column(
//                modifier = Modifier
//                    .padding(start = 12.dp, end = 12.dp, top = 5.dp, bottom = 16.dp)
//            ) {
//                Text(
//                    buildAnnotatedString {
//                        withStyle(
//                            style = ParagraphStyle(textIndent = TextIndent(firstLine = 16.sp))
//                        ) {
//                            withStyle(style = SpanStyle(color = Color(0xFF9A9A9A))) {
//                                append(post.value)
//                            }
//                        }
//                    },
//                    fontSize = 16.sp,
//                    letterSpacing = 0.2.sp,
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun LineSpacer(height: Dp = 10.dp) {
//    Spacer(
//        modifier = Modifier
//            .height(height)
//            .fillMaxWidth()
//    )
//}
//
//
//@Composable
//fun GroupList(
//    title: String,
//    unit: @Composable () -> Unit = {}
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(color = Color.White, shape = RoundedCornerShape(6.dp))
//            .padding(horizontal = 10.dp, vertical = 4.dp),
//        verticalArrangement = Arrangement.Center
//    ) {
//        GroupListTitle(title)
//        unit()
//    }
//}
//
//@Composable
//fun GroupListTitle(text: String) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
////            .height(25.dp)
//            .padding(5.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Image(
//            painter = painterResource(R.drawable.ic_v),
//            contentDescription = "",
//            modifier = Modifier
//                .width(16.dp)
//                .height(16.dp)
//        )
//        Text(text = text, fontSize = 16.sp)
//    }
//}
//
//@SuppressLint("UnrememberedMutableState")
//@Preview
//@Composable
//fun PreviewGroupList() {
//    GroupList(title = "这是标题") {
//        LineSwitch(
//            title = "测试任务",
//            desc = "测试任务",
//            checked = true,
//            onChange = {
//
//            },
//            otherInfoList = listOf("上次执行时间：2020-12-11 19:15:55")
//        )
//        LineCheckBox(
//            title = "好友111111111111111111111111111111111111111111111",
//            desc = "110",
//            checked = true,
//            onChange = {}
//        )
//        LineCheckBox(
//            title = "好友12",
//            desc = "119",
//            checked = true,
//            onChange = {}
//        )
//    }
//}