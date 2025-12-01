package com.example.aida.ui.popups

import androidx.compose.animation.core.copy
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlin.collections.forEachIndexed
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.aida.ui.constants.moveActionColor
import com.example.aida.ui.constants.specialActionColor

// Define your options here.
// You could also pass these as a parameter if they need to be dynamic.
val ifOptions = kotlin.collections.listOf("Option 1", "Option 2", "Option 3")

/**
 * Popup that is opened upon opening the configuration for an IF block.
 *
 * @param onDismiss Callback that is called upon closing popup.
 * @param onSave Callback that is called upon clicking save. Gets called with the selected option string.
 */
@androidx.compose.runtime.Composable
fun PopupIf(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    // State to track which option is selected (0, 1, or 2)
    var selectedOptionIndex by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<Int?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        androidx.compose.material3.Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 8.dp,
            tonalElevation = 2.dp,
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .background(moveActionColor)
                    .padding(20.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    androidx.compose.material3.Text(
                        text = "Select Condition",
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                    )

                    // Display the 3 options vertically
                    androidx.compose.foundation.layout.Column(
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ifOptions.forEachIndexed { index, optionText ->
                            IfOptionButton(
                                text = optionText,
                                isSelected = index == selectedOptionIndex,
                                onClick = { selectedOptionIndex = index }
                            )
                        }
                    }

                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))

                    // Action Buttons (Cancel / Save)
                    androidx.compose.foundation.layout.Row(
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        androidx.compose.material3.Button(
                            onClick = onDismiss,
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = popupCancelButtonColor
                            )
                        ) {
                            androidx.compose.material3.Text("Cancel")
                        }

                        androidx.compose.material3.Button(
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(popupSaveButtonColor),
                            onClick = {
                                selectedOptionIndex?.let { index ->
                                    onSave(ifOptions[index])
                                }
                                onDismiss()
                            },
                            enabled = selectedOptionIndex != null
                        ) {
                            androidx.compose.material3.Text("Save")
                        }
                    }
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
fun IfOptionButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Animate color change on selection
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) specialActionColor else Color.LightGray.copy(alpha = 0.3f),
        animationSpec = tween(durationMillis = 150),
        label = "ColorAnimation"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.Black,
        label = "TextColorAnimation"
    )

    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = text,
            color = textColor,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
    }
}
