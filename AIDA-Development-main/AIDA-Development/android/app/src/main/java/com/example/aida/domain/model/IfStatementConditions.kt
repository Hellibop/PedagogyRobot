package com.example.aida.domain.model

/**
 * Data class for IfStatementConditions
 * ( Right now only type is used and conditionArg is empty )
 * @param type Enum for the condition type of the IfStatement.
 * @param conditionArg extra argument for the condition converted to a string for serialization, expected to be able to be converted to correct type for condition implementation on robot.
 */
class IfStatementConditions (val type: IfStatementConditionsType, var conditionArg: String)