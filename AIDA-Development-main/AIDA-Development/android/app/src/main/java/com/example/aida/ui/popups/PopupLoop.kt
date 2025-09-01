package com.example.aida.ui.popups


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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.chargemap.compose.numberpicker.NumberPicker
import com.example.aida.ui.constants.loopIterationRange
import com.example.aida.ui.constants.moveActionColor

/**
 * Popup that is opened upon opening the configuration for a loop action.
 *
 * @param onDismiss Callback that is called upon closing popup.
 * @param onSave Callback that is called upon clicking save. Gets called with the
 * number of selected iterations as argument.
 */
@Composable
fun PopupLoop(
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var pickerValue by remember { mutableIntStateOf(1) }

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        // Wraps the loop selection content.
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 8.dp, // Adds shadow
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
                        text = "Select a loop interval",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                    )

                    // Displays the number picker for selecting a loop interval.
                    NumberPicker(
                        value = pickerValue,
                        range = loopIterationRange,
                        onValueChange = {
                            pickerValue = it
                        },
                        textStyle = TextStyle(fontSize = 24.sp),
                    )

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

                        // Save button that saves the selected loop interval and closes the popup.
                        Button(
                            modifier = Modifier,
                            colors = ButtonDefaults.buttonColors(popupSaveButtonColor),
                            onClick = {
                                pickerValue.let {
                                    onSave(it)
                                }
                                onDismiss()
                            }
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}
