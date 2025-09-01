package com.example.aida.ui.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


/**
 * Displays a horizontal progress bar (scrollbar) indicating the user's scroll position relative to the total width of the sequence.
 *
 * @param scrollState The state representing the current scroll position.
 * @param totalWidth The total width in dp of the scrollable area.
 * @param actionCount The number of actions in the SequenceBar.
 * @param modifier Optional [Modifier] for layout or styling.
 * @param onDragStopped A callback that decides what should happen when dragging has stopped.
 *      Used to implement snapping to closest action.
 */
@Composable
fun ProgressBar(
    scrollState: ScrollState,
    totalWidth: Dp,
    actionCount: Int,
    modifier: Modifier = Modifier,
    onDragStopped: () -> Unit
) {
    // scope for the coroutine
    val scope = rememberCoroutineScope()

    // Density of the device
    val density = LocalDensity.current

    // Progressbar container
    Box(
        modifier
            .width(totalWidth)
            .height(32.dp)
            .background(color = Color.LightGray, shape = RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        val thumbSize = 1f / actionCount.coerceAtLeast(1)
        val thumbPosition = scrollState.value / (scrollState.maxValue.toFloat())

        val thumbWidth = totalWidth * thumbSize
        val thumbPositionX = (totalWidth * thumbPosition).coerceIn(0.dp, totalWidth - thumbWidth)

        // Draggable thumb
        Box(
            Modifier
                .offset( x = thumbPositionX)
                .width(thumbWidth)
                .fillMaxHeight()
                .background(color = Color(0xBF3A88D1), shape = RoundedCornerShape(16.dp))
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        val deltaDp = with(density) { delta.toDp() }
                        val scrollDelta = (deltaDp / totalWidth) * scrollState.maxValue

                        scope.launch {
                            scrollState.scrollBy(scrollDelta)
                        }
                    },
                    onDragStopped = { _ ->
                        onDragStopped()
                    }
                )
        )
    }
}
