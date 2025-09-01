package com.example.aida.data.repository

import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import com.example.aida.domain.remote.RobotApi
import com.example.aida.domain.remote.RobotApiException
import com.example.aida.domain.repository.ManualControlRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


/**
 * The implementation of the manual control repository. Holds function for fetching video/lidar/stt data and sending joystick data.
 */
class ManualControlRepositoryImpl @Inject constructor(
    private val robotApi: RobotApi
): ManualControlRepository{

    private var videoJob: Job? = null
    private var lidarJob: Job? = null
    private val _videoFrames = MutableStateFlow<ImageBitmap?>(null)
    private val _lidarFrames = MutableStateFlow<ImageBitmap?>(null)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * [StateFlow] of video frames in the form of [ImageBitmap] or null
     */
    override val videoFrameFlow: StateFlow<ImageBitmap?> = _videoFrames

    /**
     * [StateFlow] of lidar frames in the form of [ImageBitmap] or null
     */
    override val lidarFrameFlow: StateFlow<ImageBitmap?> = _lidarFrames

    /**
     * Starts video streaming
     */
    override fun startVideo() {
        // early return if job is active
        if (videoJob?.isActive == true) return

        videoJob =  scope.launch {
            try {
                delay(1000)
                robotApi.sendRequestVideo()
                while (isActive) {
                    val frame = robotApi.receiveVideoData()
                    _videoFrames.value = frame
                }
            } catch (e: RobotApiException) {
                Log.d("ManualControlRepository", "Video streaming error:${e.message}")
            }
        }
    }

    /**
     * Stops video streaming
     */
    override fun stopVideo() {
        videoJob?.cancel()
        videoJob = null
    }

    /**
     * Starts lidar streaming
     */
    override fun startLidar() {
        // early return if job is active
        if (lidarJob?.isActive == true) return

        lidarJob =  scope.launch {
            try {
                robotApi.sendRequestLidarData()
                while (isActive) {
                    val frame = robotApi.receiveLidarData()
                    _lidarFrames.value = frame
                }
            } catch (e: RobotApiException) {
                Log.d("ManualControlRepository", "Lidar streaming error:${e.message}")
                stopLidar()
            }
        }
    }

    /**
     * Stops lidar streaming
     */
    override fun stopLidar() {
        lidarJob?.cancel()
        lidarJob = null
    }

    /**
     * Sends instruction to the robot to start transcribing STT
     */
    override suspend fun sendStartSTT() = withContext(Dispatchers.IO) {
        robotApi.sendStartSTT() // Unsure if the robot can handle this instruction
    }

    /**
     * !!UNSURE OF THIS IMPLEMENTATION!!
     * [Flow] of STT data from the robot.
     * @return [Flow] of [String]
     */
    override fun getSTTDataFlow(): Flow<String> = flow {
        robotApi.sendStopSTT()
        robotApi.sendRequestSTTData()

        // In the previous MainViewModel STTData is only received once,
        // Should it not be a loop instead to get multiple messages?
        while (currentCoroutineContext().isActive) {
            val sttData: String = robotApi.receiveSTTData()
            emit(sttData)
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Sends joystick data to the server with a 1 sec delay between sends.
     */
    override suspend fun sendJoystickData(x: Float, y: Float) = withContext(Dispatchers.IO) {
        robotApi.sendJoystickData(x, y)
        delay(1000) // Delay should possibly be tweaked depending on robot
    }
}