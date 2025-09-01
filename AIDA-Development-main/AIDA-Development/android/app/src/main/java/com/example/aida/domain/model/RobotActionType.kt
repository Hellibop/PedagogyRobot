package com.example.aida.domain.model

/**
 * Enum class that contains all the different
 * Special Actions data that can be sent
 * to the server
 * @param id Identifier to be sent to the robot, via the RobotAPI
 * @param isSpecial Boolean to see if an actions is special or not.
 * @param duration Time in double that it takes to execute the action.
 */
enum class RobotActionType(val id: Short, val isSpecial: Boolean, val duration: Double){
    FORWARDS(2, false, 1.0),
    BACKWARDS(9, false, 1.0),
    TURN_LEFT(1, false, 1.0),
    TURN_RIGHT(4, false, 1.0),
    FORWARDS_LONG(3, false, 2.0),
    BACKWARDS_LONG(10, false, 2.0),
    TURN_LEFT_LONG(8, false, 2.0),
    TURN_RIGHT_LONG(11, false, 2.0),
    LOOP_START(12, true, 0.0),
    LOOP_END(13, true, 0.0),
    INPUT_GESTURE(6, true, 1.0),
    INPUT_VOICE(7, true, 1.0),
    INPUT_SOUND(14, true, 1.0);

    companion object {
        fun fromId(id: Short): RobotActionType = entries.find { it.id == id }?: FORWARDS
    }
}