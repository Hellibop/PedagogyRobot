package com.example.aida.data.remote

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.aida.data.remote.protocol.Instructions
import com.example.aida.data.remote.protocol.MessageType
import com.example.aida.domain.model.RobotAction
import com.example.aida.domain.remote.ConnectionId
import com.example.aida.domain.remote.RobotApi
import com.example.aida.domain.remote.RobotApiException
import com.example.aida.domain.remote.SocketManager
import com.example.aida.domain.repository.AppPrefsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

/**
 * The implementation of the RobotAPi. Implements the functionality for communicating with the robot.
 */

class RobotSocketApi @Inject constructor(
    private val settingsRepository: AppPrefsRepository,
    private val socketManger: SocketManager
): RobotApi {

    /**
     * Try to connect on initialization using stored settings
     */
    init {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            val settings = settingsRepository.currentSettings.first()
            connect(settings.ip, settings.port)
        }
    }

    /**
     * Connect to robot using entries in domain/ConnectionId and new ip and port.
     * @param ip new ip-address
     * @param port new port
     */
    override suspend fun connect(ip: String, port: Int) {
        coroutineScope {
            val connectionJobs = ConnectionId.entries.map { connectionId ->
                async(Dispatchers.IO) {
                    try {
                         socketManger.connect(connectionId, ip, port, 60000)
                    } catch (e: RobotApiException) {
                        Log.d("RobotSocketApi","Failed to connect: ${e.message}")
                    }
                }
            }
            connectionJobs.awaitAll()
        }
    }

    /**
     * Disconnect all connections to the robot.
     */
    override suspend fun disconnect() {
        coroutineScope {
            val disconnectJobs = ConnectionId.entries.map { connectionId ->
                async(Dispatchers.IO) {
                    try {
                        socketManger.disconnect(connectionId)
                    } catch (e: RobotApiException) {
                        Log.d("RobotSocketApi","Failed to disconnect: ${e.message}")
                    }
                }
            }
            disconnectJobs.awaitAll()
        }
    }

    /**
     * Function to create a Instruction body
     * @param id Id explains to server the what type of message @see com.example.aida.data.remote.protocol.MessageType
     * @param instruction The instruction to be sent to the server
     */
    private fun createInstructionBody(id: Short, instruction: Short): ByteArray {
        val buffer = ByteBuffer.allocate(2)
            .putShort(instruction)
            .array()

        return buffer
    }

    /**
     * Function to create a header
     * @param id Id explains to server the what type of message @see com.example.aida.data.remote.protocol.MessageType
     * @param dataSize size of the payload
     */
    private fun createHeader(id: Short, dataSize: Int): ByteArray {
        val header = ByteBuffer.allocate(6)
            .order(ByteOrder.BIG_ENDIAN)
            .putShort(id)
            .putInt(dataSize)
            .order(ByteOrder.BIG_ENDIAN)
            .array()

        return header
    }

    /**
     * Function to send instruction to the server
     * @param id Id explains to server the what type of message @see com.example.aida.data.remote.protocol.MessageType
     * @param instruction The instruction to be sent to the server
     */
    private fun sendInstruction(id: Short, instruction: Short, socketType: ConnectionId) {
        val body = createInstructionBody(id, instruction)
        val header = createHeader(id, body.size)

        socketManger.send(socketType, header)
        socketManger.send(socketType, body)
    }

    /**
     * Function to send request to the server
     * @param id Id explains to server the what type of message @see com.example.aida.data.remote.protocol.MessageType
     * @param socketType The socket used to send the request
     */
    private fun sendRequest(id: Short, socketType: ConnectionId) {
        val body = ByteBuffer.allocate(0)
            .array()
        val header = createHeader(id, body.size)

        socketManger.send(socketType, header)
        socketManger.send(socketType, body)
    }

    /**
     * Function to send request to the server
     * @param socketType The socket used to receive data
     */
    private fun receiveData(socketType: ConnectionId): ByteArray {
        val (id, size) = socketManger.getHeader(socketType)
        return socketManger.receive(socketType, size)
    }

    /**
     * Send joystick data to server
     * @param x The x value of the joystick - float value between -1 and 1
     * @param y The y value of the joystick - float value between -1 and 1
     */
    override suspend fun sendJoystickData(x : Float, y : Float) {

        if(x < -1 || x > 1 || y < -1 || y > 1){
            throw IllegalArgumentException("Joystick values must be between -1 and 1")
        }

        val body = ByteBuffer.allocate(MessageType.JOYSTICK.size)
            .putFloat(x)
            .putFloat(y)
            .array()

        val header = createHeader(MessageType.JOYSTICK.id, body.size)

        socketManger.send(ConnectionId.JOYSTICK, header)
        socketManger.send(ConnectionId.JOYSTICK, body)
    }

    /**
     * What we send to AIDA (message body):
     *
     * First we prepare a number (short) that represents the ON/OFF signal.
     * If ON (1), we have sent 1 or more actions for AIDA to immediately execute.
     * If OFF (2), we have sent a simple stop signal that is supposed to tell AIDA
     * to stop executing any actions right now.
     *
     *
     * After that, for each action in the list we prepare the following:
     *
     * First an ID (short) representing the action.
     * If it was a move action, that's it. We send the next action ID.
     *
     * If it was a special action however, we send a few extras.
     * First we also add a size (short) for the following data (string in UTF8 format) that is sent.
     * After that we lastly add the special action data (string in UTF8).
     * Then we are done with the special action and prepare the next action ID.
     * Repeat until entire list is prepared and then send to server.
     */
    /**
     * Send data containing a list of actions to execute to server
     * @param actions The list of actions to be sent to AIDA for execution
     */
    override suspend fun sendSequence(actions: MutableList<RobotAction>) {
        var size = 2 // ON/OFF signal takes 2 bytes (Int)
        for (action in actions) {
            size += MessageType.SEQUENCE.size // Int is 2 bytes and we need one short per action for ID
            if (action.type.isSpecial) {
                val utf8DataSize = action.data.toByteArray().size
                size += 2 // 2 byte short allocated for saying how long the incoming string in UTF8 containing special action data is
                size += utf8DataSize.toShort()
            }
        }
        val buffer = ByteBuffer.allocate(size)
            .putShort(Instructions.ON.value) // Add on signal

        for (action in actions) {
            buffer.putShort(action.type.id)

            if(action.type.isSpecial) {
                buffer.put(action.data.toByteArray())
            }
        }
        val body = buffer.array()
        val header = createHeader(MessageType.SEQUENCE.id, body.size)

        socketManger.send(ConnectionId.SEQUENCE, header)
        socketManger.send(ConnectionId.SEQUENCE, body)
    }

    /**
     * Send a signal to AIDA that tells it to stop executing any currently running actions
     */
    override suspend fun sendStopSequence() {
        sendInstruction(MessageType.SEQUENCE.id, Instructions.OFF.value, ConnectionId.SEQUENCE)
    }

    /**
     * Sends a request to start the camera to the server
     */
    override suspend fun sendStartCamera() {
        sendInstruction(MessageType.CAMERA.id, Instructions.ON.value, ConnectionId.VIDEO)
    }

    /**
     * Sends a request to stop the camera feed to the server
     */
    override suspend fun sendStopCamera() {
        sendInstruction(MessageType.CAMERA.id, Instructions.OFF.value, ConnectionId.VIDEO)
    }

    /**
     * Sends a request to server to start sending video data
     */
    override suspend fun sendRequestVideo() {
        sendRequest(MessageType.REQ_VIDEO_FEED.id, ConnectionId.VIDEO)
    }

    /**
     * Receives video data from server and converts to Image
     * @return [ImageBitmap] of the lidar data or null
     */
    override suspend fun receiveVideoData(): ImageBitmap? {
        val data = receiveData(ConnectionId.VIDEO)

        return BitmapFactory.decodeByteArray(data, 0, data.size)
            ?.asImageBitmap()
    }

    /**
     * Sends a request to start the image analysis to the server
     */
    override suspend fun sendStartGesture() {
        sendInstruction(MessageType.IMAGE_ANALYSIS.id, Instructions.GESTURE.value, ConnectionId.GESTURE)
    }

    /**
     * Sends a request to stop the camera feed to the server
     */
    override suspend fun sendStopGesture() {
        sendInstruction(MessageType.IMAGE_ANALYSIS.id, Instructions.OFF.value, ConnectionId.GESTURE)
    }

    /**
     * Send message to server requesting to turn on Lidar
     */
    override suspend fun sendStartLidar() {
        sendInstruction(MessageType.LIDAR.id, Instructions.ON.value, ConnectionId.LIDAR)
    }

    /**
     * Send message to server requesting to turn off Lidar
     */
    override suspend fun sendStopLidar() {
        sendInstruction(MessageType.LIDAR.id, Instructions.OFF.value, ConnectionId.LIDAR)
    }

    /**
     * Send request to server to start sending Lidar data
     */
    override suspend fun sendRequestLidarData() {
        sendRequest(MessageType.REQ_LIDAR.id, ConnectionId.LIDAR)
    }

    /**
     * Receives lidar data from server and converts to Image
     * @return [ImageBitmap] of the video data or null
     */
    override suspend fun receiveLidarData() : ImageBitmap?{
        val data = receiveData(ConnectionId.LIDAR)

        return BitmapFactory.decodeByteArray(data, 0, data.size)
            ?.asImageBitmap()
    }

    /**
     * Send message to server requesting to turn on microphone
     */
    override suspend fun sendStartMic() {
        sendInstruction(MessageType.MIC.id, Instructions.ON.value, ConnectionId.STT)
    }

    /**
     * Send message to server requesting to stop microphone
     */
    override suspend fun sendStopMic() {
        sendInstruction(MessageType.MIC.id, Instructions.OFF.value, ConnectionId.STT)
    }

    /**
     * Send message to server requesting to turn on STT-transcription
     */
    override suspend fun sendStartSTT() {
        sendInstruction(MessageType.STT.id, Instructions.ON.value, ConnectionId.STT)
    }

    /**
     * Send message to server requesting to stop STT-transcription
     */
    override suspend fun sendStopSTT() {
        sendInstruction(MessageType.STT.id, Instructions.OFF.value, ConnectionId.STT)
    }

    /**
     * Send request to server to start sending STT data
     */
    override suspend fun sendRequestSTTData() {
        sendRequest(MessageType.REQ_STT.id, ConnectionId.STT)
    }

    /**
     * Receives STT data from server and converts to string
     * @return [String] of the STT data
     */
    override suspend fun receiveSTTData() : String {
        val data = receiveData(ConnectionId.STT)

        return data.toString(Charsets.UTF_8)
    }
}
