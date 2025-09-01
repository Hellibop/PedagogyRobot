package com.example.aida.domain.repository

import kotlinx.coroutines.flow.Flow

interface TopBarRepository {
    val micEnabled: Flow<Boolean>
    val cameraEnabled: Flow<Boolean>
    val gestureEnabled: Flow<Boolean>
    val lidarEnabled: Flow<Boolean>

    suspend fun enableDevices()
    suspend fun sendStartMic()
    suspend fun sendStopMic()
    suspend fun sendStartCamera()
    suspend fun sendStopCamera()
    suspend fun sendStartLidar()
    suspend fun sendStopLidar()
    suspend fun sendStartGesture()
    suspend fun sendStopGesture()
}