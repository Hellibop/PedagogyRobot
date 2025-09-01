package com.example.aida.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.aida.ui.constants.defaultPlayButtonColor
import com.example.aida.ui.constants.defaultStepButtonColor
import com.example.aida.ui.constants.defaultStopButtonColor
import com.example.aida.ui.constants.inactiveUserButtonColor
import com.example.aida.ui.constants.playButtonIcon
import com.example.aida.ui.constants.stepButtonIcon
import com.example.aida.ui.constants.stopButtonIcon
import com.example.aida.ui.viewmodel.SequenceBarState
import com.example.aida.ui.viewmodel.UserInteractionState


/**
 * Displays the user control buttons (Stop, Step, Play/Pause).
 *
 * The logic for stepping between differnet actions and sending them to the robot is handled
 * in the [SequenceBar] functions.
 *
 * @param onClickPlay A callback that decides what should happen when clicking play.
 * @param onClickStop A callback that decides what should happen when clicking stop.
 * @param onClickStep A callback that decides what should happen when clicking step.
 * @param uiState The [SequenceBarState] that contains the UI state of the sequence bar.
 */
@Composable
fun UserButtons(
    onClickStop: () -> Unit,
    onClickPlay: () -> Unit,
    onClickStep: () -> Unit,
    uiState: SequenceBarState
) {
    val stepButtonColor = if (uiState.menuState == UserInteractionState.PLAYING) inactiveUserButtonColor else defaultStepButtonColor
    val playButtonColor = if (uiState.menuState == UserInteractionState.PLAYING) inactiveUserButtonColor else defaultPlayButtonColor

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(200.dp)
            .padding(15.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray)
            ) {
                // STOP BUTTON
                IconButton(
                    onClick = onClickStop,
                    modifier = Modifier.size(70.dp)
                ) {
                    Icon(
                        imageVector = stopButtonIcon,
                        contentDescription = "Stop",
                        tint = defaultStopButtonColor,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }

                // !!! WARNING BELOW THIS LINE !!!
                //
                // PLAY/PAUSE BUTTON
                //
                // FOR THE GROUP CONTINUING WORK ON THIS PROJECT
                //
                // THE PLAY / PAUSE FUNCTIONALITY IS ONLY A PSEUDO - VISUAL REPRESENTATION OF HOW THE APP IS THOUGHT TO WORK
                //
                // PLEASE REMAKE THE PLAY / PAUSE AND STEP FUNCTIONALITY ACCORDING TO YOUR NEEDS!
                //
                // our groups thoughts are that when an instruction is sent to RoS,
                // the scrolling stops until a sort of acknowledgement is received from RoS that the instruction is executed,
                // at which point the scroll can continue!
                //
                // !!!                          !!!
                IconButton(
                    onClick = {
                        if (uiState.menuState == UserInteractionState.STOPPED) {
                            // If we are currently stopped, play the sequence as normal
                            onClickPlay()
                        }
                    },
                    modifier = Modifier.size(70.dp)
                ) {
                    Icon(
                        imageVector = playButtonIcon,
                        contentDescription = if (uiState.menuState == UserInteractionState.PLAYING) "Play" else "Pause",
                        tint = playButtonColor,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // STEP BUTTON
                IconButton(
                    onClick = onClickStep,
                    modifier = Modifier.size(70.dp)
                ) {
                    Icon(
                        imageVector = stepButtonIcon,
                        contentDescription = "Not started",
                        tint = stepButtonColor,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}