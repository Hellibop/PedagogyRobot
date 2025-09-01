package com.example.aida.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.aida.domain.model.RobotAction
import com.example.aida.domain.model.RobotActionType
import com.example.aida.ui.constants.inactiveActionColor
import com.example.aida.ui.constants.moveActionColor
import com.example.aida.ui.popups.robotGestures
import com.example.aida.ui.constants.specialActionColor

/**
 *  Represents an action that is placed in the sequence bar.
 *  @property action Contains the data to be sent to the robot.
 *  @property durationRemaining The duration for which the action should be played,
 *      displayed in the UI.
 *  @property iterations The number of iterations the action should be played,
 *      Only used if this is a loop.
 */
class UIAction(
    type: RobotActionType
) {
    val action: RobotAction = RobotAction(type, "")
    var durationRemaining: Double = action.type.duration
    var iterations: Int = 1 // TODO: figure out a way to not store this in every action

    /**
     *  Makes a deep copy of an action. Necessary to force redrawing the interface when
     *  changing action data.
     */
    fun clone(): UIAction {
        val newAction = UIAction(action.type)
        newAction.durationRemaining = durationRemaining
        newAction.iterations = iterations
        newAction.action.data = action.data

        return newAction
    }
}

/**
 *  Represents the appearance of an action in the library and sequence bar.
 *  Returned when retrieving an action's appearance based on it's type.
 *  @property text The text that should be displayed in the action.
 *  @property icon The icon that should be displayed in the action.
 */
data class ActionUIData(
    val text: String,
    val icon: ImageVector,
)

val actionUIDataMap: HashMap<RobotActionType, ActionUIData> = hashMapOf(
    RobotActionType.FORWARDS to ActionUIData("FORWARD", Icons.Outlined.ArrowUpward),
    RobotActionType.BACKWARDS to ActionUIData("BACKWARD", Icons.Outlined.ArrowDownward),
    RobotActionType.TURN_LEFT to ActionUIData("TURN LEFT", Icons.Outlined.ArrowBack),
    RobotActionType.TURN_RIGHT to ActionUIData("TURN RIGHT", Icons.Outlined.ArrowForward),
    RobotActionType.FORWARDS_LONG to ActionUIData("FORWARDS LONG", Icons.Outlined.North),
    RobotActionType.BACKWARDS_LONG to ActionUIData("BACKWARDS LONG", Icons.Outlined.South),
    RobotActionType.TURN_LEFT_LONG to ActionUIData("TURN LEFT LONG", Icons.Outlined.West),
    RobotActionType.TURN_RIGHT_LONG to ActionUIData("TURN RIGHT LONG", Icons.Outlined.East),
    RobotActionType.LOOP_START to ActionUIData("LOOP START", Icons.Outlined.Repeat),
    RobotActionType.LOOP_END to ActionUIData("LOOP END", Icons.Outlined.Repeat),
    RobotActionType.INPUT_GESTURE to ActionUIData("INPUT GESTURE", Icons.Outlined.WavingHand),
    RobotActionType.INPUT_VOICE to ActionUIData("INPUT VOICE", Icons.Outlined.Campaign),
    RobotActionType.INPUT_SOUND to ActionUIData("INPUT SOUND", Icons.Outlined.MusicNote),
)

/**
 *  Retrieves the appearance of an action based on it's type.
 *
 *  This overload is needed when the action's data field is significant, for example
 *  for special actions.
 *
 *  @param action The [RobotAction] for which to get the appearance.
 *  @return An [ActionUIData] instance that describes the action's appearance.
 */
fun GetActionAppearance(
    action: RobotAction
): ActionUIData {
    assert(actionUIDataMap.containsKey(action.type))

    if (action.type == RobotActionType.INPUT_GESTURE && !action.data.isEmpty()) {
        val icon = robotGestures.getValue(action.data).icon

        return ActionUIData(action.data, icon)
    }

    return actionUIDataMap.getValue(action.type)
}

/**
 *  Retrieves the appearance of an action based on it's type.
 *
 *  This overload is used when an action's data isn't significant, i.e for normal actions,
 *  and their appearance can be derived from their type alone.
 *
 *  @param type The [RobotActionType] for which to get the appearance.
 *  @return An [ActionUIData] instance that describes the action's appearance.
 */
fun GetActionAppearance(
    type: RobotActionType
): ActionUIData {
    assert(actionUIDataMap.containsKey(type))
    return actionUIDataMap.getValue(type)
}

/**
 *  Retrieves the color of an action based on it's type.
 *
 *  This overload is needed when the action's data field is significant, for example
 *  for special actions.
 *
 *  @param action The [RobotAction] for which to get the color.
 *  @return The [Color] the action should be rendered as.
 */
fun GetActionColor(
    action: RobotAction
): Color {
    if (!action.type.isSpecial) {
        return moveActionColor
    }

    if (action.data.isEmpty() &&
        !(action.type == RobotActionType.LOOP_START || action.type == RobotActionType.LOOP_END)) {
        return inactiveActionColor
    }

    return specialActionColor
}

/**
 *  Retrieves the color of an action based on it's type.
 *
 *  This overload is used when an action's data isn't significant, i.e for normal actions,
 *  and their appearance can be derived from their type alone.
 *
 *  @param type The [RobotActionType] for which to get the color.
 *  @return The [Color] the action should be rendered as.
 */
fun GetActionColor(
    action: RobotActionType
): Color {
    if (!action.isSpecial) {
        return moveActionColor
    }
    return specialActionColor

}