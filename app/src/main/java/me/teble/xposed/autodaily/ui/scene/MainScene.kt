package me.teble.xposed.autodaily.ui.scene


import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialogProperties
import me.teble.xposed.autodaily.ui.NavigationItem
import me.teble.xposed.autodaily.ui.composable.TopBar
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.About
import me.teble.xposed.autodaily.ui.icon.icons.ChevronRight
import me.teble.xposed.autodaily.ui.icon.icons.Configuration
import me.teble.xposed.autodaily.ui.icon.icons.Notice
import me.teble.xposed.autodaily.ui.icon.icons.Script
import me.teble.xposed.autodaily.ui.icon.icons.Setting
import me.teble.xposed.autodaily.ui.navigate


@Composable
fun MainScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        TopBar(text = "XAutoDaily", endIcon = Icons.Notice, backClick = {
            navController.popBackStack()
        })
        Banner()
        GridLayout(navController)
    }

    UpdateDialog()


}

@Composable
private fun GridLayout(navController: NavController, viewModel: MainViewModel = viewModel()) {
    val execTaskNum by viewModel.execTaskNum.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .padding(top = 32.dp)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CardItem(
                iconColor = Color(0xFF47B6FF),
                Icons.Configuration,
                "签到配置",
                "已启用 $execTaskNum 项",
                true
            ) {
                navController.navigate(NavigationItem.Sign, NavigationItem.Main)
            }
            CardItem(
                iconColor = Color(0xFF8286FF),
                Icons.Script,
                "自定义脚本",
                "敬请期待",
                false
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFFFFFFF), SmootherShape(12.dp)),
        ) {
            TextItem(
                iconColor = Color(0xFF60D893),
                Icons.Setting,
                "设置"

            )
            TextItem(
                iconColor = Color(0xFFFFBC04),
                Icons.About,
                "关于"
            )

        }
    }


}


@Composable
private fun UpdateDialog(viewModel: MainViewModel = viewModel()) {
    val updateDialogText by viewModel.updateDialogText.collectAsState()
    val showUpdateDialog by viewModel.showUpdateDialog.collectAsState()
    if (showUpdateDialog) {
        BottomSheetDialog(
            onDismissRequest = {
                viewModel.dismissDialogState()
            },
            properties = BottomSheetDialogProperties(
                dismissWithAnimation = true,
                enableEdgeToEdge = true
            ),

            ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color(0xFFFFFFFF))
                    .padding(start = 32.dp, end = 32.dp, bottom = 24.dp)
            ) {
                Text(
                    text = "公告",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight(600),
                        color = Color(0xFF202124),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp)
                )

                Text(
                    text = "",
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(Color(0xFFF7F7F7))
                )

                Text(
                    text = "最后呢，在这片沙漠之中，至少我能知道还会有一个，珍爱这朵花儿的人。有一个人就足够了。最后呢，在这片沙漠之中，至少我能知道还会有一个，珍爱这朵花儿的人。有一个人就足够了。最后呢，在这片沙漠之中，至少我能知道还会有一个，珍爱这朵花儿的人。有一个人就足够了。最后呢，在这片沙漠之中，至少我能知道还会有一个，珍爱这朵花儿的人。有一个人就足够了。最后呢，在这片沙漠之中，至少我能知道还会有",
                    modifier = Modifier.padding(top = 24.dp),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF4F5355),
                    )
                )

                Text(
                    text = "确定",
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .clip(SmootherShape(12.dp))
                        .background(Color(0x0F0095FF))
                        .padding(vertical = 16.dp),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight(600),
                        color = Color(0xFF0095FF),
                        textAlign = TextAlign.Center,
                    )
                )
            }
        }
    }

}


@Composable
private fun ColumnScope.Banner() {
    Column(
        modifier = Modifier
            .padding(top = 24.dp)
            .align(alignment = Alignment.CenterHorizontally),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "16",
            style = TextStyle(
                fontSize = 64.sp,
                fontWeight = FontWeight(300),
                color = Color(0xFF2ECC71),
                textAlign = TextAlign.Center,
            )
        )
        Text(
            text = "今日执行",
            style = TextStyle(
                color = Color(0xFF4F5355),
                fontWeight = FontWeight(700),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        )
        Text(
            text = "立即签到",
            modifier = Modifier
                .padding(top = 16.dp)
                .clip(shape = SmootherShape(radius = 24.dp))
                .background(color = Color(0x290095FF))
                .clickable(role = Role.Button, onClick = {})
                .padding(start = 32.dp, top = 10.dp, end = 32.dp, bottom = 10.dp),
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF0095FF),

                textAlign = TextAlign.Center,
            )
        )


    }
}

@Composable
private fun RowScope.CardItem(
    iconColor: Color,
    imageVector: ImageVector,
    text: String,
    subText: String,
    enable: Boolean = true,
    onClick: () -> Unit = {}
) {

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
            .weight(1f)
            .clip(SmootherShape(12.dp))
            .background(color = cardBackground)
            .clickable(role = Role.Button, enabled = enable, onClick = onClick)
            .padding(top = 24.dp, start = 16.dp, bottom = 24.dp)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "",
            modifier = Modifier
                .size(32.dp)
                .background(iconBackgroundColor, CircleShape),
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

@Composable
private fun TextItem(
    iconColor: Color,
    imageVector: ImageVector,
    text: String
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .clickable(role = Role.Button, onClick = {})
            .padding(vertical = 15.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "",
            modifier = Modifier
                .size(32.dp)
                .background(iconColor, CircleShape),
            tint = Color(0xffffffff)
        )

        Text(
            text = text,
            Modifier.padding(start = 16.dp),
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF202124)
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.ChevronRight,
            contentDescription = "",
            modifier = Modifier.size(24.dp),
            tint = Color(0xFFE6E6E6)
        )
    }
}

