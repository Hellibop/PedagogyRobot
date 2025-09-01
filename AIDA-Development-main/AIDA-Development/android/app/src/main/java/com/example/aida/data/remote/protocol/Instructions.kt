package com.example.aida.data.remote.protocol
/**
 * Enum class for the different instructions that can be sent to the server.
 */
enum class Instructions(val value: Short) {
    ON(1),
    OFF(2),
    GESTURE(3),
    POSE(4)
}