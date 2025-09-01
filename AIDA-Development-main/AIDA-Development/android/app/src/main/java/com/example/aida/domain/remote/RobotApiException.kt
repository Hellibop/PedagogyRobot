package com.example.aida.domain.remote

import java.net.SocketTimeoutException

/**
 * Base class for RobotApi errors.
 */
sealed class RobotApiException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

/** Unable to connect. */
class ConnectionFailedException(ip: String, port: Int, cause: Throwable? = null)
    : RobotApiException("Cannot connect to robot at $ip:$port", cause)

/** Read timeout. */
class ReadTimeoutException(cause: SocketTimeoutException? = null)
    : RobotApiException("Connection timed out", cause)

/** Peer closed the connection unexpectedly. */
class RobotDisconnectedException
    : RobotApiException("Robot disconnected unexpectedly")

/** Unable to read structure of data */
class ProtocolException(detail: String, cause: Throwable? = null)
    : RobotApiException("Protocol error: $detail", cause)