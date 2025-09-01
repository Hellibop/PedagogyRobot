package com.example.aida.data.repository

import android.util.Log
import com.example.aida.domain.remote.ConnectionId
import com.example.aida.domain.remote.ConnectionState
import com.example.aida.domain.remote.RobotApi
import com.example.aida.domain.remote.RobotApiException
import com.example.aida.domain.repository.ConnectionRepository
import com.example.aida.domain.repository.TopBarRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject


/**
 * The implementation of the TopbarRepository. Holds functions to turn on mic/camera/gesture/lidar on the robot.
 * Also holds flows on what instruments is enabled on the robot.
 */

class TopBarRepositoryImpl @Inject constructor(
    private val robotApi: RobotApi,
    private val connectionRepository: ConnectionRepository
): TopBarRepository{

    private val _micEnabled = MutableStateFlow(false)
    private val _cameraEnabled = MutableStateFlow(false)
    private val _gestureEnabled = MutableStateFlow(false)
    private val _lidarEnabled = MutableStateFlow(false)

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // map each ConnectionId to its toggle flow
    private val connectionFlows: Map<ConnectionId, MutableStateFlow<Boolean>> =
        mapOf(
            ConnectionId.STT to _micEnabled,
            ConnectionId.VIDEO to _cameraEnabled,
            ConnectionId.GESTURE to _gestureEnabled,
            ConnectionId.LIDAR to _lidarEnabled
        )

    init {
        connectionRepository.connectionStates
            .onEach { states ->
                states.forEach { (id, state) ->
                    // Set enabled flags
                    if (state is ConnectionState.Disconnected || state is ConnectionState.Failed) {
                        connectionFlows[id]?.value = false
                    }
                }
            }
            .map { states ->
                // Hacky solution to always enable devices on connect
                listOf(
                    ConnectionId.STT,
                    ConnectionId.VIDEO,
                    ConnectionId.GESTURE,
                    ConnectionId.LIDAR
                ).all { id -> states[id] is ConnectionState.Connected }
            }
            .distinctUntilChanged()
            .onEach { allConnected ->
                if (allConnected) {
                    enableDevices()
                }
            }
            .launchIn(scope)
    }

    override val micEnabled: Flow<Boolean> = _micEnabled.asStateFlow()
    override val cameraEnabled: Flow<Boolean> = _cameraEnabled.asStateFlow()
    override val gestureEnabled: Flow<Boolean> = _gestureEnabled.asStateFlow()
    override val lidarEnabled: Flow<Boolean> = _lidarEnabled.asStateFlow()

    /**
     * Enable all of the robot’s input devices if they’re not already enabled.
     */
    override suspend fun enableDevices() = withContext(Dispatchers.IO) {
        if (!_cameraEnabled.value) sendStartCamera()
        if (!_lidarEnabled.value)  sendStartLidar()
        if (!_micEnabled.value)    sendStartMic()
        if (!_gestureEnabled.value) sendStartGesture()
    }


    /**
     * Send an instruction using the [RobotApi] to turn on the microphone.
     */
    override suspend fun sendStartMic() = withContext(Dispatchers.IO) {
        try {
            robotApi.sendStartMic()
            _micEnabled.value = true
        } catch (e: RobotApiException) {
            _micEnabled.value = false
        }
    }

    /**
     * Send an instruction using the [RobotApi] to turn off the microphone.
     */
    override suspend fun sendStopMic() = withContext(Dispatchers.IO) {
        try {
            robotApi.sendStopMic()
            _micEnabled.value = false
        } catch (e: RobotApiException) {
            _micEnabled.value = true
        }
    }

    /**
     * Send an instruction using the [RobotApi] to turn on the camera.
     */
    override suspend fun sendStartCamera() = withContext(Dispatchers.IO) {
        try {
            robotApi.sendStartCamera()
            _cameraEnabled.value = true
        } catch (e: RobotApiException) {
            _cameraEnabled.value = false
        }
    }

    /**
     * Send an instruction using the [RobotApi] to turn off the camera.
     */
    override suspend fun sendStopCamera() = withContext(Dispatchers.IO) {
        try {
            robotApi.sendStopCamera()
            _cameraEnabled.value = false
        } catch (e: RobotApiException) {
            _cameraEnabled.value = true
        }
    }

    /**
     * Send an instruction using the [RobotApi] to turn on the lidar.
     */
    override suspend fun sendStartLidar() = withContext(Dispatchers.IO) {
        try {
            robotApi.sendStartLidar()
            _lidarEnabled.value = true
        } catch (e: RobotApiException) {
            _lidarEnabled.value = false
        }
    }

    /**
     * Send an instruction using the [RobotApi] to turn off the lidar.
     */
    override suspend fun sendStopLidar() = withContext(Dispatchers.IO) {
        try {
            robotApi.sendStopLidar()
            _lidarEnabled.value = false
        } catch (e: RobotApiException) {
            _lidarEnabled.value = true
        }
    }

    /**
     * Send an instruction using the [RobotApi] to turn on the gesture recognition.
     */
    override suspend fun sendStartGesture() = withContext(Dispatchers.IO) {
        try {
            robotApi.sendStartGesture()
            _gestureEnabled.value = true
        } catch (e: RobotApiException) {
            _gestureEnabled.value = false
        }
    }

    /**
     * Send an instruction using the [RobotApi] to turn off the gesture recognition.
     */
    override suspend fun sendStopGesture() = withContext(Dispatchers.IO) {
        try {
            robotApi.sendStopGesture()
            _gestureEnabled.value = false
        } catch (e: RobotApiException) {
            _gestureEnabled.value = true
        }
    }
}