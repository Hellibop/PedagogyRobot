package com.example.aida.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aida.domain.remote.ConnectionId
import com.example.aida.domain.remote.ConnectionState
import com.example.aida.domain.remote.RobotApiException
import com.example.aida.domain.repository.ConnectionRepository
import com.example.aida.domain.repository.TopBarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Represents the UI state for the application's top bar.
 * This includes the enabled status of various features like gestures, microphone, camera,
 * Lidar, and the connection states of different robot components.
 *
 * @property isGestureEnabled True if gesture recognition is currently enabled.
 * @property isMicEnabled True if the microphone is currently enabled.
 * @property isCameraEnabled True if the camera feed is currently enabled.
 * @property isLidarEnabled True if Lidar is currently enabled.
 *
 * @property connectionStates A map holding the current [ConnectionState] for each [ConnectionId].
 *                            Initializes all connection states to [ConnectionState.Disconnected].
 */
data class TopBarUiState(
    var isGestureEnabled : Boolean = false,
    var isMicEnabled : Boolean = false,
    var isCameraEnabled : Boolean = false,
    val isLidarEnabled   : Boolean = false, // Currently unused, should potentially add a lidar-button?
    val connectionStates: Map<ConnectionId, ConnectionState> =
        ConnectionId.entries.associateWith { ConnectionState.Disconnected }
)

/**
 * ViewModel for the application's top bar.
 * Manages the UI state related to top bar controls (microphone, camera, gestures)
 * and reflects overall connection statuses. It interacts with [TopBarRepository]
 * to send commands and [ConnectionRepository] to observe connection states.
 *
 * @param topBarRepository Repository for managing top bar feature states (mic, camera, gestures).
 * @param connectionRepository Repository for observing connection states to various robot components.
 */
@HiltViewModel
class TopBarViewModel @Inject constructor(
    private val topBarRepository: TopBarRepository,
    private val connectionRepository: ConnectionRepository
) : ViewModel() {

    /**
     * Private mutable state flow and public state flow for the [TopBarUiState]
     * The UI layer collects the public flow to react to state changes. non-mutable.
     */
    private val _uiState = MutableStateFlow(TopBarUiState())
    val uiState : StateFlow<TopBarUiState> = _uiState.asStateFlow()

    init {

        // Combines multiple flows: connection states from ConnectionRepository,
        // and enabled statuses for mic, camera, gestures, and Lidar from TopBarRepository.
        // When any of these source flows emit a new value, a new TopBarUiState is created.
        combine(
            connectionRepository.connectionStates,
            topBarRepository.micEnabled,
            topBarRepository.cameraEnabled,
            topBarRepository.gestureEnabled,
            topBarRepository.lidarEnabled
        ) { connStates, micStatus, camStatus, gestStatus, lidarStatus ->

            // Create a new UI state instance with the latest values from all combined flows.
            TopBarUiState(
                isGestureEnabled = gestStatus,
                isMicEnabled = micStatus,
                isCameraEnabled = camStatus,
                isLidarEnabled = lidarStatus,
                connectionStates = connStates
            )
            }
            .onEach { newState ->

                // Update the internal UI state with the newly combined state.
                _uiState.value = newState
            }
            .launchIn(viewModelScope)
    }

    /**
     * Toggles the microphone's enabled state.
     * If the microphone is currently disabled, it sends a command to start it.
     * If it's enabled, it sends a command to stop it.
     */
    fun onMicButtonClick() = viewModelScope.launch {
        if (!_uiState.value.isMicEnabled){
            topBarRepository.sendStartMic()
        } else {
            topBarRepository.sendStopMic()
        }
    }

    /**
     * Toggles the camera's enabled state.
     * If the camera is currently disabled, it sends a command to start it.
     * If it's enabled, it sends a command to stop it.
     */
    fun onCameraButtonClick() = viewModelScope.launch {
        if (!_uiState.value.isCameraEnabled){
            topBarRepository.sendStartCamera()
        } else {
            topBarRepository.sendStopCamera()
        }
    }

    /**
     * Toggles the gesture recognition's enabled state.
     * If gesture recognition is currently disabled, it sends a command to start it.
     * If it's enabled, it sends a command to stop it.
     */
    fun onGestureButtonClick() = viewModelScope.launch {
        if (!_uiState.value.isGestureEnabled){
            topBarRepository.sendStartGesture()
        } else {
            topBarRepository.sendStopGesture()
        }
    }
}