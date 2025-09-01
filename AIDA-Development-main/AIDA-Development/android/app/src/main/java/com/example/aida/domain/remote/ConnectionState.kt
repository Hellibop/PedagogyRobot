package com.example.aida.domain.remote

/**
 * Data objects for the different kind of connection states
 * Set by socket and then used by ViewModels to check on the connection health/state.
 */
sealed class ConnectionState {

    data object Disconnected : ConnectionState()

    data class Connecting(val ip: String, val port: Int) : ConnectionState()

    data class Connected(val ip: String, val port: Int) : ConnectionState()

    data class Failed(val ip: String, val port: Int, val cause: Throwable) : ConnectionState()
}