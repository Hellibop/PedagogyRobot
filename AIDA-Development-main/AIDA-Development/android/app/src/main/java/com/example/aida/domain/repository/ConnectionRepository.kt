package com.example.aida.domain.repository

import com.example.aida.domain.remote.ConnectionId
import com.example.aida.domain.remote.ConnectionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ConnectionRepository {
    val connectionStates: StateFlow<Map<ConnectionId, ConnectionState>>
    val isConnected: Flow<Boolean>
}