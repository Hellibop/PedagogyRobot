package com.example.aida.data.remote.protocol
/**
 * Enum class that contains all the different
 * message types that can be sent and received
 * from the server
 */
enum class MessageType(val id: Short, val size: Int) {
    CAMERA(1, 2),
    IMAGE_ANALYSIS(2, 2),
    MIC(3, 2),
    STT(4, 2),
    LIDAR(5,2),

    REQ_VIDEO_FEED(6, 0),
    REQ_STT(7, 2),
    REQ_LIDAR(8, 2),

    TEXT(9, 2),
    VIDEO_FRAME(10, 2),
    LIDAR_DATA(11, 2),
    AUDIO(12, 2),
    JOYSTICK(14, 8),
    SEQUENCE(15, 2),
    ACK(99, 2)
}