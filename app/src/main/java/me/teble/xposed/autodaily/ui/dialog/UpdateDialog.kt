package me.teble.xposed.autodaily.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.Close
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding

class UpdateDialog {
}

@Composable
fun updateLayout() {
    Column {

        Row(
            modifier = Modifier
                .padding(start = 32.dp, end = 26.dp)
                .padding(vertical = 21.dp)
        ) {
            Text(
                text = "新版本",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF202124),
                    textAlign = TextAlign.Center
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Close, contentDescription = "关闭",
                Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = {})
                    .padding(6.dp)
            )
        }
        Divider(
            color = Color(0xFFF7F7F7),
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .padding(bottom = 12.dp)
                .height(1.dp)
                .fillMaxWidth()
        )


        Text(
            text = "1. 修复部分情况下无法正常获取资源导致崩溃的问题",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, false)
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            style = TextStyle(
                color = Color(0xFF4F5355),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )
        )

        Row(
            Modifier
                .fillMaxWidth()
                .defaultNavigationBarPadding()
                .padding(top = 24.dp)
                .padding(horizontal = 32.dp)
        ) {

            Text(
                text = "忽略",
                modifier = Modifier
                    .weight(3f)
                    .clip(SmootherShape(12.dp))
                    .background(Color(0x0F0095FF))
                    .padding(vertical = 16.dp),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0095FF),
                    textAlign = TextAlign.Center,
                )
            )

            Text(
                text = "123 盘",
                modifier = Modifier
                    .weight(4f)
                    .padding(horizontal = 16.dp)
                    .clip(SmootherShape(12.dp))
                    .background(Color(0x0F0095FF))
                    .padding(vertical = 16.dp),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0095FF),
                    textAlign = TextAlign.Center,
                )
            )

            Text(
                text = "GitHub",
                modifier = Modifier
                    .weight(4f)
                    .clip(SmootherShape(12.dp))
                    .background(Color(0x0F0095FF))
                    .padding(vertical = 16.dp),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0095FF),
                    textAlign = TextAlign.Center,
                )
            )
        }

    }
}

@Preview
@Composable
fun Dialog() {
    updateLayout()
}