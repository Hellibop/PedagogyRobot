package com.example.aida.di

import com.example.aida.data.repository.AppPrefsRepositoryImpl
import com.example.aida.data.repository.ConnectionRepositoryImpl
import com.example.aida.data.repository.ManualControlRepositoryImpl
import com.example.aida.data.repository.SequenceRepositoryImpl
import com.example.aida.data.repository.TopBarRepositoryImpl
import com.example.aida.domain.repository.AppPrefsRepository
import com.example.aida.domain.repository.ConnectionRepository
import com.example.aida.domain.repository.ManualControlRepository
import com.example.aida.domain.repository.SequenceRepository
import com.example.aida.domain.repository.TopBarRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing repository dependencies.
 * This module is responsible for binding repository implementations
 * to their respective interfaces, making them available for injection
 * throughout the application as singletons.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds the [ConnectionRepositoryImpl] implementation to the [ConnectionRepository] interface.
     * This allows for injecting [ConnectionRepository] where needed, with Hilt providing
     * the [ConnectionRepositoryImpl] instance.
     *
     * @param impl The concrete implementation of [ConnectionRepository].
     * @return The [ConnectionRepository] interface.
     */
    @Binds
    @Singleton
    abstract fun bindTopConnectionRepository(
        impl: ConnectionRepositoryImpl
    ): ConnectionRepository

    /**
     * Binds the [TopBarRepositoryImpl] implementation to the [TopBarRepository] interface.
     * This allows for injecting [TopBarRepository] where needed, with Hilt providing
     * the [TopBarRepositoryImpl] instance.
     *
     * @param impl The concrete implementation of [TopBarRepository].
     * @return The [TopBarRepository] interface.
     */
    @Binds
    @Singleton
    abstract fun bindTopBarRepository(
        impl: TopBarRepositoryImpl
    ): TopBarRepository

    /**
     * Binds the [ManualControlRepositoryImpl] implementation to the [ManualControlRepository] interface.
     * This allows for injecting [ManualControlRepository] where needed, with Hilt providing
     * the [ManualControlRepositoryImpl] instance.
     *
     * @param impl The concrete implementation of [ManualControlRepository].
     * @return The [ManualControlRepository] interface.
     */
    @Binds
    @Singleton
    abstract fun bindManualControlRepository(
        impl: ManualControlRepositoryImpl
    ): ManualControlRepository

    /**
     * Binds the [SequenceRepositoryImpl] implementation to the [SequenceRepository] interface.
     * This allows for injecting [SequenceRepository] where needed, with Hilt providing
     * the [SequenceRepositoryImpl] instance.
     *
     * @param impl The concrete implementation of [SequenceRepository].
     * @return The [SequenceRepository] interface.
     */
    @Binds
    @Singleton
    abstract fun bindSequenceRepository(
        impl: SequenceRepositoryImpl
    ): SequenceRepository

    /**
     * Binds the [AppPrefsRepositoryImpl] implementation to the [AppPrefsRepository] interface.
     * This allows for injecting [AppPrefsRepository] where needed, with Hilt providing
     * the [AppPrefsRepositoryImpl] instance.
     *
     * @param impl The concrete implementation of [AppPrefsRepository].
     * @return The [AppPrefsRepository] interface.
     */
    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: AppPrefsRepositoryImpl
    ): AppPrefsRepository
}