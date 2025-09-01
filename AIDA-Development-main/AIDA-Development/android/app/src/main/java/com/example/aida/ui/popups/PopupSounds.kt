package com.example.aida.ui.popups

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.aida.ui.constants.moveActionColor
import com.example.aida.ui.constants.specialActionColor

/**
 * Popup that displays a selection of sounds to pick. Called when opening the configuration
 * menu for a sound [RobotActionType].
 *
 * @param onDismiss Callback that is called upon closing the popup.
 * @param onSave Callback that is called upon saving the sound. Gets called with the name of
 * the sound as a string.
 */
@Composable
fun PopupSounds(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedSoundIndex by remember { mutableStateOf<Int?>(null) }
    var isPlayingSound by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        // Wraps the sound selection content.
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 8.dp,
            tonalElevation = 2.dp,
        ) {
            Box(
                modifier = Modifier
                    .background(moveActionColor)
                    .padding(20.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ){
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Select a Sound",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                    )

                    // Displays the available sounds as SoundButton items.
                    robotSounds.forEachIndexed { index, (name, sound) ->
                        SoundButton(
                            label = name,
                            isSelected = selectedSoundIndex == index,
                            onClick = {
                                if (!isPlayingSound) {
                                    // TODO: cancel sound on closing?
                                    val mediaPlayer = MediaPlayer.create(context, sound)
                                    mediaPlayer.start()

                                    isPlayingSound = true

                                    mediaPlayer.setOnCompletionListener {
                                        isPlayingSound = false
                                    }

                                    selectedSoundIndex = index
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Cancel button that closes the popup without saving.
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = popupCancelButtonColor
                            )
                        ) {
                            Text("Cancel")
                        }

                        // Save button that saves the selected sound and closes the popup, enabled only if a sound is selected.
                        Button(
                            modifier = Modifier,
                            colors = ButtonDefaults.buttonColors(popupSaveButtonColor),
                            onClick = {
                                selectedSoundIndex?.let {
                                    val selectedSound = robotSounds[it].first

                                    onSave(selectedSound)
                                    onDismiss()
                                }
                            },
                            enabled = selectedSoundIndex != null
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SoundButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Creates a button with a background color based on selection state.
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) specialActionColor else moveActionColor
        ),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(vertical = 4.dp)
    ) {
        Text(label)
    }
}