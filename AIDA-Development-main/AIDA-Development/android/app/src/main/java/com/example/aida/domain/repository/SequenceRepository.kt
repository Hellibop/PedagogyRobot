package com.example.aida.domain.repository

import com.example.aida.domain.model.RobotActionType
import com.example.aida.ui.component.UIAction
import kotlinx.coroutines.flow.Flow

interface SequenceRepository {
    fun saveSequence();
    fun addAction(type: RobotActionType)
    fun removeAction(index: Int)
    fun moveAction(from: Int, to: Int)
    fun getAction(index: Int): UIAction
    fun setDuration(index: Int, duration: Double)
    fun resetAllDurations()
    fun resetDurationsInRange(begin: Int, end: Int)
    fun setIterations(index: Int, iterations: Int)
    fun setData(index: Int, data: String)

    fun actionCount(): Int
    fun getActionsFlow(): Flow<List<UIAction>>

    fun clearActions()
    fun setActions(actions: List<UIAction>)

    suspend fun executeFullSequence()
    suspend fun executeSectionOfSequence(from: Int, to: Int)
    suspend fun executeAction(index: Int)
    suspend fun stopExecutingActions()
}
