package me.teble.xposed.autodaily.ui.scene

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.teble.xposed.autodaily.data.Dependency
import me.teble.xposed.autodaily.ui.composable.TopBar
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.layout.bottomPaddingValue
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme
import me.teble.xposed.autodaily.utils.openUrl

@Composable
fun LicenseScene(backClick: () -> Unit, viewmodel: LicenseViewModel = viewModel()) {
    Scaffold(
        topBar = {
            TopBar(text = "开放源代码许可", backClick = backClick)
        },
        containerColor = XAutodailyTheme.colors.colorBgLayout
    ) { contentPadding ->

        val context = LocalContext.current
        LaunchedEffect(context) {
            viewmodel.readJson(context)
        }

        LazyColumn(
            modifier = Modifier
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
                .clip(SmootherShape(12.dp)),
            contentPadding = bottomPaddingValue,
            // 绘制间隔
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            items(
                items = viewmodel.dependencies,
                key = { it.name },
                contentType = { it.name }) { dependency ->
                DependencyItem(dependency)
            }
        }


    }
}

@Composable
fun DependencyItem(dependency: Dependency) {

    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .background(XAutodailyTheme.colors.colorBgContainer)
            .clickable(role = Role.Button, onClick = {
                context.openUrl(dependency.mavenCoordinates.groupId)
            })
            .padding(vertical = 20.dp, horizontal = 16.dp),
    ) {
        Text(
            text = dependency.name,
            maxLines = 1,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = XAutodailyTheme.colors.colorText
            )
        )

        Text(
            text = dependency.licenses.joinToString(",") {
                it.name
            },
            Modifier.padding(top = 6.dp),
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = XAutodailyTheme.colors.colorTextSecondary
            )
        )

    }
}