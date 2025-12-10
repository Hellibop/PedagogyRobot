package com.example.aida.domain.model

/**
 * Enum class that contains all the different
 * Condition types that can be sent and handled
 * on the robot(AIDA)
 * @param id Identifier to be sent to the robot
 * @isSpecial Boolean to se if condition takes extra argument data
 */
enum class IfStatementConditionsType (val id: Short, val isSpecial: Boolean, ) {
    TRUE(1, false),
    FALSE(2, false),
    CAMERA_CONDITION_WITH_ARG(3, true)
}