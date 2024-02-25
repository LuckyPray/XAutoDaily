package me.teble.xposed.autodaily.ui.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
fun HintEditText(
    value: String,
    modifier: Modifier,
    textStyle: TextStyle,
    onValueChange: (String) -> Unit,
    hintText: String,
    hintTextStyle: TextStyle
) {


    BasicTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        singleLine = true,
        decorationBox = { innerTextField ->
            Box {
                AnimatedVisibility(
                    value.isEmpty(),
                    enter = slideInHorizontally() + fadeIn(),
                    exit = slideOutHorizontally() + fadeOut(),
                ) {
                    Text(
                        text = hintText,
                        style = hintTextStyle
                    )
                }

                innerTextField()
            }

        }
    )

}