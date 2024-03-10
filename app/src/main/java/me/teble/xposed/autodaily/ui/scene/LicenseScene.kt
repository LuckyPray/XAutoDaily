package me.teble.xposed.autodaily.ui.scene

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.teble.xposed.autodaily.data.Dependency
import me.teble.xposed.autodaily.ui.composable.Text
import me.teble.xposed.autodaily.ui.composable.TopBar
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme

@Composable
fun LicenseScene(backClick: () -> Unit, viewmodel: LicenseViewModel = viewModel()) {
    Scaffold(
        topBar = {
            TopBar(text = "开放源代码许可", backClick = backClick)
        },
        containerColor = XAutodailyTheme.colors.colorBgLayout
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
                .clip(SmootherShape(12.dp))
                .verticalScroll(rememberScrollState())
                .defaultNavigationBarPadding(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            viewmodel.dependencies.forEach { dependency ->
                DependencyItem(dependency)
            }
        }


    }
}

@Composable
fun DependencyItem(dependency: Dependency) {

    val colors = XAutodailyTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .drawBehind {
                drawRect(colors.colorBgContainer)
            }
            .padding(vertical = 20.dp, horizontal = 16.dp),
    ) {
        Text(
            text = dependency.name,
            maxLines = 1,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            color = { colors.colorText }
        )

        Text(
            text = dependency.licenses.joinToString(",") {
                it.name
            },
            Modifier.padding(top = 6.dp),
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            ),
            color = {
                colors.colorTextSecondary
            }
        )

    }
}