package com.example.aida.domain.model

/**
 * Enum class that contains all the different
 * Special Actions data that can be sent
 * to the server
 * @param type String representing the type of special actions data.
 */
enum class SpecialActionsData (type : String){
    THUMBS_UP("gesture"),
    THUMBS_DOWN("gesture"),
    POINT("gesture"),
    FINGER_GUN("gesture"),
    WAVING("gesture"),
    STOP("gesture"),
    EIGHT_BIT_LASER("sound"),
    BEEPING_ROBOT_MACHINE("sound"),
    ROBOT_POWER_OFF("sound"),
    MECHANICAL_CLAMP("sound"), // Rename sound file
    ROBOT_CALL("sound"),
    ROBOT_DRUM_LOOP_100BPM("sound") // Rename sound file
}