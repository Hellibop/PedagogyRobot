package com.example.aida.ui.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aida.domain.remote.ConnectionId
import com.example.aida.domain.remote.ConnectionState
import com.example.aida.domain.remote.RobotApiException
import com.example.aida.domain.repository.AppPrefsRepository
import com.example.aida.domain.repository.ConnectionRepository
import com.example.aida.domain.repository.ManualControlRepository
import com.example.aida.domain.repository.TopBarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Represents the UI state for the Manual Control screen.
 *
 * @property sttText The text recognized from speech-to-text. Defaults to an empty string.
 * @property isRecording True if voice recording is currently active.
 * @property isMicEnabled True if the microphone is enabled in the application settings.
 * @property isCameraEnabled True if the camera feed is enabled in the application settings.
 * @property port The port number used for robot communication.
 * @property ip The IP address of the robot.
 * @property connectionStates A map holding the current [ConnectionState] for each [ConnectionId].
 *                            Initializes all connection states to [ConnectionState.Disconnected].
 */
data class ManualControlUiState(
    var sttText : String = "",
    var isRecording : Boolean = false,
    val isMicEnabled    : Boolean = false,
    val isCameraEnabled : Boolean = false,
    var port : Int = 0,
    var ip : String = "",

    val connectionStates: Map<ConnectionId, ConnectionState> =
        ConnectionId.entries.associateWith { ConnectionState.Disconnected }
)

/**
 * ViewModel for the Manual Control screen.
 * Manages the state and business logic for manual robot interaction, including
 * video streaming, Lidar data, joystick control, and speech-to-text functionality.
 * It combines data from various repositories to update the [ManualControlUiState].
 *
 * @param manualRepository Repository for handling manual control commands and data flows (STT, video, Lidar, joystick).
 * @param settingsRepository Repository for accessing application preferences like IP address and port.
 * @param connectionRepository Repository for managing and observing connection states to the robot.
 * @param topBarRepository Repository for observing top bar states, such as microphone and camera enabled status.
 */
@HiltViewModel
class ManualControlViewModel @Inject constructor(
    private val manualRepository: ManualControlRepository,
    private val settingsRepository: AppPrefsRepository,
    private val connectionRepository: ConnectionRepository,
    private val topBarRepository: TopBarRepository
) : ViewModel() {

    /**
     * Private mutable state flow  and public state flow for the [ManualControlUiState]
     * The UI layer collects the public flow to react to state changes. non-mutable.
     */
    private val _uiState = MutableStateFlow(ManualControlUiState())
    val uiState : StateFlow<ManualControlUiState> = _uiState.asStateFlow()

    val videoFrame: StateFlow<ImageBitmap?> = manualRepository.videoFrameFlow
    val lidarFrame: StateFlow<ImageBitmap?> = manualRepository.lidarFrameFlow

    init {
        // combined flows for state
        combine(
            settingsRepository.currentSettings,
            connectionRepository.connectionStates,
            topBarRepository.micEnabled,
            topBarRepository.cameraEnabled
        ) { settings, connStates, mic, cam ->
            ManualControlUiState(
                sttText         = _uiState.value.sttText,
                isRecording     = _uiState.value.isRecording,
                ip              = settings.ip,
                port            = settings.port,
                isMicEnabled    = mic,
                isCameraEnabled = cam,
                connectionStates= connStates
            )
        }
            .onEach { _uiState.value = it }
            .launchIn(viewModelScope)


        // Watch if Lidar is connected an enabled: start lidar streaming
        combine(
            connectionRepository.connectionStates
                .map { it[ConnectionId.LIDAR] is ConnectionState.Connected },
            topBarRepository.lidarEnabled
        ) { connected, enabled -> connected && enabled }
            .distinctUntilChanged()
            .onEach { shouldStream ->
                if (shouldStream) manualRepository.startLidar()
                else          manualRepository.stopLidar()
            }
            .launchIn(viewModelScope)


        // Watch if video connected and camera toggled on: start video streaming
        combine(
            connectionRepository.connectionStates
                .map { it[ConnectionId.VIDEO] is ConnectionState.Connected },
            topBarRepository.cameraEnabled
        ) { connected, enabled -> connected && enabled }
            .distinctUntilChanged()
            .onEach { shouldStream ->
                if (shouldStream)  manualRepository.startVideo()
                else               manualRepository.stopVideo()
            }
            .launchIn(viewModelScope)
    }

    // Coroutine jobs for managing ongoing data flows (video, Lidar, STT, joystick).
    // These allow cancellation of the flows when they are no longer needed.

    private var sttJob : Job? = null
    private var joystickJob : Job? = null

    /**
     * !!UNSURE OF THIS IMPLEMENTATION!!
     * Handles the action when the record voice button is pressed and held.
     * Sends instruction to the robot to turn on STT
     * Sets isRecording in the [_uiState] to true
     */
    fun onRecordButtonHold()  = viewModelScope.launch {
        if (!_uiState.value.isRecording) {
            try {
                manualRepository.sendStartSTT()
                _uiState.update { it.copy(isRecording = true) }
            } catch (e: RobotApiException) {
                Log.d("ManualControlViewModel", e.message.toString())
            }
        }
    }

    /**
     * !!UNSURE OF THIS IMPLEMENTATION!!
     * Handles the action when the record voice button is released.
     * Sets isRecording in the [_uiState] to false since the recording is done.
     * Creates a sttJob to recieve sttText from the [manualRepository] via getSTTDataFlow().
     * a flow to collect the transcribed text from the STT service.
     */
    fun onRecordButtonRelease() {
        if (_uiState.value.isRecording) {

            sttJob = manualRepository
                .getSTTDataFlow()
                .onStart { _uiState.update { it.copy(isRecording = false) } }
                .catch { e ->
                    Log.d("ManualControlViewModel", e.message.toString())
                }
                .onEach { text ->
                    _uiState.update { it.copy(sttText = text) }
                }
                .launchIn(viewModelScope)
        } else {
            sttJob?.cancel()
            sttJob = null
        }
    }

    /**
     * Sends joystick movement data (x, y coordinates) to the robot.
     * This action is performed only if the joystick connection is active and
     * no other joystick data sending operation is currently in progress.
     *
     * @param x The x-axis value of the joystick.
     * @param y The y-axis value of the joystick.
     */
    fun onJoystickMove(x: Float, y: Float) {
        if (joystickJob == null || joystickJob?.isCompleted == true) {
            joystickJob = viewModelScope.launch {
                try {
                    manualRepository.sendJoystickData(x, y)
                } catch (e: RobotApiException) {
                    Log.d("ManualControlViewModel", e.message.toString())
                }
            }
        }
    }
}