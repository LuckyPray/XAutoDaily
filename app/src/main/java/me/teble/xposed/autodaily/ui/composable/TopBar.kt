package me.teble.xposed.autodaily.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.teble.xposed.autodaily.ui.icon.Icons
import me.teble.xposed.autodaily.ui.icon.icons.Back
import me.teble.xposed.autodaily.ui.icon.icons.Notice

@Composable
fun TopBar(
    text : String,
    modifier: Modifier = Modifier,
    hasBack: Boolean = false,
    backClick: () ->Unit = {},
    endIcon: ImageVector? = null,
    endIconClick: () ->Unit = {}
) {
    Row(
        modifier = modifier
            .statusBarsPadding()
            .padding(bottom = 20.dp, top = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if(hasBack){
            Icon(
                imageVector = Icons.Back,
                modifier = Modifier
                    .padding(start = 26.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = backClick)
                    .padding(6.dp),
                contentDescription = "",
                tint = Color(0xFF202124)
            )
        }
        Spacer(modifier = Modifier.width(if(hasBack) 16.dp else 32.dp))
        Text(
            text = text,
            style = TextStyle(
                color = Color(0xFF202124),
                fontWeight = FontWeight(700),
                fontSize = 20.sp
            )
        )
        Spacer(modifier = Modifier.weight(1f))

        endIcon?.let {
            Icon(
                imageVector = it,
                modifier = Modifier
                    .padding(end = 26.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = endIconClick)
                    .padding(6.dp),
                contentDescription = "",
                tint = Color(0xFF202124)
            )
        }

    }

}