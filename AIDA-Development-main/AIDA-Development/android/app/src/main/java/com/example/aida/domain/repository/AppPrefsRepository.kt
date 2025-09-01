package com.example.aida.domain.repository
import com.example.aida.domain.model.RobotAction
import com.example.aida.domain.model.Settings
import kotlinx.coroutines.flow.Flow

interface AppPrefsRepository {
    // val actionSequence: Flow<List<RobotAction>>
    suspend fun loadSavedSequence(): List<RobotAction>
    val currentSettings: Flow<Settings>
    suspend fun updateSettings(newSettings: Settings)
    suspend fun saveSequence(newSequence: List<RobotAction>)
}
