package me.teble.xposed.autodaily.ui.scene


import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.mandatorySystemGesturesPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.waterfallPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Create
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController


private const val Today = 0
private const val Announcement = 1

@Composable
fun MainScreen(navController: NavController)  {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .waterfallPadding()
                .mandatorySystemGesturesPadding()
                .padding(horizontal = 16.dp)
        ) {
            TopBar()
            Banner()
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(top = 24.dp),
                contentPadding = WindowInsets.navigationBars.asPaddingValues(),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                cardItem(
                    iconColor = Color(0xFF47B6FF),
                    Icons.Default.AccountBox,
                    "签到配置",
                    "已启用 16 项",
                    false

                )
                cardItem(
                    iconColor = Color(0xFF8286FF),
                    Icons.Default.Create,
                    "自定义脚本",
                    "敬请期待",
                    false

                )
                cardItem(
                    iconColor = Color(0xFF60D893),
                    Icons.Default.Call,
                    "设置",
                    "配置模块",
                    false

                )
                cardItem(
                    iconColor = Color(0xFFFFBC04),
                    Icons.Default.Add,
                    "关于",
                    "关于模块",
                    false

                )

            }

        }
        UpdateDialog()

        Fab()
    }

}

@Composable
private fun UpdateDialog(viewModel: MainViewModel = viewModel()) {
    val updateDialogText by viewModel.updateDialogText.collectAsState()
    val showUpdateDialog by viewModel.showUpdateDialog.collectAsState()
    if (showUpdateDialog) {
        me.teble.xposed.autodaily.ui.UpdateDialog(
            title = "版本更新",
            text = updateDialogText,
            onGithub = {
//            navController.context.startActivity(Intent().apply {
//                action = Intent.ACTION_VIEW
//                data = Uri.parse(GITHUB_RELEASE_URL)
//            })
            },
            onLanzou = {
//            navController.context.startActivity(Intent().apply {
//                action = Intent.ACTION_VIEW
//                data = Uri.parse(PAN_URL)
//            })
            },
            onDismiss = {
                viewModel.dismissDialogState()
            }
        )
    }

}

@Composable
private fun TopBar() {
    Text(
        text = "XAutoDaily",
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 16.dp, bottom = 20.dp, top = 10.dp),
        style = TextStyle(
            color = Color(0xFF202124),
            fontWeight = FontWeight(700),
            fontSize = 16.sp
        )
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Banner() {
    val pagerState = rememberPagerState(pageCount = { 2 })
    Box {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(156.dp)
                .clip(RoundedCornerShape(16.dp))
        ) { state ->
            when (state) {
                Today -> TodayCard()
                Announcement -> AnnouncementCard()
            }


        }
        // Indicator
        Indicator(currentPage = pagerState.currentPage, pageCount = pagerState.pageCount)
    }
}

@Composable
private fun TodayCard(
    viewModel: MainViewModel = viewModel()
) {
    // background: linear-gradient(97.16deg, #0065FF 17.78%, #34B6FF 86.9%);
    val colorStops = arrayOf(
        0.1778f to Color(0xff0065FF),
        0.869f to Color(0xff34B6FF)
    )
    val execTaskNum by viewModel.execTaskNum.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.horizontalGradient(colorStops = colorStops))
            .padding(start = 36.dp, top = 24.dp)
    ) {
        // text = execTaskNum
        Text(
            text = "16",
            modifier = Modifier.padding(bottom = 4.dp),
            style = TextStyle(
                color = Color(0xFFFFFFFF),
                fontWeight = FontWeight(700),
                fontSize = 54.sp,
                lineHeight = 64.sp,
                textAlign = TextAlign.Center
            )
        )

        Text(
            text = "今日执行",
            style = TextStyle(
                color = Color(0xFFFFFFFF),
                fontWeight = FontWeight(700),
                fontSize = 22.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun AnnouncementCard(
    viewModel: MainViewModel = viewModel()
) {
    val notice by viewModel.notice.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xDEFFFFFF))
            .padding(start = 32.dp, top = 24.dp)
    ) {
        Text(
            text = "公告", style = TextStyle(
                color = Color(0xFF202124),
                fontWeight = FontWeight(700),
                fontSize = 22.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center
            )
        )
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .width(32.dp)
                .height(4.dp)
                .background(Color(0xFFD6DDE7), shape = RoundedCornerShape(size = 20.dp))
        )

        // text = notice
        Text(
            text = "最后呢，在这片沙漠之中，至少我能知道还会有一个，珍爱这朵花儿的人。有一个人就足够了。",
            maxLines = 3,
            style = TextStyle(
                color = Color(0xFF4F5355),
                fontWeight = FontWeight(400),
                fontSize = 14.sp,
                lineHeight = 16.sp,

            )
        )
    }
}

@Composable
private fun BoxScope.Indicator(
    currentPage: Int,
    pageCount: Int
) {
    Row(
        Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        val selectedColor by animateColorAsState(
            targetValue =
            Color(if (currentPage == Today) 0xFFFFFFFF else 0xFF202124),
            label = ""
        )

        val unSelectedColor by animateColorAsState(
            targetValue =

            Color(if (currentPage == Today) 0x99FFFFFF else 0x61202124),
            label = ""
        )
        repeat(pageCount) { iteration ->
            val color by animateColorAsState(
                targetValue =
                if (currentPage == iteration) selectedColor else unSelectedColor,
                label = ""
            )

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(6.dp)
            )
        }
    }
}


private fun LazyGridScope.cardItem(
    iconColor: Color,
    imageVector: ImageVector,
    text: String,
    subText: String,
    enable: Boolean = true
) {
    item {
        val cardBackground by animateColorAsState(
            targetValue =
            Color(if (enable) 0xFFFFFFFF else 0x99FFFFFF),
            label = ""
        )

        val contentColor by animateColorAsState(
            targetValue = Color(0xffffffff).copy(alpha = if (enable) 1f else 0.6f),
            label = ""
        )
        val iconBackgroundColor by animateColorAsState(
            targetValue = iconColor.copy(alpha = if (enable) 1f else 0.6f),
            label = ""
        )
        val textColor by animateColorAsState(
            targetValue =
            Color(if (enable) 0xFF202124 else 0x61202124),
            label = ""
        )

        val subtextColor by animateColorAsState(
            targetValue =
            Color(if (enable) 0xFF4F5355 else 0x614F5355),
            label = ""
        )

        Column(
            Modifier
                .background(color = cardBackground, shape = RoundedCornerShape(size = 12.dp))
                .padding(top = 24.dp, start = 16.dp, bottom = 24.dp)
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = "",
                modifier = Modifier
                    .size(32.dp)
                    .background(iconBackgroundColor, RoundedCornerShape(16.dp))
                    .padding(8.dp),
                tint = contentColor
            )

            Text(
                text = text, Modifier.padding(top = 16.dp), style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 21.6.sp,
                    fontWeight = FontWeight(700),
                    color = textColor,
                )
            )
            Text(
                text = subText, Modifier.padding(top = 4.dp), style = TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 14.4.sp,
                    fontWeight = FontWeight(400),
                    color = subtextColor,
                )
            )
        }
    }
}

@Composable
fun BoxScope.Fab(viewModel: MainViewModel = viewModel()) {
    // #2ECC71, #31CC2E
    val colorStops = arrayOf(
        0.1778f to Color(0xff2ECC71),
        0.869f to Color(0xff31CC2E)
    )
    Text(text = "立刻签到",
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .navigationBarsPadding()
            .padding(bottom = 48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.horizontalGradient(colorStops = colorStops)
            )
            .clickable {
                viewModel.signClick()
            }
            .padding(vertical = 12.dp, horizontal = 24.dp),
        style = TextStyle(
            fontSize = 14.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight(700),
            color = Color(0xFFFFFFFF),
            textAlign = TextAlign.Center,
        ))
}