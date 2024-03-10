package me.teble.xposed.autodaily.ui.composable


import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.text.TextStyle


@Composable
fun Text(
    text: () -> String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    color: ColorProducer? = null
) {
    Text(
        text = text(),
        modifier = modifier,
        style = style,
        minLines = minLines,
        maxLines = maxLines,
        color = color
    )
}

/**
 * 为了使用 ColorProducer，封装 BasicText
 */
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    color: ColorProducer? = null
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = style,
        minLines = minLines,
        maxLines = maxLines,
        color = color
    )
}

