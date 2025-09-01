package com.example.aida.ui.popups

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FrontHand
import androidx.compose.material.icons.outlined.PanToolAlt
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.outlined.WavingHand
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.aida.R

// Colors for specific buttons in the UI
val popupCancelButtonColor = Color(0xFFD9A600)
val popupSaveButtonColor = Color(0xFF5C8F1F)
val popupDeleteButtonColor = Color(0xFFB30000)
val popupPlayButtonColor = Color(0xFF2196F3)

/**
 * Represents a gesture action, including its identifier, name, and associated icon.
 *
 * @param id A unique identifier for the gesture.
 * @param name The name of the gesture.
 * @param icon The icon associated with the gesture.
 * @param iconRotation The rotation angle (in degrees) for the icon, if applicable. Default is 0.
 */
data class Gesture(
    val id: Int,
    val name: String,
    val icon: ImageVector,
    val iconRotation: Float = 0f // Default value for gestures without icon rotation
)

/**
 * Predefined gestures available in the library.
 * Each gesture includes a unique ID, name, and an associated icon.
 */
val robotGestures: HashMap<String, Gesture> = hashMapOf(
    "Thumbs up" to Gesture(id = 1, name = "Thumbs up", icon = Icons.Outlined.ThumbUp),
    "Thumbs down" to Gesture(id = 2, name = "Thumbs down", icon = Icons.Outlined.ThumbDown),
    "Point" to Gesture(id = 3, name = "Point", icon = Icons.Outlined.PanToolAlt),
    "Finger gun" to Gesture(id = 4, name = "Finger gun", icon = Icons.Outlined.PanToolAlt, iconRotation = 90f),
    "Waving" to Gesture(id = 5, name = "Waving", icon = Icons.Outlined.WavingHand),
    "Stop" to Gesture(id = 6, name = "Stop", icon = Icons.Outlined.FrontHand)
)

/**
 * Predefined list of robot sound effects
 */
val robotSounds = listOf(
    "Eight bit laser" to R.raw.eight_bit_laser,
    "Beeping robot machine" to R.raw.beeping_robot_or_machine,
    "Robot power-off" to R.raw.robot_power_off,
    "Mechanical clamp" to R.raw.mechanicalclamp,
    "Robot call" to R.raw.robot_call,
    "Robot drum" to R.raw.robot_drum_loop_100bpm
)

