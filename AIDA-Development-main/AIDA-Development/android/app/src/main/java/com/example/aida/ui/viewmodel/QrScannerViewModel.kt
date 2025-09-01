// QrScannerScreen.kt
package com.example.aida.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.aida.domain.model.RobotActionType
import com.example.aida.domain.repository.SequenceRepository
import com.example.aida.ui.component.UIAction
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for the QR Code Scanner screen.
 * This ViewModel is responsible for parsing QR code data into a sequence of robot actions
 * and updating the [SequenceRepository] with these actions.
 *
 * @param sequenceRepository Repository for managing and storing the sequence of robot actions.
 */
@HiltViewModel
class QrScannerViewModel @Inject constructor(
    private val sequenceRepository: SequenceRepository
) : ViewModel() {

    /**
     * Parses a string obtained from a QR code into a list of [UIAction] objects.
     * The QR code string is expected to be a comma-separated list of action codes.
     * Each code maps to a specific [RobotActionType].
     * Loop start actions are expected in the format "lsp-N" where N is the number of iterations.
     *
     * @param code The raw string data scanned from the QR code.
     * @return A list of [UIAction] objects derived from the QR code data.
     *         Returns an empty list if the code is empty or contains no valid actions.
     */
    private fun parseQR(code: String): List<UIAction> {

        // Split the input code string by commas to get individual action codes.
        val splitted  = code.split(",")
        val res: MutableList<UIAction> = mutableListOf<UIAction>()

        for (ac in splitted) {
            when (ac) {

                // Mapping for standard, non-loop actions.
                "lfwd" -> res.add(UIAction(RobotActionType.FORWARDS_LONG))
                "lbwd" -> res.add(UIAction(RobotActionType.BACKWARDS_LONG))
                "fwd" -> res.add(UIAction(RobotActionType.FORWARDS))
                "bwd" -> res.add(UIAction(RobotActionType.BACKWARDS))
                "lef" -> res.add(UIAction(RobotActionType.TURN_LEFT))
                "llef" -> res.add(UIAction(RobotActionType.TURN_LEFT_LONG))
                "rig" -> res.add(UIAction(RobotActionType.TURN_RIGHT))
                "lrig" -> res.add(UIAction(RobotActionType.TURN_RIGHT_LONG))

                // Mapping for empty special actions
                "ipg" -> res.add(UIAction(RobotActionType.INPUT_GESTURE))
                "ipv" -> res.add(UIAction(RobotActionType.INPUT_VOICE))
                "imu" -> res.add(UIAction(RobotActionType.INPUT_SOUND))

                // Mapping for specific gestures
                "gstu" -> res.add(UIAction(RobotActionType.INPUT_GESTURE).apply { action.data = "Thumbs up" })
                "gstd" -> res.add(UIAction(RobotActionType.INPUT_GESTURE).apply { action.data = "Thumbs down" })
                "gspo" -> res.add(UIAction(RobotActionType.INPUT_GESTURE).apply { action.data = "Point" })
                "gsfg" -> res.add(UIAction(RobotActionType.INPUT_GESTURE).apply { action.data = "Finger gun" })
                "gswv" -> res.add(UIAction(RobotActionType.INPUT_GESTURE).apply { action.data = "Waving" })
                "gsst" -> res.add(UIAction(RobotActionType.INPUT_GESTURE).apply { action.data = "Stop" })

                // Mapping for specific sounds. hacky solution should be changed
                "sebl" -> res.add(UIAction(RobotActionType.INPUT_SOUND).apply { action.data = "Eight bit laser" })
                "sbrm" -> res.add(UIAction(RobotActionType.INPUT_SOUND).apply { action.data = "Beeping robot machine" })
                "srpo" -> res.add(UIAction(RobotActionType.INPUT_SOUND).apply { action.data = "Robot power-off" })
                "smec" -> res.add(UIAction(RobotActionType.INPUT_SOUND).apply { action.data = "Mechanical clamp" })
                "sroc" -> res.add(UIAction(RobotActionType.INPUT_SOUND).apply { action.data = "Robot call" })
                "srod" -> res.add(UIAction(RobotActionType.INPUT_SOUND).apply { action.data = "Robot drum" })

                // Mapping for loop end/start
                "lpe" -> res.add(UIAction(RobotActionType.LOOP_END))

                else -> {
                    // Check if the action code represents a loop start (e.g., "lsp-5").
                    if (ac.contains("-")) { //Loop start (hopefully)
                        val loopIterations = ac.split("-")[1]
                        val loopAction = UIAction(RobotActionType.LOOP_START)
                        val iterationsInt = loopIterations.toIntOrNull()
                        if (iterationsInt != null) {
                            loopAction.iterations = iterationsInt
                        } else {
                            loopAction.iterations = 0
                        }
                        res.add(loopAction)
                    }
                    else {
                        Log.d("QrScannerViewModel","Undefined type found in Parsing: $ac")
                    }
                }
            }
        }
        return res
    }

    /**
     * Processes a scanned QR code.
     * This function parses the QR code string into a list of actions and then
     * updates the [SequenceRepository] with these actions.
     *
     * @param code The raw string data scanned from the QR code.
     * @return `true` if the QR code was successfully parsed and resulted in at least one action,
     *         `false` otherwise (e.g., if the QR code was empty or contained no valid actions).
     */
    fun onQRCodeScanned(code: String): Boolean {
        val actions = parseQR(code)
        sequenceRepository.setActions(actions)

        return !actions.isEmpty()
    }
}


