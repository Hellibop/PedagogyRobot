package com.example.aida.domain.remote

/**
 * Enum for the different types of connections available
 * Sent to the robot to relate a instruction to the correct functionality.
 */
enum class ConnectionId {
    LIDAR,
    VIDEO,
    STT,
    JOYSTICK,
    SEQUENCE,
    GESTURE
}