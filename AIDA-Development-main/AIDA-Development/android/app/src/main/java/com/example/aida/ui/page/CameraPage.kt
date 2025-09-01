package com.example.aida.ui.page

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import com.example.aida.domain.remote.ConnectionId
import com.example.aida.domain.remote.ConnectionState
import com.example.aida.ui.component.CameraFeed
import com.example.aida.ui.component.Joystick
import com.example.aida.ui.component.Lidar
import com.example.aida.ui.component.RecordVoiceButton
import com.example.aida.ui.component.VoiceCommandBox
import com.example.aida.ui.viewmodel.ManualControlViewModel

/**
 * The CameraPage composable displays the main interaction screen for controlling the robot.
 * It features a primary camera feed, a secondary Lidar view (which can be expanded),
 * a joystick for manual movement, a button for voice commands, and a display area
 * for transcribed voice commands.
 *
 * The Lidar feed currently uses placeholder data pending full implementation.
 *
 * @param viewModel An instance of [ManualControlViewModel] used to manage the state and
 *                  interactions of this page, such as joystick movements, voice recording,
 *                  and fetching video/Lidar frames.
 */
@OptIn(UnstableApi::class)
@Composable
fun CameraPage(
    viewModel: ManualControlViewModel = hiltViewModel()) {

    // Observe UI state, video frames, and Lidar frames from the ViewModel.
    val uiState by viewModel.uiState.collectAsState()
    val videoFrame by viewModel.videoFrame.collectAsState()
    val lidarFrame by viewModel.lidarFrame.collectAsState()

    // Get screen dimensions for responsive layout.
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    // Standard padding for widgets placed at the screen edges.
    val widgetPadding = 40.dp
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        var isLidarExpanded by remember { mutableStateOf(false) }
        CameraFeed(
            lidarIsExpanded = isLidarExpanded,
            screenWidth = screenWidth,
            imageBitmap = videoFrame,
            cameraFeedConnectionState = uiState
                .connectionStates[ConnectionId.VIDEO]
                ?: ConnectionState.Disconnected,
            ipAddress = uiState.ip,
            port = uiState.port
        )

        Lidar(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .zIndex(2f),
            screenWidth = screenWidth,
            screenHeight = screenHeight,
            isLidarExpanded = isLidarExpanded,
            lidarFrame = lidarFrame,
            onToggleLidar = { isLidarExpanded = !isLidarExpanded },
            lidarConnectionState = uiState
                .connectionStates[ConnectionId.LIDAR]
                ?: ConnectionState.Disconnected,
        )

        // Displays the transcribed text from voice commands (speech-to-text).
        // Positioned at the top-center of the screen, below the status bar area.
        VoiceCommandBox(
            modifier = Modifier
                .padding(top = 3.dp, bottom = screenHeight - screenHeight / 4)
                .align(Alignment.TopCenter)
                .zIndex(10f),
            screenWidth = screenWidth,
            isVoiceRecording = uiState.isRecording,
            voiceCommandString = uiState.sttText
        )

        // Joystick for manual robot control.
        Joystick(
            modifier = Modifier
                .padding(bottom = widgetPadding, start = widgetPadding)
                .align(Alignment.BottomStart)
                .zIndex(3f),
            joystickSize = 130F,
            thumbSize = 45f,
            enabled = (uiState.connectionStates[ConnectionId.JOYSTICK] is ConnectionState.Connected)
        ) { offset: Offset ->
            viewModel.onJoystickMove(offset.x, offset.y)
        }

        // Button to initiate voice recording on the robot, trigger STT(on-robot) after release.
        RecordVoiceButton(
            modifier = Modifier
                .padding(bottom = widgetPadding, end = widgetPadding)
                .zIndex(3f)
                .align(Alignment.BottomEnd),
            enabled = (uiState.connectionStates[ConnectionId.STT] is ConnectionState.Connected && uiState.isMicEnabled),
            onHold = { viewModel.onRecordButtonHold() },
            onRelease = { viewModel.onRecordButtonRelease() }
        )
    }
}
