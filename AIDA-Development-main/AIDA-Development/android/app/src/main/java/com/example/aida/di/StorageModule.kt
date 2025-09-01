package com.example.aida.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import com.example.aida.data.local.AppPreferencesSerializer
import com.example.aida.datastore.generated.AppPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing data storage related dependencies.
 * This module is specifically responsible for setting up and providing
 * the [DataStore] for [AppPreferences].
 */
@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    /**
     * The file name for the Proto DataStore that stores [AppPreferences].
     */
    private const val DATASTORE_FILE_NAME = "app_prefs.pb"

    /**
     * Provides a singleton instance of [AppPreferencesSerializer].
     * This serializer is used by DataStore to read and write [AppPreferences] objects.
     *
     * @return The [Serializer] for [AppPreferences].
     */
    @Singleton
    @Provides
    fun provideAppPreferencesSerializer(): Serializer<AppPreferences> =
        AppPreferencesSerializer

    /**
     * Provides a singleton instance of [DataStore] for [AppPreferences].
     * This DataStore is used to persist and retrieve application preferences.
     *
     * @param appContext The application context, used to determine the file path for the DataStore.
     * @param serializer The [AppPreferencesSerializer] used to serialize and deserialize [AppPreferences].
     * @return A [DataStore] instance for [AppPreferences].
     */
    @Singleton
    @Provides
    fun provideAppPreferencesDataStore(
        @ApplicationContext appContext: Context,
        serializer: Serializer<AppPreferences>
    ): DataStore<AppPreferences> {
        return DataStoreFactory.create(
            serializer = serializer,
            produceFile = { appContext.dataStoreFile(DATASTORE_FILE_NAME) },
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }
}