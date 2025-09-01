import com.example.aida.data.remote.protocol.MessageType
import org.junit.Assert.assertEquals
import org.junit.Test

class MessageTypeTest {
    @Test
    fun testCameraValue(){
        val expected = 1.toShort()
        assertEquals(expected, MessageType.CAMERA.id)
    }
    @Test
    fun testImageAnalysisValue(){
        val expected = 2.toShort()
        assertEquals(expected, MessageType.IMAGE_ANALYSIS.id)
    }
    @Test
    fun testMicValue(){
        val expected = 3.toShort()
        assertEquals(expected, MessageType.MIC.id)
    }
    @Test
    fun testSttValue(){
        val expected = 4.toShort()
        assertEquals(expected, MessageType.STT.id)
    }
    @Test
    fun testLidarValue(){
        val expected = 5.toShort()
        assertEquals(expected, MessageType.LIDAR.id)
    }
    @Test
    fun testReqVideoFeedValue(){
        val expected = 6.toShort()
        assertEquals(expected, MessageType.REQ_VIDEO_FEED.id)
    }
    @Test
    fun testReqSttValue(){
        val expected = 7.toShort()
        assertEquals(expected, MessageType.REQ_STT.id)
    }
    @Test
    fun testReqLidarValue(){
        val expected = 8.toShort()
        assertEquals(expected, MessageType.REQ_LIDAR.id)
    }
    @Test
    fun testTextValue(){
        val expected = 9.toShort()
        assertEquals(expected, MessageType.TEXT.id)
    }
    @Test
    fun testVideoFrameValue(){
        val expected = 10.toShort()
        assertEquals(expected, MessageType.VIDEO_FRAME.id)
    }
    @Test
    fun testLidarDataValue(){
        val expected = 11.toShort()
        assertEquals(expected, MessageType.LIDAR_DATA.id)
    }
    @Test
    fun testAudioValue(){
        val expected = 12.toShort()
        assertEquals(expected, MessageType.AUDIO.id)
    }
    @Test
    fun testJoystickValue(){
        val expected = 14.toShort()
        assertEquals(expected, MessageType.JOYSTICK.id)
    }
}