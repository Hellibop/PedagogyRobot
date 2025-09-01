package com.example.aida.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aida.domain.model.Settings
import com.example.aida.domain.remote.ConnectionId
import com.example.aida.domain.remote.ConnectionState
import com.example.aida.domain.remote.RobotApi
import com.example.aida.domain.remote.RobotApiException
import com.example.aida.domain.repository.AppPrefsRepository
import com.example.aida.domain.repository.ConnectionRepository
import com.example.aida.domain.repository.TopBarRepository
import com.google.protobuf.Internal.BooleanList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * uiState for the config page. Contains fields needed to render the page.
 * @property port the current port used in the connection to the robot.
 * @property ip the current ip used in the connection to the robot.
 * @property connectionStates a map of each [ConnectionId] to the robot and its [ConnectionState]
 */
data class ConfigUiState(
    var port : Int = 0,
    var ip : String = "",
    var isConnected : Boolean = false,

    // Initialize all connection IDs as Disconnected by default
    val connectionStates: Map<ConnectionId, ConnectionState> =
        ConnectionId.entries.associateWith { ConnectionState.Disconnected }
)

@HiltViewModel
class ConfigViewModel @Inject constructor(
    private val connectionRepository: ConnectionRepository,
    private val settingsRepository: AppPrefsRepository,
    private val robotApi: RobotApi,
    private val topBarRepository: TopBarRepository
): ViewModel() {

    // Stateflow that holds the uiState
    private val _uiState = MutableStateFlow(ConfigUiState())

    /**
     * uiState that is collected in the composable.
     */
    val uiState : StateFlow<ConfigUiState> = _uiState.asStateFlow()

    /**
     * Sets up flow of stored settings and stages of connection to the robot. Updates the ConfigUiState with new values.
     */
    init{
        // Settings flow
        settingsRepository.currentSettings
            .onEach { setting ->
                _uiState.update {
                    it.copy(
                        ip = setting.ip,
                        port = setting.port
                    )
                }
            }.launchIn((viewModelScope))

        // Flow that emits true if any connection is connected
        connectionRepository.isConnected
            .onEach { connected ->
                _uiState.update { it.copy(isConnected = connected) }
            }
            .launchIn(viewModelScope)

        // ConnectionStates flow
        connectionRepository.connectionStates
            .onEach { states ->
                _uiState.update { it.copy(connectionStates = states) }
            }.launchIn(viewModelScope)
    }

    /**
     * Saves the new settings to Datastore.
     */
    private suspend fun persistSettings(ip: String, port: Int) {
        settingsRepository.updateSettings(Settings(ip, port))
    }

    /**
     * Connects to robot with given ip and port, also updates the stored settings.
     * @param ipInput ip-address to connect to and store
     * @param portInput new port to connect to and store
     */
    fun connectToRobot(ip: String, port: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                persistSettings(ip, port)
                robotApi.connect(ip, port)
            } catch (e: RobotApiException) {
                Log.d("ConfigViewModel", "error: ${e.message}")
            }
        }
    }

    /**
     * Disconnect all connections to the robot.
     */
    fun disconnectRobot() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                robotApi.disconnect()
            } catch (e: RobotApiException) {
                Log.d("ConfigViewModel", "error: ${e.message}")
            }
        }
    }
}