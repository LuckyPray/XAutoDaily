package me.teble.xposed.autodaily.ui.scene

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.hook.base.hostVersionName
import me.teble.xposed.autodaily.ui.composable.TopBar
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.XAutoDaily
import me.teble.xposed.autodaily.ui.icon.icons.XAutoDailyRound

@Composable
fun AboutScene(navController: NavController) {
    Scaffold(
        topBar = {
            TopBar(text = "关于", backClick = {
                navController.popBackStack()
            })
        },
        backgroundColor = Color(0xFFF7F7F7)
    ) { contentPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
                .clip(SmootherShape(12.dp))
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BuildConfigLayout()
        }
    }
}

@Composable
fun BuildConfigLayout() {
    var moduleVersionName by remember { mutableStateOf("") }
    var moduleVersionCode by remember { mutableIntStateOf(0) }
    var qqVersionName by remember { mutableStateOf("") }

    LaunchedEffect(qqVersionName) {
        moduleVersionName = BuildConfig.VERSION_NAME
        moduleVersionCode = BuildConfig.VERSION_CODE
        qqVersionName = hostVersionName
    }
    Image(
        Icons.XAutoDailyRound,
        contentDescription = "XAutoDaily Round Icon",
        Modifier
            .padding(top = 36.dp)
            .size(80.dp)

    )

    Icon(
        tint = Color(0xFF202124),
        imageVector = Icons.XAutoDaily,
        contentDescription = "text logo",
        modifier = Modifier.padding(top = 16.dp)
    )

    Text(
        text = "v${moduleVersionName} (${moduleVersionCode}) ",
        modifier = Modifier.padding(top = 8.dp),
        style = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF5F6368),
            textAlign = TextAlign.Center,
        )
    )
}