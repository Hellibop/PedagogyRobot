package com.example.aida.domain.repository

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ManualControlRepository {
    val videoFrameFlow: StateFlow<ImageBitmap?>
    val lidarFrameFlow: StateFlow<ImageBitmap?>

    fun startVideo()
    fun stopVideo()
    fun startLidar()
    fun stopLidar()
    fun getSTTDataFlow() : Flow<String>
    suspend fun sendStartSTT()
    suspend fun sendJoystickData(x: Float, y: Float)
}