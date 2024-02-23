package me.teble.xposed.autodaily.ui.scene

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import me.teble.xposed.autodaily.R
import me.teble.xposed.autodaily.ui.composable.ImageItem
import me.teble.xposed.autodaily.ui.composable.RoundedSnackbar
import me.teble.xposed.autodaily.ui.composable.TopBar
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.layout.verticalScrollPadding


@Composable
fun DeveloperScene(navController: NavController, viewmodel: DeveloperViewModel = viewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }

    // 展示对应 snackbarText
    LaunchedEffect(Unit) {
        viewmodel.snackbarText.collect {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        topBar = {
            TopBar(text = "开发者", backClick = {
                navController.popBackStack()
            })
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                RoundedSnackbar(it)
            }
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
                .verticalScrollPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthorLayout(navController)
        }
    }
}


@Composable
private fun AuthorLayout(
    navController: NavController,
    viewmodel: DeveloperViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .clip(SmootherShape(12.dp))
            .background(color = Color(0xffffffff)),
    ) {
        AuthorItem(
            icon = painterResource(id = R.drawable.teble),
            contentDescription = "韵の祈",
            title = "韵の祈",
            info = "主要开发者"
        ) {
            viewmodel.openAuthorGithub(navController, "https://github.com/teble")
        }
        AuthorItem(
            icon = painterResource(id = R.drawable.lagrio),
            contentDescription = "MaiTungTM",
            title = "MaiTungTM",
            info = "UI & Icon 设计师"
        ) {
            viewmodel.openAuthorGithub(navController, "https://github.com/Lagrio")
        }
        AuthorItem(
            icon = painterResource(id = R.drawable.agoines),
            contentDescription = "agoines",
            title = "Agoines",
            info = "UI 开发者"
        ) {
            viewmodel.openAuthorGithub(navController, "https://github.com/agoines")
        }
    }
}

@Composable
private fun AuthorItem(
    icon: Painter,
    contentDescription: String,
    title: String,
    info: String,
    onClick: () -> Unit = {}
) {
    ImageItem(
        icon = icon,
        contentDescription = contentDescription,
        title = title,
        info = info,
        clickEnabled = true,
        shape = SmootherShape(12.dp),
        onClick = onClick

    )
}