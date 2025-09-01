package com.example.aida.data.remote.network

import android.util.Log
import com.example.aida.domain.remote.ConnectionId
import com.example.aida.domain.remote.ConnectionState
import com.example.aida.domain.remote.RobotApiException
import com.example.aida.domain.remote.SocketManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

/**
 * The implementation of the SocketManager. Manages the socket connections to the robot (connecting/sending/receiving).
 */
class SocketManagerImpl @Inject constructor() : SocketManager {

    // internal flow of Connection states on the different connections
    private val _connectionStates: MutableStateFlow<Map<ConnectionId, ConnectionState>> =
        MutableStateFlow(ConnectionId.entries.associateWith { ConnectionState.Disconnected })

    /**
     * Exposed [StateFlow] of Map<[ConnectionId], [ConnectionState]>. Provides the [ConnectionState] of the robots connections.
     */
    override val connectionStates: StateFlow<Map<ConnectionId, ConnectionState>>
        get() = _connectionStates

    // Map of that holds socket connections of the different ConnectionIds
    private val connections = ConcurrentHashMap<ConnectionId, SocketConnection>()

    // Helper function to get the socket connection based on ConnectionId
    private fun getConnection(socketType: ConnectionId): SocketConnection =
        connections[socketType]
            ?: throw IllegalStateException("No connection for ${socketType.name}")

    // Helper function to update the Connection State when a error occurs
    private fun updateStateOnError(socketType: ConnectionId, cause: Throwable) {
        when (val currentState = _connectionStates.value[socketType]) {
            is ConnectionState.Connecting -> {
                _connectionStates.update { map ->
                    map + (socketType to ConnectionState.Disconnected)
                }
            }
            is ConnectionState.Connected -> {
                _connectionStates.update { map ->
                    map + (socketType to ConnectionState.Failed(currentState.ip,currentState.port,cause))
                }
            }
            else -> return
        }
    }

    /**
     * Opens a connection to the robot based on [ConnectionId].
     * @param socketType to specify which [ConnectionId] to connect with
     * @param ip ip-address to use for the connection
     * @param port port to use for the connection
     * @param timeout times out the connection if unable to connect within the given value, default value is 60 seconds.
     */
    override fun connect(socketType: ConnectionId, ip: String, port: Int, timeout: Int) {

        // Emit Connecting state with ip and port we are trying to connect to
        _connectionStates.update { map ->
            map + (socketType to ConnectionState.Connecting(ip, port))
        }

        try {
            // Attempt to connect
            val connection = SocketConnection(ip, port, timeout)

            // If successful remove old connection and add the new one
            connections.remove(socketType)?.close()
            connections[socketType] = connection

            // Emit Connected state with ip and port
            _connectionStates.update { map ->
                map + (socketType to ConnectionState.Connected(ip, port))
            }

        } catch (e: RobotApiException) {
            updateStateOnError(socketType,e)
            throw e
        }
    }


    /**
     * Disconnect a connection to the robot based on [ConnectionId].
     * @param socketType to specify which [ConnectionId] to disconnect
     */
    override fun disconnect(socketType: ConnectionId) {
        // Attempt to remove connection
        val removedConnection = connections.remove(socketType)

        // If a connection was removed -> close it and emit Disconnected state
        if (removedConnection != null) {
            removedConnection.close()
            _connectionStates.update { map ->
                map + (socketType to ConnectionState.Disconnected)
            }
        }
    }

    /**
     * Send data to [ConnectionId].
     * @param socketType to specify which [ConnectionId] to send data to.
     * @param data the [ByteArray] to send
     */
    override fun send(socketType: ConnectionId, data: ByteArray) {
        val connection = getConnection(socketType)
        try {
            connection.send(data)
        } catch (e: RobotApiException) {
            updateStateOnError(socketType,e)
            throw e
        }
    }

    /**
     * Receive data to from connection.
     * @param socketType to specify which [ConnectionId] to receive from.
     * @param size of the data to be received
     * @return [ByteArray] of data.
     */
    override fun receive(socketType: ConnectionId, size : Int): ByteArray {
        val connection = getConnection(socketType)
        try {
            return connection.receive(size)
        } catch (e: Throwable) {
            updateStateOnError(socketType,e)
            throw e
        }
    }

    /**
     * Receives a header from specific Connection. Must be used in conjunction with a request call.
     * @param socketType to specify which [ConnectionId] to receive from.
     * @return [Pair] of [Short] and [Int] representing id and size
     */
    override fun getHeader(socketType: ConnectionId): Pair<Short, Int> {
        val connection = getConnection(socketType)
        try {
            return connection.getHeader()
        } catch (e: RobotApiException) {
            updateStateOnError(socketType,e)
            throw e
        }
    }

    /**
     * Closes all connections.
     */
    override fun closeConnections() {
        connections.values.forEach { it.close() }
        connections.clear()
    }
}
