package com.example.aida.data.repository

import com.example.aida.domain.remote.ConnectionId
import com.example.aida.domain.remote.ConnectionState
import com.example.aida.domain.remote.SocketManager
import com.example.aida.domain.repository.ConnectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * The implementation of the ConnectionRepository. Holds functions to save settings (ip, port) and sequence of actions on the device.
 */

class ConnectionRepositoryImpl @Inject constructor(
    private val socketManager: SocketManager
): ConnectionRepository {

    /**
     * [StateFlow] of different [ConnectionState] on the connections to the robot.
     * @return [StateFlow] of Map<ConnectionId, ConnectionState>
     */
    override val connectionStates: StateFlow<Map<ConnectionId, ConnectionState>>
        get() = socketManager.connectionStates


    /**
     * [Flow] that emits true if any of the connections has a connected [ConnectionState]
     * or false once all connections are either Disconnected or Failed.
     */
    override val isConnected: Flow<Boolean> =
        connectionStates
            .map { states ->
                states.values.any { it is ConnectionState.Connected }
            }
            .distinctUntilChanged()
}