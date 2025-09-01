package com.example.aida.ui.popups

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.aida.domain.model.RobotActionType
import com.example.aida.ui.constants.moveActionColor
import com.example.aida.ui.constants.specialActionColor


/**
 * Popup that is opened upon opening the configuration for a gesture action that can be found
 * in [RobotActionType].
 *
 * @param onDismiss Callback that is called upon closing popup.
 * @param onSave Callback that is called upon clicking save. Gets called with the name of
 * the selected gesture as a string.
 */
@Composable
fun PopupGestures(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var selectedGestureIndex by remember { mutableStateOf<Int?>(null) }
    val gestureList = robotGestures.toList()

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        // Wraps the gesture selection content.
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
                // Arranges the content vertically with centered alignment and padding.
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Displays the title "Select a Gesture" in a large font style.
                    Text(
                        text = "Select a Gesture",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                    )

                    // Displays the gesture items in a grid layout with 3 columns.
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .padding(16.dp)
                            .background(Color.White)
                    ) {
                        // Displays gestures as GestureButton with icon and name based on gestureList.
                        itemsIndexed(gestureList) { index, (_, gesture) ->
                            GestureButton(
                                item = gesture,
                                isSelected = index == selectedGestureIndex,
                                onClick = {
                                    selectedGestureIndex = index
                                }
                            )
                        }
                    }

                    // Container for cancel and save buttons.
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Cancel button that closes the popup without saving anything.
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = popupCancelButtonColor
                            )
                        ) {
                            Text("Cancel")
                        }

                        // Save button that saves the selected gesture and closes the popup, only enabled if a gesture is selected.
                        Button(
                            modifier = Modifier,
                            colors = ButtonDefaults.buttonColors(popupSaveButtonColor),
                            onClick = {
                                selectedGestureIndex?.let {
                                    val gesture = gestureList[it]
                                    onSave(gesture.first)

                                }
                                onDismiss()
                            },
                            enabled = selectedGestureIndex != null
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
fun GestureButton(
    item: Gesture,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) specialActionColor else moveActionColor,
        animationSpec = tween(durationMillis = 150),
        label = ""
    )

    // A container for the gesture button with a background color that changes based on selection state.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
    ) {
        // Arranges the icon and name vertically within the gesture button.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Displays the icon for the gesture.
            Icon(
                imageVector = item.icon,
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier
                    .size(70.dp)
                    .graphicsLayer(rotationZ = item.iconRotation)
            )
            // Displays the name of the gesture.
            Text(
                text = item.name,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}
