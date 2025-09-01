package com.example.aida.data.remote.network

import com.example.aida.domain.remote.ConnectionFailedException
import com.example.aida.domain.remote.ProtocolException
import com.example.aida.domain.remote.ReadTimeoutException
import com.example.aida.domain.remote.RobotApiException
import com.example.aida.domain.remote.RobotDisconnectedException
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException
import java.nio.ByteBuffer
import java.nio.ByteOrder


/**
 * Class representing a socket connection
 * @param ip Ip-address for the connection
 * @param hostPort Port for the connection
 * @param timeout Timeout given in ms
 */
class SocketConnection(
    private val ip: String,
    private val hostPort: Int,
    private val timeout: Int = 60000
) {
    private val socket: Socket

    // Connect on initialization
    init {
        val s = Socket()
        try {
            s.connect(InetSocketAddress(ip, hostPort), timeout)
            // s.soTimeout = 5000 // disabled read timeout.
        } catch (ste: SocketTimeoutException) {
            s.close()
            throw ConnectionFailedException(ip, hostPort, ste)
        } catch (ioe: IOException) {
            s.close()
            throw ConnectionFailedException(ip, hostPort, ioe)
        }
        socket = s
    }

    /**
     * Send data to output stream
     * @param data to be sent in [ByteArray]
     */
    fun send(data: ByteArray) {
        try {
            socket.getOutputStream().write(data)
        } catch (e: IOException) {
            throw RobotDisconnectedException()
        }
    }

    /**
     * Receive data from input stream
     * @param size of data to be received
     * @return data in form of [ByteArray]
     */
    fun receive(size: Int): ByteArray {
        val buffer = ByteArray(size)
        var bytesRead = 0

        try {
            while (bytesRead < size) {
                val result = socket.getInputStream().read(buffer, bytesRead, size - bytesRead)
                if (result < 0) {
                    throw RobotDisconnectedException()
                }
                bytesRead += result
            }
            return buffer

        } catch (ste: SocketTimeoutException) {
            throw ReadTimeoutException(ste)
        } catch (ioe: IOException) {
            throw RobotDisconnectedException()
        }
    }

    /**
     * Receive a header
     * @param size of data to be received
     * @return [Pair] of [Short] and [Int] representing id and size
     */
    fun getHeader(): Pair<Short, Int> {
        val headerBytes = try {
            receive(6)
        } catch (e: RobotApiException) {
            throw e
        }
        val buf = ByteBuffer.wrap(headerBytes).order(ByteOrder.BIG_ENDIAN)
        val id = buf.short
        val length = buf.int
        if (length < 0) {
            throw ProtocolException("Negative payload length: $length")
        }
        return id to length
    }

    /**
     * Close socket
     */
    fun close() {
        socket.close()
    }
}
