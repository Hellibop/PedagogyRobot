package com.example.aida.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aida.domain.model.RobotActionType
import com.example.aida.domain.remote.ConnectionId
import com.example.aida.domain.remote.ConnectionState
import com.example.aida.domain.remote.RobotApiException
import com.example.aida.domain.repository.ConnectionRepository
import com.example.aida.domain.repository.SequenceRepository
import com.example.aida.ui.component.UIAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/* This enum represents the current state the UI in the sequence tab is in.
 * This affects which actions are allowed and the colors of the UI.
 */
enum class UserInteractionState {
    STOPPED,
    PLAYING,
    DRAGGING
}

data class SequenceBarState(
    val actions: List<UIAction> = emptyList(),
    var menuState: UserInteractionState = UserInteractionState.STOPPED,
    var isLocked: Boolean = false,
    var isConnected: Boolean = false
)

/**
 * A viewmodel that stores the state of the sequence bar.
 *
 * @property _sequenceBarState A [MutableStateFlow] that contains the state of the sequence bar.
 *      Only used internally.
 * @property sequenceBarState A [StateFlow] that can be used to retrieve the current state of the
 *      sequence bar.
 */
@HiltViewModel
class SequenceViewModel @Inject constructor(
    private val sequenceRepository: SequenceRepository,
    private val connectionRepository: ConnectionRepository
) : ViewModel() {

    private val _sequenceBarState = MutableStateFlow(SequenceBarState())
    val sequenceBarState: StateFlow<SequenceBarState> = _sequenceBarState.asStateFlow()

    init {

        viewModelScope.launch {
            connectionRepository.connectionStates
                .collect { states ->
                    _sequenceBarState.update {
                        it.copy(isConnected = states[ConnectionId.SEQUENCE] is ConnectionState.Connected)
                    }
                }
        }

        viewModelScope.launch {
            sequenceRepository.getActionsFlow()
                .collect{ actionsList ->
                _sequenceBarState.update { currentState ->
                    currentState.copy(actions = actionsList)
                }
            }
        }
    }

    /**
     * Saves the current sequence of actions to storage so the sequence is saved
     * when closing the app.
     */
    fun saveActions() {
        sequenceRepository.saveSequence()
    }

    /**
     * Adds a new action of the specified [RobotActionType] to the end of the current sequence.
     *
     * @param actionType The type of [RobotActionType] to add.
     */
    fun addAction(actionType: RobotActionType) {
        sequenceRepository.addAction(actionType)
    }

    /**
     * Removes the action at the specified index from the sequence.
     *
     * @param index The index of the action to remove.
     */
    fun removeAction(index: Int) {
        sequenceRepository.removeAction(index)
    }

    /**
     * Moves an action from one position in the sequence to another.
     *
     * @param from The starting index of the action to move.
     * @param to The target index for the action.
     */
    fun moveAction(from: Int, to: Int) {
        sequenceRepository.moveAction(from, to)
    }

    /**
     * Sets the number of iterations for a specific action in the sequence.
     * This is relevant for actions that can be repeated.
     *
     * @param index The index of the action to modify.
     * @param iterations The number of times the action should be repeated.
     */
    fun setIterations(index: Int, iterations: Int) {
        sequenceRepository.setIterations(index, iterations)
    }

    /**
     * Sets custom data associated with a specific action.
     * The meaning of 'data' depends on the [RobotActionType].
     *
     * @param index The index of the action to modify.
     * @param data A string representation of the data for the action.
     */
    fun setData(index: Int, data: String) {
        sequenceRepository.setData(index, data)
    }

    /**
     * Retrieves the action at the specified index.
     *
     * @param index The index of the action to retrieve.
     * @return The [UIAction] at the given index.
     */
    fun getAction(index: Int): UIAction {
        return sequenceRepository.getAction(index)
    }

    /**
     * Gets the total number of actions currently in the sequence.
     *
     * @return The count of actions.
     */
    fun actionCount(): Int {
        return sequenceRepository.actionCount()
    }

    /**
     * Removes all actions from the current sequence.
     */
    fun clearActions() {
        sequenceRepository.clearActions()
    }

    /**
     * Asynchronously executes a single action at the specified index.
     * This is a suspending function and should be called from a coroutine.
     * @param index The index of the action to execute.
     */
    fun executeAction(index: Int) {
        if (sequenceBarState.value.isConnected) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    sequenceRepository.executeAction(index)
                } catch (e: RobotApiException) {
                    Log.d("SequenceViewModel","Error: ${e.message}")
                }
            }
        }
    }

    /**
     * Asynchronously executes all actions in the sequence from beginning to end.
     * This is a suspending function and should be called from a coroutine.
     */
     fun executeFullSequence() {
        if (sequenceBarState.value.isConnected) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    sequenceRepository.executeFullSequence()
                } catch (e: RobotApiException) {
                    Log.d("SequenceViewModel","Error: ${e.message}")
                }
            }
        }
    }

    /**
     * Asynchronously executes a specific range of actions within the sequence.
     * The 'to' index is exclusive.
     * This is a suspending function and should be called from a coroutine.
     * @param from The index (inclusive) of the section to execute.
     * @param to The index (exclusive) of the section to execute.
     */
    fun executeSectionOfSequence(from: Int, to: Int) {
        if (sequenceBarState.value.isConnected) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    sequenceRepository.executeSectionOfSequence(from, to)
                } catch (e: RobotApiException) {
                    Log.d("SequenceViewModel","Error: ${e.message}")
                }
            }
        }
    }

    /**
     * Asynchronously requests to stop any currently executing actions or sequences.
     * The stop might not be instantaneous.
     * This is a suspending function and should be called from a coroutine.
     */
    fun stopExecutingActions() {
        if (sequenceBarState.value.isConnected) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    sequenceRepository.stopExecutingActions()
                } catch (e: RobotApiException) {
                    Log.d("SequenceViewModel","Error: ${e.message}")
                }
            }
        }
    }

    /**
     * Sets the duration for a specific action.
     * This is used to count down the remaining duration when playing an action.
     * @param index The index of the action to modify.
     * @param duration The duration value in seconds.
     */
    fun setDuration(index: Int, duration: Double) {
        sequenceRepository.setDuration(index, duration)
    }

    /**
     * Resets the durations of all actions in the sequence to their default values.
     * These values are dependent on the [RobotActionType] of the actions.
     */
    fun resetAllDurations() {
        sequenceRepository.resetAllDurations()
    }

    /**
     * Resets the durations of actions within a specified range to their default values.
     *
     * @param begin The starting index (inclusive) of the range.
     * @param end The ending index (exclusive) of the range.
     */
    fun resetDurationsInRange(begin: Int, end: Int) {
        sequenceRepository.resetDurationsInRange(begin, end)
    }

    /**
     * Sets the current state of the sequence bar.
     *
     * @param state A [UserInteractionState] value to switch the current state to.
     */
    fun setState(state: UserInteractionState) {
        _sequenceBarState.update { currentState ->
            currentState.copy(menuState = state)
        }
    }

    /**
     * Gets the current state of the sequence bar.
     *
     * @return A [UserInteractionState] that describes the current UI state.
     */
    fun getState(): UserInteractionState {
        return _sequenceBarState.value.menuState
    }

    /**
     * Sets the sequence bar to be either locked or unlocked.
     *
     * @param value to set the lock value to.
     */
    fun setLockState(value: Boolean) {
        _sequenceBarState.update { currentState ->
            currentState.copy(isLocked = value)
        }
    }

    /**
     * Gets the lock state of the sequence bar.
     *
     * @return The current lock value.
     */
    fun getLockedState(): Boolean {
        return _sequenceBarState.value.isLocked
    }

}