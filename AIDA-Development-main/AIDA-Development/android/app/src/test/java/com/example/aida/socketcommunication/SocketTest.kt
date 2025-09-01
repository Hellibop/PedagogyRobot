/*package com.example.aida.socketcommunication

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.North
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.South
import androidx.compose.material.icons.outlined.WavingHand
import androidx.compose.runtime.mutableStateOf
import com.example.aida.data.remote.protocol.Instructions
import com.example.aida.data.remote.protocol.MessageType
import com.example.aida.ui.composables.sequenceTabComposables.BaseAction
import com.example.aida.ui.composables.sequenceTabComposables.MoveAction
import com.example.aida.ui.composables.sequenceTabComposables.SpecialAction
import com.example.aida.ui.composables.sequenceTabComposables.inActiveColor
import com.example.aida.ui.composables.sequenceTabComposables.specialActionColor
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.Thread.sleep
import java.nio.ByteBuffer

class SocketTest {

    /**
     * Test that the header is received correctly in the STTClient
     */
    @Test
    fun testGetHeaderSTT(){
        val serverThread = Thread {
            val server = Server()
            server.receiveMessage()
            server.sendData(MessageType.STT.value, "Hello, World!".toByteArray())
            server.closeConnection()
        }
        serverThread.start()
        // Wait for the server to come online
        sleep(1000)
        val client = STTClient()
        client.sendStartSTT()
        val (id, size) = client.getHeader()
        assertEquals(MessageType.STT.value, id)
        assertEquals(13, size)
        client.stop()
        serverThread.join()
    }

    /**
     * Test that the header is received correctly in the LidarClient
     */
    @Test
    fun testGetHeaderLidar(){
        val serverThread = Thread {
            val server = Server()
            server.receiveMessage()
            server.receiveMessage()
            // TODO - Send a message with lidar data
            server.sendData(MessageType.LIDAR.value, "Hello, World!".toByteArray())
            server.closeConnection()
        }
        serverThread.start()
        // Wait for the server to come online
        sleep(1000)
        val client = LidarClient()
        client.sendStartLidar()
        client.sentRequestLidarData()
        val (id, size) = client.getHeader()
        assertEquals(MessageType.LIDAR.value, id)
        assertEquals(13, size)
        client.stop()
        serverThread.join()
        sleep(1000)
    }

    /**
     * Test that the header is received correctly in the VideoClient
     */
    @Test
    fun testGetHeaderVideo(){
        val serverThread = Thread {
            val server = Server()
            server.receiveMessage()
            server.receiveMessage()
            // TODO - Send a message with video data
            server.sendData(MessageType.CAMERA.value, "Hello, World!".toByteArray())
            server.closeConnection()
        }
        serverThread.start()
        // Wait for the server to come online
        sleep(1000)
        val client = VideoClient()
        client.sendStartCamera()
        client.sendGetVideo()
        val (id, size) = client.getHeader()
        assertEquals(MessageType.CAMERA.value, id)
        assertEquals(13, size)
        client.stop()
        serverThread.join()
        sleep(1000)
    }

    /**
     * Test that string communication works for STTClient
     */
    @Test
    fun testStringCommunication(){
        val serverThread = Thread {
            val server = Server()
            server.receiveMessage()
            server.sendData(MessageType.STT.value,"Hello, World!".toByteArray())
            server.closeConnection()
        }
        serverThread.start()
        // Wait for the server to come online
        sleep(1000)
        val client = STTClient()
        client.sendStartSTT()
        val receivedMessage = client.receiveSTTData()
        assertEquals("Hello, World!", receivedMessage)
        client.stop()
        serverThread.join()
        sleep(1000)
    }

    /**
     * Check that the STT client can receive an empty message
     */
    @Test
    fun testSendNoData(){
        val serverThread = Thread{
            val server = Server()
            server.receiveMessage()
            server.sendData(MessageType.STT.value,ByteArray(0))
            server.closeConnection()
        }
        serverThread.start()
        // Wait for the server to come online
        sleep(1000)
        val client = STTClient()
        client.sendStartSTT()
        val receivedMessage = client.receiveSTTData()
        assertEquals("", receivedMessage)
        client.stop()
        serverThread.join()
        sleep(1000)
    }

    /**
     * Check that the STT client can receive a message with a single character
     */
    @Test
    fun testSendSingleChar(){
        val serverThread = Thread{
            val server = Server()
            server.receiveMessage()
            server.sendData(MessageType.STT.value,"A".toByteArray())
            server.closeConnection()
        }
        serverThread.start()
        // Wait for the server to come online
        sleep(1000)
        val client = STTClient()
        client.sendStartSTT()
        val receivedMessage = client.receiveSTTData()
        assertEquals("A", receivedMessage)
        client.stop()
        serverThread.join()
        sleep(1000)
    }

    /**
     * Check that StartMic message is sent correctly
     */
    @Test
    fun testSendStartMic(){
        val serverThread = Thread{
            val server = Server()
            sleep(500)
            val (id, size) = server.getHeader()
            assertEquals(MessageType.MIC.value, id)
            assertEquals(2, size)
            val message = server.getBody(size)
            assertEquals(Instructions.ON.value, ByteBuffer.wrap(message).short)

            server.closeConnection()
        }
        serverThread.start()
        // Wait for the server to come online
        sleep(1000)
        val client = STTClient()
        client.sendStartMic()
        client.stop()
        serverThread.join()
        sleep(1000)
    }

    /**
     * Check that StopMic message is sent correctly
     */
    @Test
    fun testSendStopMic(){
        val serverThread = Thread{
            val server = Server()
            sleep(500)
            val (id, size) = server.getHeader()
            assertEquals(MessageType.MIC.value, id)
            assertEquals(2, size)
            val message = server.getBody(size)
            assertEquals(Instructions.OFF.value, ByteBuffer.wrap(message).short)

            server.closeConnection()
        }
        serverThread.start()
        // Wait for the server to come online
        sleep(1000)
        val client = STTClient()
        client.sendStopMic()
        client.stop()
        serverThread.join()
        sleep(1000)
    }

    /**
     * Check that StartSTT message is sent correctly
     */
    @Test
    fun testSendStartSTT(){
        val serverThread = Thread{
            val server = Server()
            sleep(500)
            val (id, size) = server.getHeader()
            val message = server.getBody(size)
            assertEquals(MessageType.STT.value, id)
            assertEquals(2, size)
            assertEquals(Instructions.ON.value, ByteBuffer.wrap(message).short)

            server.closeConnection()
        }
        serverThread.start()
        // Wait for the server to come online
        sleep(1000)
        val client = STTClient()
        client.sendStartSTT()
        client.stop()
        serverThread.join()
        sleep(1000)
    }

    /**
     * Check that stopSTT message is sent correctly
     */
    @Test
    fun testSendStopSTT(){
        val serverThread = Thread{
            val server = Server()
            sleep(500)
            val (id, size) = server.getHeader()
            assertEquals(MessageType.STT.value, id)
            assertEquals(2, size)
            val message = server.getBody(size)
            assertEquals(Instructions.OFF.value, ByteBuffer.wrap(message).short)

            server.closeConnection()
        }
        serverThread.start()
        // Wait for the server to come online
        sleep(1000)
        val client = STTClient()
        client.sendStopSTT()
        client.stop()
        serverThread.join()
        sleep(1000)
    }


    /**
     * Check that ReqSTT message is sent correctly
     */
    @Test
    fun testSendReqSTT(){
        val serverThread = Thread{
            val server = Server()
            sleep(500)
            val (id, size) = server.getHeader()
            assertEquals(MessageType.REQ_STT.value, id)
            assertEquals(0, size)
            server.closeConnection()
        }
        serverThread.start()
        // Wait for the server to come online
        sleep(1000)
        val client = STTClient()
        client.sendRequestSTTData()
        client.stop()
        serverThread.join()
        sleep(1000)
    }

    /**
     * Check that StartLidar message is sent correctly
     */
    @Test
    fun testSendStartLidar(){
        val serverThread = Thread{
            val server = Server()
            sleep(500)
            val (id, size) = server.getHeader()
            val message = server.getBody(size)
            assertEquals(MessageType.LIDAR.value, id)
            assertEquals(2, size)
            assertEquals(Instructions.ON.value, ByteBuffer.wrap(message).short)
            server.closeConnection()
        }
        serverThread.start()
        // Wait for the server to come online
        sleep(1000)
        val client = LidarClient()
        client.sendStartLidar()
        client.stop()
        serverThread.join()
        sleep(1000)
    }

    /**
     * Check that StopLidar message is sent correctly
     */
    @Test
    fun testSendStopLidar(){
        val serverThread = Thread{
            val server = Server()
            sleep(500)
            val (id, size) = server.getHeader()
            val message = server.getBody(size)
            assertEquals(MessageType.LIDAR.value, id)
            assertEquals(2, size)
            assertEquals(Instructions.OFF.value, ByteBuffer.wrap(message).short)
            server.closeConnection()
        }
        serverThread.start()
        // Wait for the server to come online
        sleep(1000)
        val client = LidarClient()
        client.sendStopLidar()
        client.stop()
        serverThread.join()
        sleep(1000)
    }

    /**
     * Check that ReqLidar message is sent correctly
     */
    @Test
    fun testSendReqLidar(){
        val serverThread = Thread{
            val server = Server()
            sleep(500)
            val (id, size) = server.getHeader()
            val message = server.getBody(size)
            assertEquals(MessageType.REQ_LIDAR.value, id)
            assertEquals(0, size)
            server.closeConnection()
        }
        serverThread.start()
        // Wait for the server to come online
        sleep(1000)
        val client = LidarClient()
        client.sentRequestLidarData()
        client.stop()
        serverThread.join()
        sleep(1000)
    }

    /**
     * Check that StartCamera message is sent correctly
     */
    @Test
    fun testSendStartCamera(){
        val serverThread = Thread{
            val server = Server()
            sleep(500)
            val (id, size) = server.getHeader()
            val message = server.getBody(size)
            assertEquals(MessageType.CAMERA.value, id)
            assertEquals(2, size)
            assertEquals(Instructions.ON.value, ByteBuffer.wrap(message).short)
            server.closeConnection()
        }
        serverThread.start()
        // Wait for the server to come online
        sleep(1000)
        val client = VideoClient()
        client.sendStartCamera()
        client.stop()
        serverThread.join()
        sleep(1000)
    }

    /**
     * Check that StopCamera message is sent correctly
     */
    @Test
    fun testSendStopCamera(){
        val serverThread = Thread{
            val server = Server()
            sleep(500)
            val (id, size) = server.getHeader()
            val message = server.getBody(size)
            assertEquals(MessageType.CAMERA.value, id)
            assertEquals(2, size)
            assertEquals(Instructions.OFF.value, ByteBuffer.wrap(message).short)
            server.closeConnection()
        }
        serverThread.start()
        // Wait for the server to come online
        sleep(1000)
        val client = VideoClient()
        client.sendStopCamera()
        client.stop()
        serverThread.join()
        sleep(1000)
    }

    /**
     * Check that ReqVideoFeed message is sent correctly
     */
    @Test
    fun testSendReqVideoFeed(){
        val serverThread = Thread{
            val server = Server()
            sleep(500)
            val (id, size) = server.getHeader()
            assertEquals(MessageType.REQ_VIDEO_FEED.value, id)
            assertEquals(0, size)
            server.closeConnection()
        }
        serverThread.start()
        // Wait for the server to come online
        sleep(1000)
        val client = VideoClient()
        client.sendGetVideo()
        client.stop()
        serverThread.join()
        sleep(1000)
    }

    /**
     * Test that joystick client gives the correct message
     */
    @Test
    fun testSendJoystick(){
        val serverThread = Thread{
            val server = Server()
            sleep(500)
            val (id, size) = server.getHeader()
            val message = server.getBody(size)
            assertEquals(MessageType.JOYSTICK.value, id)
            val messageBuffer = ByteBuffer.wrap(message).asFloatBuffer()
            assertEquals(8, size)
            assertEquals(1.0f, messageBuffer.get(0))
            assertEquals(-0.5f, messageBuffer.get(1))
            server.closeConnection()
        }
        serverThread.start()
        // Wait for the server to come online
        sleep(1000)
        val client = JoystickClient()
        client.sendJoystickData(1.0f, -0.5f)
        client.stop()
        serverThread.join()
        sleep(1000)
    }

    /**
     * Check that joystick throws an exception if the x value is too high
     */
    @Test
    fun testSendJoystickXTooHigh(){
        val serverThread = Thread{
            val server = Server()
            sleep(500)
            server.closeConnection()
        }
        serverThread.start()
        sleep(1000)
        val client = JoystickClient()
        try {
            client.sendJoystickData(1.1f, 0.0f)
            assertEquals("Joystick values must be between -1 and 1", "Worked")
        } catch (e : IllegalArgumentException){
            assertEquals("Joystick values must be between -1 and 1", e.message)
        }
        finally {
            client.stop()
            serverThread.join()
        }
    }

    /**
     * Check that joystick throws an exception if the x value is too low
     */

    @Test
    fun testSendJoystickXTooLow(){
        val serverThread = Thread{
            val server = Server()
            sleep(500)
            server.closeConnection()
        }
        serverThread.start()
        sleep(1000)
        val client = JoystickClient()
        try {
            client.sendJoystickData(-1.1f, 0.0f)
            assertEquals("Joystick values must be between -1 and 1", "Worked")
        } catch (e : IllegalArgumentException){
            assertEquals("Joystick values must be between -1 and 1", e.message)
        }
        finally {
            client.stop()
            serverThread.join()
        }
    }

    /**
     * Check that joystick throws an exception if the y value is too high
     */
    @Test
    fun testSendJoystickYTooHigh(){
        val serverThread = Thread{
            val server = Server()
            sleep(500)
            server.closeConnection()
        }
        serverThread.start()
        sleep(1000)
        val client = JoystickClient()
        try {
            client.sendJoystickData(0.0f, 1.1f)
            assertEquals("Joystick values must be between -1 and 1", "Worked")
        } catch (e : IllegalArgumentException){
            assertEquals("Joystick values must be between -1 and 1", e.message)
        }
        finally {
            client.stop()
            serverThread.join()
        }
    }

    /**
     * Check that joystick throws an exception if the y value is too low
     */
    @Test
    fun testSendJoystickYTooLow(){
        val serverThread = Thread{
            val server = Server()
            sleep(500)
            server.closeConnection()
        }
        serverThread.start()
        sleep(1000)
        val client = JoystickClient()
        try {
            client.sendJoystickData(0.0f, -1.1f)
            assertEquals("Joystick values must be between -1 and 1", "Worked")
        } catch (e : IllegalArgumentException){
            assertEquals("Joystick values must be between -1 and 1", e.message)
        }
        finally {
            client.stop()
            serverThread.join()
        }
    }

    /**
     * Test that sequence client correctly sends move actions
     */
    @Test
    fun testSendSequenceMoveActions(){
        val serverThread = Thread{
            val server = Server()
            sleep(500)
            val (id, size) = server.getHeader()
            println("Server received ID: $id")
            println("Server received size: $size")
            val message = server.getBody(size)
            assertEquals(MessageType.SEQUENCE.value, id)
            assertEquals(6, size.toInt())
            val messageBuffer = ByteBuffer.wrap(message).asShortBuffer()
            println("Server received message: $message")
            println("Server received sequence message with following ON/OFF signal: " + messageBuffer.get(0))
            println("Server received sequence message with following first action ID: " + messageBuffer.get(1))
            println("Server received sequence message with following second action ID: " + messageBuffer.get(2))
            assertEquals(1, messageBuffer.get(0).toInt())
            assertEquals(1, messageBuffer.get(1).toInt())
            assertEquals(10, messageBuffer.get(2).toInt())
            server.closeConnection()
        }
        serverThread.start()
        // Wait for the server to come online
        sleep(1000)
        val client = SequenceClient()
        val action1 = MoveAction(id = 1, name = "TURN LEFT", isReorderable = true, data = mutableStateOf(""), icon = mutableStateOf(Icons.Outlined.ArrowBack))
        val action2 = MoveAction(id = 1, name = "BACKWARDS LONG", isReorderable = true, data = mutableStateOf(""), icon = mutableStateOf(Icons.Outlined.South))
        val actionList = mutableListOf<BaseAction>(action1, action2)
        client.sendActionsToExecute(actionList)
        client.stop()
        serverThread.join()
        sleep(1000)
    }

    /**
     * Test that sequence client correctly sends a special action with data in it
     */
    @Test
    fun testSendSequenceSpecialAction(){
        val serverThread = Thread{
            val server = Server()
            sleep(500)
            val (id, size) = server.getHeader()
            println("Server received ID: $id")
            println("Server received size: $size")
            val message = server.getBody(size)
            assertEquals(MessageType.SEQUENCE.value, id)
            assertEquals(10, size.toInt())
            server.decodeSequenceMessage(message)
            assertEquals("test", message.decodeToString(6, 10))
            server.closeConnection()
        }
        serverThread.start()
        // Wait for the server to come online
        sleep(1000)
        val client = SequenceClient()
        val action = SpecialAction(id = 2, name = "INPUT VOICE", isReorderable = true, data = mutableStateOf("test"), icon = mutableStateOf(Icons.Outlined.Campaign), cardColor = mutableStateOf(inActiveColor))
        val actionList = mutableListOf<BaseAction>(action)
        client.sendActionsToExecute(actionList)
        client.stop()
        serverThread.join()
        sleep(1000)
    }

    /**
     * Test that sequence client correctly sends a message with both special actions and move actions
     */
    @Test
    fun testSendSequenceMultipleActions(){
        val serverThread = Thread{
            val server = Server()
            sleep(500)
            val (id, size) = server.getHeader()
            println("Server received ID: $id")
            println("Server received size: $size")
            val message = server.getBody(size)
            assertEquals(MessageType.SEQUENCE.value, id)
            assertEquals(64, size.toInt())
            server.decodeSequenceMessage(message)
            server.closeConnection()
        }
        serverThread.start()
        // Wait for the server to come online
        sleep(1000)
        val client = SequenceClient()
        val action1 = SpecialAction(id = 2, name = "INPUT VOICE", isReorderable = true, data = mutableStateOf("Hello how are you"), icon = mutableStateOf(Icons.Outlined.Campaign), cardColor = mutableStateOf(inActiveColor))
        val action2 = MoveAction(id = 1, name = "TURN RIGHT", isReorderable = true, data = mutableStateOf(""), icon = mutableStateOf(Icons.Outlined.ArrowForward))
        val action3 = SpecialAction(id = 2, name = "LOOP START", isActive = mutableStateOf(true), isReorderable = false, data = mutableStateOf("5"), icon = mutableStateOf(Icons.Outlined.Repeat), cardColor = mutableStateOf(specialActionColor))
        val action4 = SpecialAction(id = 2, name = "INPUT GESTURE", isReorderable = true, data = mutableStateOf("open_hand"), icon = mutableStateOf(Icons.Outlined.WavingHand), cardColor = mutableStateOf(inActiveColor))
        val action5 = MoveAction(id = 1, name = "FORWARD LONG", isReorderable = true, data = mutableStateOf(""), icon = mutableStateOf(Icons.Outlined.North))
        val action6 = SpecialAction(id = 2, name = "INPUT SOUND", isReorderable = true, data = mutableStateOf("eight_bit_laser"), icon = mutableStateOf(Icons.Outlined.MusicNote), cardColor = mutableStateOf(inActiveColor))
        val actionList = mutableListOf<BaseAction>(action1, action2, action3, action4, action5, action6)
        client.sendActionsToExecute(actionList)
        client.stop()
        serverThread.join()
        sleep(1000)
    }
}

 */