package com.example.aida.domain.remote

import kotlinx.coroutines.flow.StateFlow

interface SocketManager {
    val connectionStates: StateFlow<Map<ConnectionId, ConnectionState>>

    fun connect(socketType: ConnectionId, ip: String, port: Int, timeout: Int)
    fun disconnect(socketType: ConnectionId)
    fun send(socketType: ConnectionId, data: ByteArray)
    fun receive(socketType: ConnectionId, size : Int): ByteArray
    fun getHeader(socketType: ConnectionId): Pair<Short, Int>
    fun closeConnections()
}