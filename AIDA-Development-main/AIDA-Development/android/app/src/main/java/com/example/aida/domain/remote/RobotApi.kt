package com.example.aida.domain.remote

import androidx.compose.ui.graphics.ImageBitmap
import com.example.aida.domain.model.RobotAction

interface RobotApi {
    suspend fun connect(ip: String, port: Int)
    suspend fun disconnect()

    suspend fun sendJoystickData(x : Float, y : Float)
    suspend fun sendStartGesture()
    suspend fun sendStopGesture()

    suspend fun sendStartLidar()
    suspend fun sendStopLidar()
    suspend fun sendRequestLidarData()
    suspend fun receiveLidarData() : ImageBitmap?

    suspend fun sendSequence(actions: MutableList<RobotAction>)
    suspend fun sendStopSequence()

    suspend fun sendStartSTT()
    suspend fun sendStopSTT()
    suspend fun sendStartMic()
    suspend fun sendStopMic()
    suspend fun sendRequestSTTData()
    suspend fun receiveSTTData() : String

    suspend fun sendStartCamera()
    suspend fun sendStopCamera()
    suspend fun sendRequestVideo()
    suspend fun receiveVideoData() : ImageBitmap?
}