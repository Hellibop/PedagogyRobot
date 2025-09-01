package com.example.aida.domain.model

/**
 * Data class for RobotAction
 * @param type Enum for the action type of the RobotAction.
 * @param data String containing the data to be send with special actions
 */
class RobotAction (val type: RobotActionType, var data: String)