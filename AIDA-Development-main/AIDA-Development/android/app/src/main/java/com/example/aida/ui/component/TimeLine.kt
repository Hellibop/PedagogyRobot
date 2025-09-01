package com.example.aida.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Displays a vertical timeline line on the left side of the sequence bar.
 * Typically used as a marker for the start position.
 */
@Composable
fun TimeLine()
{
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(durationMillis = 500)),
        exit = fadeOut(tween(durationMillis = 500))
    ) {
        // Green vertical line acting as a timeline indicator.
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(160.dp)
                .background(Color(0x4D099D0B))
        )
    }
}