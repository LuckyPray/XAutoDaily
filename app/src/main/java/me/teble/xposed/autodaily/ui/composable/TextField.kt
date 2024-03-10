package me.teble.xposed.autodaily.ui.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.Edit
import me.teble.xposed.autodaily.ui.theme.DisabledAlpha
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors

@Composable
fun HintEditText(
    value: () -> String,
    modifier: Modifier,
    textStyle: TextStyle,
    singleLine: Boolean = true,
    hintText: String,
    hintTextStyle: TextStyle,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        modifier = modifier,
        value = value(),
        onValueChange = onValueChange,
        textStyle = textStyle,
        singleLine = singleLine,
        cursorBrush = SolidColor(colors.themeColor),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.CenterStart
            ) {
                AnimatedVisibility(
                    value().isEmpty(),
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

@Composable
fun IconEditText(
    value: () -> String,
    modifier: Modifier,
    hintText: String,
    iconClick: () -> Unit,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HintEditText(
            modifier = Modifier
                .padding(vertical = 18.dp, horizontal = 16.dp)
                .weight(1f),
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.colorText
            ),

            singleLine = false,

            hintText = hintText,
            hintTextStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colors.colorText.copy(alpha = DisabledAlpha)
            ),
        )

        VerticalDivider(
            color = colors.colorSelection,
            thickness = 2.dp,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .clip(SmootherShape(1.dp)),
        )

        Icon(
            imageVector = Icons.Edit,
            modifier = Modifier
                .padding(8.dp)
                .clip(CircleShape)
                .clickable(role = Role.Button) {
                    iconClick()
                }
                .padding(8.dp)
                .size(24.dp),
            contentDescription = "", tint = colors.themeColor
        )
    }

}