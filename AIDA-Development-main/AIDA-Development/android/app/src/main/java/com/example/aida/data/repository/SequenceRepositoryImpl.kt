package com.example.aida.data.repository

import android.util.Log
import com.example.aida.domain.model.RobotActionType
import com.example.aida.domain.remote.RobotApi
import com.example.aida.domain.repository.AppPrefsRepository
import com.example.aida.domain.repository.SequenceRepository
import com.example.aida.ui.component.UIAction
import com.example.aida.ui.constants.loopIterationRange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The implementation of the sequence repository.
 *
 * @property _actionsFlow A [MutableStateFlow] that contains the actions in the sequence bar.
 *      Only used internally.
 */
class SequenceRepositoryImpl @Inject constructor(
    private val _appPrefsRepository: AppPrefsRepository,
    private val _robotApi: RobotApi
) : SequenceRepository {
    // MutableStateFlow to hold and emit the current list state
    private val _actionsFlow = MutableStateFlow<List<UIAction>>(emptyList())


    /**
     * Load the stored sequence on initialization
     */
    init {
        loadSequence()
    }


    // loads the the sequence from the datastore
    private fun loadSequence() {
        val savedList = mutableListOf<UIAction>()

        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            _appPrefsRepository.loadSavedSequence().map {
                val newAction = UIAction(it.type)
                newAction.action.data = it.data
                newAction.iterations = it.data.toIntOrNull() ?: 1
                savedList.add(newAction)
            }

            _actionsFlow.value = savedList.map { it.clone() }
        }
    }

    /**
     * Saves the current sequence to the repository.
     */
     override fun saveSequence() {
        val sequence = _actionsFlow.value.map { it.action }

        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            _appPrefsRepository.saveSequence(sequence)
        }
    }

    /**
     * Adds an action to the sequence bar list.
     *
     * @param type A [RobotActionType] enum that describes which type of action to be added.
     *      All the other values for the action can be derived from the type.
     */
    override  fun addAction(type: RobotActionType) {
        val copy = _actionsFlow.value.toMutableList()
        val action = UIAction(type)
        copy.add(action)


        if (type == RobotActionType.LOOP_START) {
            // If the action added was a loop start, also add a loop end
            val loopEnd = UIAction(RobotActionType.LOOP_END)
            action.action.data = "1" // Initialize loops to 1 iteration

            copy.add(loopEnd)
        }

        _actionsFlow.value = copy.toList()
    }

    /**
     * Removes an action from the sequence bar list.
     *
     * @param index The index at which to remove an action..
     */
    override  fun removeAction(index: Int) {
        val copy = _actionsFlow.value.toMutableList()
        val actionToRemove = copy[index].action.type

        // If loop start or loop end is removed, remove the corresponding loop-pair-action, otherwise just remove the action
        if (actionToRemove == RobotActionType.LOOP_START)
        {
            // Find the corresponding LOOP END and remove both
            var nextIndex = index + 1

            // Since a loop start should never be last in a list, this should never trigger.
            // If it does, we have a bug somewhere.
            assert(nextIndex < copy.size)

            var nextAction = copy[nextIndex].action.type

            // Search for LOOP END
            while (nextAction != RobotActionType.LOOP_END && nextIndex < copy.size) {
                nextIndex++
                nextAction = copy[nextIndex].action.type
            }

            if (nextAction == RobotActionType.LOOP_END) {
                copy.removeAt(index)                // Remove LOOP START
                copy.removeAt(nextIndex - 1)  // Remove LOOP END
            }

        }
        else if (actionToRemove == RobotActionType.LOOP_END)
        {
            // Find the corresponding LOOP START and remove both
            var nextIndex = index - 1

            // Since a loop end should never be last in a list, this should never trigger.
            // If it does, we have a bug somewhere.
            assert(nextIndex >= 0)

            var nextAction = copy[nextIndex].action.type

            // Search for LOOP START
            while (nextIndex >= 0 && nextAction != RobotActionType.LOOP_START) {
                nextIndex--
                nextAction = copy[nextIndex].action.type
            }

            if (nextAction == RobotActionType.LOOP_START) {
                copy.removeAt(index)      // Remove LOOP END
                copy.removeAt(nextIndex)  // Remove LOOP START
            }
        }
        else
        {
            copy.removeAt(index)
        }
        _actionsFlow.value = copy.toList()
    }

    /**
     * Moves an action from one index to another.
     *
     * @param from The index to move from.
     * @param to The index to move to.
     */
    override fun moveAction(from: Int, to: Int) {
        val copy = _actionsFlow.value.toMutableList()

        if (from in copy.indices && to in copy.indices) {
            copy.add(getLegalIndex(from, to), copy.removeAt(from).clone())

        } else {
            Log.e("SequenceBar, onSettle", "Error: fromIndex or to Index out of bounds in onSettle.")
        }
        _actionsFlow.value = copy.toList()
    }

    /*
     * Gets the first index that is valid to place an action at, so that a loop begin never
     * precedes a loop end.
     */
    private fun getLegalIndex(from: Int, to: Int): Int {
        val movedAction =_actionsFlow.value[from].action.type

        if (movedAction == RobotActionType.LOOP_END || movedAction == RobotActionType.LOOP_START){
            val stepDir = if (from < to) 1 else -1
            var index = from + stepDir

            while (index != to + stepDir) {
                val actionAtIndex =_actionsFlow.value[index].action.type

                if ((actionAtIndex == RobotActionType.LOOP_END) || (actionAtIndex == RobotActionType.LOOP_START)) {
                    return index - stepDir
                }

                index += stepDir
            }
        }

        return to
    }

    /**
     * Gets the action at a specific index.
     *
     * @param index The index to get te action at.
     * @return The [UIAction] at the index.
     */
    override fun getAction(index: Int): UIAction {
        return _actionsFlow.value[index]
    }


    /**
     * Gets the number of actions in the list.
     *
     * @return The number of actions in the list.
     */
    override  fun actionCount(): Int {
        return _actionsFlow.value.size
    }

    /**
     * Sets the number of iterations for a specific action. Must be a loop action.
     *
     * @param index The index of the action whose iterations should be changed.
     * @param iterations The number of iterations to set the action to.
     */
    override fun setIterations(index: Int, iterations: Int) {
        val copy = _actionsFlow.value.toMutableList()

        val newAction = _actionsFlow.value[index].clone()
        assert(newAction.action.type == RobotActionType.LOOP_START)

        newAction.iterations = iterations.coerceIn(loopIterationRange)
        newAction.action.data = iterations.coerceIn(loopIterationRange).toString()
        copy[index] = newAction

        _actionsFlow.value = copy.toList()
    }

    /**
     * Sets the data for a specific action. Must be a special action.
     *
     * @param index The index of the action whose iterations should be changed.
     * @param data The data to set the action to.
     */
    override fun setData(index: Int, data: String) {
        val copy = _actionsFlow.value.toMutableList()

        val newAction = _actionsFlow.value[index].clone()
        assert(newAction.action.type.isSpecial && (newAction.action.type != RobotActionType.LOOP_START) &&
                newAction.action.type != RobotActionType.LOOP_END)

        newAction.action.data = data
        copy[index] = newAction

        _actionsFlow.value = copy.toList()
    }

    /**
     * Gets a list of the action in the list as a [Flow].
     *
     * @return A [Flow] that contains the list of actions.
     */
    override fun getActionsFlow(): Flow<List<UIAction>> {
        return  _actionsFlow.asStateFlow()
    }

    /**
     * Sends the entire list of robot actions to the robot to be executed.
     */
    override suspend fun executeFullSequence() {
        val actionsToSend = _actionsFlow.value.map {
            it.action
        }.toMutableList()

        _robotApi.sendSequence(actionsToSend)
    }


    /**
     * Sends a range of the list of robot actions to the robot to be executed.
     *
     * @param from The start index of the range.
     * @param to The end index of the range.
     *
     */
    override suspend fun executeSectionOfSequence(from: Int, to: Int) {
        val actionsToSend = _actionsFlow.value.subList(from, to).map {
            it.action
        }.toMutableList()

        _robotApi.sendSequence(actionsToSend)
    }

    /**
     * Sends an action to be executed by the robot.
     *
     * @param index The index of the action to execute.
     */
    override suspend fun executeAction(index: Int) {
        _robotApi.sendSequence(mutableListOf(_actionsFlow.value[index].action))

    }

    /**
     * Tells the robot to stop executing the actions.
     */
    override suspend fun stopExecutingActions() {
        _robotApi.sendStopSequence();
    }

    /**
     * Clears the list of actions contained in the repository.
     */
    override fun clearActions() {
        _actionsFlow.value = emptyList()
    }

    /**
     * Sets the action list of the repository to one provided by the caller.
     *
     * @param actions The new list of actions.
     */
    override fun setActions(actions: List<UIAction>) {
        _actionsFlow.value = actions.map { it.clone() }
    }

    /**
     * Sets the duration of a specific action.
     *
     * @param index The index of the action whose duration should be changed.
     * @param duration The new duration of the action.
     */
    override fun setDuration(index: Int, duration: Double) {
        val copy = _actionsFlow.value.toMutableList()

        // Create a copy of the action instance to force recompose on list change
        val newAction = _actionsFlow.value[index].clone()
        newAction.durationRemaining = duration.coerceAtLeast(0.0)

        copy[index] = newAction

        _actionsFlow.value = copy.toList()
    }


    /**
     * Resets all the durations of the actions in the list to their original values.
     */
    override fun resetAllDurations() {
        val copy = _actionsFlow.value.toMutableList()

        copy.forEachIndexed { index, item ->
            val newAction = item.clone()
            newAction.durationRemaining = newAction.action.type.duration
            copy[index] = newAction
        }

        _actionsFlow.value = copy.toList()
    }

    /**
     * Resets the duration of all actions in the range to their original values.
     *
     * @param begin The start of the range (inclusive).
     * @param end The end of the range (exclusive).
     */
    override fun resetDurationsInRange(begin: Int, end: Int) {
        val copy = _actionsFlow.value.toMutableList()

        copy.subList(begin, end).forEachIndexed { index, item ->
            val newAction = item.clone()
            newAction.durationRemaining = item.action.type.duration
            // index here corresponds to index in the sublist, so add
            // begin to make it the index in the entire list
            copy[begin + index] = newAction
        }

        _actionsFlow.value = copy.toList()
    }
}