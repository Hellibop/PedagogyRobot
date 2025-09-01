package com.example.aida.ui.constants

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotStarted
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.StopCircle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Defines a set of colors used for action components.
 */
val moveActionColor = Color(0xff6c779c)    // Color for move actions
val specialActionColor = Color(0xff435159) // Color for special actions
val activeActionColor = Color(0xFF5B7A8C)        // Color indicating an active state
val inactiveActionColor = Color(0xff8A8A8A)      // Color indicating an inactive state

/**
 * The dimensions of the actions.
 */
val sequenceBarActionHeight = 150.dp
val sequenceBarActionWidth  = 160.dp
val actionLibraryButtonWidth = sequenceBarActionWidth
val actionLibraryButtonHeight = sequenceBarActionHeight

/**
 * The padding between each action when placed in the sequence bar.
 */
val sequenceBarActionPadding = 12.dp

/**
 * The total distance between each action in the sequence bar.
 */
val sequenceBarActionDistance = sequenceBarActionWidth + sequenceBarActionPadding

/**
 * The percentage of the action that has to be scrolled past before snapping to next action.
 * For example, if set to 0.6, 60% of the action has to be scrolled past in order for the scroll
 * to snap to the next action. Otherwise, it will snap back to the left border of the centered action.
 */
val sequenceBarActionSnapThreshold = 0.5f //

/**
 * The pixel independent width of the progress bar.
 */
val progressBarWidth = 800.dp

/**
 * The colors of the play, stop and step buttons.
 */
val defaultStopButtonColor = Color(0xFF8C241A)
val defaultStepButtonColor = Color(0xFF6DA925)
val defaultPlayButtonColor = Color(0xFFFFC300)
val defaultPauseButtonColor = Color.Blue
val inactiveUserButtonColor = Color.LightGray


/**
 * The icons of the play, stop and step buttons.
 */
val pauseButtonIcon = Icons.Outlined.PauseCircle
val playButtonIcon = Icons.Outlined.PlayCircle
val stopButtonIcon = Icons.Outlined.StopCircle
val stepButtonIcon = Icons.Outlined.NotStarted


/**
 * The color of the lock button.
 */
val lockButtonColor = Color(0xFF08A4FF)


/**
 * The colors of the sequence bar background when in it's various interaction states.
 */
val sequenceTabIdleColor = Color(0xFFFFFFFF)
val sequenceTabDraggingColor = Color(0xFF808080)
val sequenceTabPlayingColor = Color(0xCCADADAD)

/**
 * The size of the text in the actions.
 */
val actionFontsize = 15.sp

/**
 * The upper limit of characters that can be displayed in an action before it's text
 * will be truncated.
 */
val specialActionTextLimit = 30


/**
 * The range of allowed iteration values for loop buttons.
 */
val loopIterationRange = 1..10