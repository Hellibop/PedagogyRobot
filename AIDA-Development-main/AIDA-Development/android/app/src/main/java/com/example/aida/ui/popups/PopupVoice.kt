package com.example.aida.ui.popups

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.aida.ui.constants.moveActionColor
import com.example.aida.ui.constants.specialActionColor
import java.util.Locale

/**
 * Popup for handling voice input, saving text, and playing text-to-speech.
 *
 * @param onDismiss Callback for dismissing the popup.
 * @param onSave Callback for saving the text input.
 * @param onPlay Callback for playing the voice with the text.
 * @param context Application context used for accessing resources and preferences.
 */
@Composable
fun PopupVoices(
    onDismiss: () -> Unit,
    onSave: (text: String) -> Unit,
    onPlay: (voice: String, text: String) -> Unit,
    context: Context
) {
    val sharedPreferences = remember { context.getSharedPreferences("PopupVoicesPrefs", Context.MODE_PRIVATE) }
    var textBoxContent by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var savedTexts by remember {
        mutableStateOf(sharedPreferences.getStringSet("savedTexts", emptySet())?.toList() ?: listOf())
    }
    var selectedText by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    // Speech recognition launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.data != null) {
            val res = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val transcribedText = res?.get(0).orEmpty()
            textBoxContent = transcribedText
        } else {
            Toast.makeText(context, "Failed to recognize speech", Toast.LENGTH_SHORT).show()
        }
    }

    // Dialog UI component with custom properties
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties( usePlatformDefaultWidth = false )
    ) {
        // Surface container for the dialog layout
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 8.dp,
            tonalElevation = 2.dp,
        ) {
            // Box container for layout alignment and background color
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.8f)
                    .background(moveActionColor)
                    .padding(20.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Row container for two main sections: sidebar and main content
                Row(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    // Sidebar for displaying saved texts
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        // Column for recent saved texts
                        Column(
                            modifier = Modifier
                                .width(220.dp)
                                .fillMaxHeight()
                                .background(Color.LightGray),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Title for saved texts section
                            Text(
                                text = "Recent Texts",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(top = 8.dp, bottom = 4.dp)
                            )

                            // Column for displaying saved text items
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(0.9f)
                                    .verticalScroll(scrollState)
                                    .drawVerticalScrollbar(scrollState, savedTexts.size)
                            ) {
                                // Loop through each saved text
                                savedTexts.forEach { text ->
                                    // Box for each saved text item
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                    ) {
                                        // Button for selecting saved text
                                        TextButton(
                                            onClick = {
                                                selectedText = text
                                                textBoxContent = text
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth(0.9f)
                                                .padding(vertical = 4.dp)
                                                .background(if (text == selectedText) specialActionColor else moveActionColor)
                                        ) {
                                            // Text display for each saved text
                                            Text(
                                                text = text,
                                                maxLines = 4,
                                                textAlign = TextAlign.Start,
                                                color = Color.White,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Delete selected button
                            Button(
                                onClick = {
                                    selectedText?.let {
                                        savedTexts = savedTexts - it
                                        sharedPreferences.edit()
                                            .putStringSet("savedTexts", savedTexts.toSet())
                                            .apply()
                                        selectedText = null
                                    }
                                },
                                enabled = selectedText != null,
                                colors = ButtonDefaults.buttonColors(containerColor = popupDeleteButtonColor),
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                            ) {
                                Text(text = "Delete Selected")
                            }
                        }
                    }

                    // Main content section for voice input and text input
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 10.dp, end = 10.dp, bottom = 8.dp, start = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Title for text input section
                            Text(
                                text = "What should AIDA say?",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier
                                    .padding(top = 8.dp, bottom = 18.dp)
                            )

                            // Column for text input and voice button
                            Column(
                                modifier = Modifier
                                    .weight(2f)
                                    .fillMaxSize()
                                    .padding(top = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ){
                                // Mic button for speech recognition
                                Box(
                                    modifier = Modifier
                                        .size(156.dp)
                                        .shadow(
                                            elevation = 20.dp,
                                            shape = CircleShape
                                        )
                                        .clip(CircleShape),
                                    contentAlignment = Alignment.Center
                                ){
                                    Button(
                                        onClick = {
                                            isListening = true
                                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
                                            }
                                            try {
                                                speechLauncher.launch(intent)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, e.message.orEmpty(), Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(Color.LightGray),
                                        modifier = Modifier
                                            .size(150.dp)
                                            .border(6.dp, popupPlayButtonColor, shape = CircleShape)
                                            .clip(CircleShape),
                                        contentPadding = PaddingValues(3.dp)
                                    ) {
                                        // Icon inside the button
                                        Icon(
                                            imageVector = Icons.Rounded.Mic,
                                            contentDescription = "Use voice input",
                                            tint = popupPlayButtonColor,
                                            modifier = Modifier
                                                .size(100.dp)
                                        )
                                    }
                                }

                                // Text input field and play button
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(top = 20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceEvenly
                                ){
                                    OutlinedTextField(
                                        value = textBoxContent,
                                        onValueChange = { textBoxContent = it },
                                        label = { Text("Enter text or use voice input") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight(),
                                        maxLines = 2,
                                    )
                                    Button(
                                        onClick = {
                                            onPlay("default_voice", textBoxContent)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = popupPlayButtonColor
                                        )
                                    ) {
                                        Text("Play")
                                    }
                                }
                            }

                            // Row for cancel and save buttons
                            Box(
                                modifier = Modifier
                                    .weight(0.5f),
                                contentAlignment = Alignment.BottomCenter
                            ){
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    // Cancel button
                                    Button(
                                        onClick = onDismiss,
                                        colors = ButtonDefaults.buttonColors(containerColor = popupCancelButtonColor)
                                    ) {
                                        Text("Cancel")
                                    }

                                    // Save button
                                    Button(
                                        onClick = {
                                            val updatedTexts = (savedTexts + textBoxContent).distinct() // Add new text and remove duplicates
                                            savedTexts = updatedTexts

                                            // Save updated texts to SharedPreferences
                                            sharedPreferences.edit()
                                                .putStringSet("savedTexts", updatedTexts.toSet())
                                                .apply()

                                            onSave(textBoxContent)
                                            onDismiss()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = popupSaveButtonColor)
                                    ) {
                                        Text("Save")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


// Custom scrollbar function
private fun Modifier.drawVerticalScrollbar(
    scrollState: ScrollState,
    itemCount: Int,
    width: Float = 10f,
    color: Color = Color.Gray
): Modifier {
    return this.then(
        Modifier.drawBehind {
            val proportion = scrollState.value / scrollState.maxValue.toFloat().coerceAtLeast(1f)

            // Shrink the scrollbar thumb based on number of items in the list
            val scrollbarHeight = if (itemCount >= 5) {
                (size.height / itemCount.toFloat()).coerceAtLeast(30f)
            } else {
                size.height
            }

            // Calculate the position of the scrollbar thumb
            val yOffset = proportion * (size.height - scrollbarHeight)

            // Draw the scrollbar thumb
            if(itemCount > 0){
                drawRoundRect(
                    color = color,
                    topLeft = Offset(size.width - width, yOffset),
                    size = Size(width, scrollbarHeight),
                    cornerRadius = CornerRadius(width / 2, width / 2)
                )
            }
        }
    )
}