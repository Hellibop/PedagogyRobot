package com.example.aida.di

import com.example.aida.data.remote.RobotSocketApi
import com.example.aida.data.remote.network.SocketManagerImpl
import com.example.aida.domain.remote.RobotApi
import com.example.aida.domain.remote.SocketManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


/**
 * Dagger Hilt module for providing network-related dependencies.
 * This module is responsible for binding network interface implementations
 * to their respective interfaces, making them available for injection
 * throughout the application as singletons.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    /**
     * Binds the [SocketManagerImpl] implementation to the [SocketManager] interface.
     * This allows for injecting [SocketManager] where needed, with Hilt providing
     * the [SocketManagerImpl] instance.
     *
     * @param impl The concrete implementation of [SocketManager].
     * @return The [SocketManager] interface.
     */
    @Binds
    @Singleton
    abstract fun bindSocketManager(
        impl: SocketManagerImpl
    ): SocketManager

    /**
     * Binds the [RobotSocketApi] implementation to the [RobotApi] interface.
     * This allows for injecting [RobotApi] where needed, with Hilt providing
     * the [RobotSocketApi] instance.
     *
     * @param impl The concrete implementation of [RobotApi].
     * @return The [RobotApi] interface.
     */
    @Binds
    @Singleton
    abstract fun bindRobotApi(
        impl: RobotSocketApi
    ): RobotApi
}