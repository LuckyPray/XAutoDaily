package me.teble.xposed.autodaily.ui.composable


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.teble.xposed.autodaily.ui.graphics.SmootherShape
import me.teble.xposed.autodaily.ui.theme.DefaultAlpha
import me.teble.xposed.autodaily.ui.theme.DisabledFontAlpha
import java.util.Locale


@Composable
fun DatePicker(
    modifier: Modifier = Modifier,
) {


    Row(
        modifier = Modifier.height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {

        TextPicker(
            count = 24, modifier = Modifier
                .padding(start = 60.dp, top = 36.dp, bottom = 36.dp, end = 8.dp)
        )

        Text(
            text = "时", modifier = Modifier.padding(end = 18.dp),
            color = Color(0xFF4F5355),
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
            )
        )
        Divider(
            color = Color(0xFFE6E6E6),
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 66.dp)
                .width(2.dp)
                .clip(SmootherShape(1.dp)),
        )
        TextPicker(
            count = 60, modifier = Modifier
                .padding(start = 48.dp, top = 36.dp, bottom = 36.dp, end = 8.dp)
        )
        Text(
            text = "分",
            modifier = Modifier
                .padding(end = 36.dp),
            color = Color(0xFF4F5355),
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            ),
        )
    }


}

@Composable
internal fun TextPicker(
    modifier: Modifier = Modifier,
    startIndex: Int = 0,
    count: Int,
    itemHeight: Dp = 77.dp,
    visibleItemCount: Int = 3,
    onScrollFinish: (snappedIndex: Int) -> Int? = { null },
) {

    val state = rememberLazyListState(startIndex)
    val isScrollInProgress = state.isScrollInProgress

    val snappingLayout = remember(state) { SnapLayoutInfoProvider(state) }
    val flingBehavior = rememberSnapFlingBehavior(snappingLayout)


    val currentIndex by remember { derivedStateOf(state::firstVisibleItemIndex) }

    val currentOnScrollFinish by rememberUpdatedState(onScrollFinish)

    val itemHalfHeightToPx = with(LocalDensity.current) { itemHeight.toPx() / 2 }

    LaunchedEffect(state.isScrollInProgress) {
        if (!state.isScrollInProgress && state.firstVisibleItemScrollOffset != 0) {
            if (state.firstVisibleItemScrollOffset < itemHalfHeightToPx) {
                state.animateScrollToItem(state.firstVisibleItemIndex)
            } else if (state.firstVisibleItemScrollOffset > itemHalfHeightToPx) {
                state.animateScrollToItem(state.firstVisibleItemIndex + 1)

            }
        }
    }

    LazyColumn(
        modifier = modifier
            .height(itemHeight * visibleItemCount)
            .width(64.dp),
        state = state,
        horizontalAlignment = Alignment.CenterHorizontally,
        flingBehavior = flingBehavior,
        contentPadding = PaddingValues(vertical = itemHeight * (visibleItemCount / 2)),
    ) {
        items(
            count,
            key = { it },
            contentType = { derivedStateOf { currentIndex == it }.value }) { index ->

            val isSelect by remember { derivedStateOf { currentIndex == index } }

            val fontWeight by animateIntAsState(
                targetValue = if (isSelect) FontWeight.Normal.weight else FontWeight.Light.weight,
                animationSpec = spring(), label = "Picker FontWeight"
            )
            val fontScale by animateFloatAsState(
                targetValue = if (isSelect) 1.0f else 0.8f,
                animationSpec = spring(), label = "Picker fontSize"
            )

            val alpha by animateFloatAsState(
                targetValue = if (isSelect) DefaultAlpha else DisabledFontAlpha,
                animationSpec = spring(), label = "Picker fontAlpha"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .graphicsLayer {
                        this.alpha = alpha
                    },
                contentAlignment = Alignment.Center
            ) {
                AutoSizeText(
                    text = String.format(Locale.getDefault(), "%02d", index),
                    color = Color(0xFF202124),
                    maxLines = 1,
                    modifier = Modifier.graphicsLayer {
                        this.scaleX = fontScale
                        this.scaleY = fontScale

                    },
                    maxTextSize = 48.sp,
                    stepGranularityTextSize = 1.sp,
                    fontWeight = FontWeight(fontWeight),

                    style = TextStyle(
                        textAlign = TextAlign.Center
                    ),

                    )
            }


        }

    }
}

@Preview
@Composable
fun PreviewSignLayout() {
    Box(modifier = Modifier.background(Color(0xFFFFFFFF))) {
        DatePicker(modifier = Modifier.width(328.dp))
    }

}





