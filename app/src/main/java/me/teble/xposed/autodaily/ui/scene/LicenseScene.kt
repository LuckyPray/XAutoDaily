package me.teble.xposed.autodaily.ui.scene

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import me.teble.xposed.autodaily.ui.composable.Text
import me.teble.xposed.autodaily.ui.composable.XaScaffold
import me.teble.xposed.autodaily.ui.data.Dependency
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.layout.defaultNavigationBarPadding
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme
import me.teble.xposed.autodaily.utils.getTextFromModuleAssets


@OptIn(ExperimentalSerializationApi::class)
@Composable
fun LicenseScene(
    backClick: () -> Unit,
) {

    val dependencyList = remember {
        mutableStateListOf(
            *Json.decodeFromString<List<Dependency>>(
                getTextFromModuleAssets("licenses.json")
            ).toTypedArray()
        )
    }
    XaScaffold(
        text = "开放源代码许可",
        backClick = backClick,
        containerColor = XAutodailyTheme.colors.colorBgLayout
    ) {

        Column(
            modifier = Modifier
                .weight(1f, false)
                .padding(top = 16.dp)
                .clip(SmootherShape(12.dp))
                .verticalScroll(rememberScrollState())
                .defaultNavigationBarPadding(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            dependencyList.forEach { dependency ->
                key(dependency.name) {
                    DependencyItem(dependency)
                }

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