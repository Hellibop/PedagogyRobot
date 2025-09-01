package com.example.aida.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import com.example.aida.datastore.generated.AppPreferences
import com.example.aida.datastore.generated.RobotActionProto
import com.example.aida.datastore.generated.SettingsProto
import com.example.aida.domain.model.RobotAction
import com.example.aida.domain.model.RobotActionType
import com.example.aida.domain.model.Settings
import com.example.aida.domain.repository.AppPrefsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * The implementation of the AppPrefsRepository. Holds functions to save settings (ip, port) and sequence of actions on the device.
 */

class AppPrefsRepositoryImpl @Inject constructor(
    private val appPreferencesDataStore: DataStore<AppPreferences>
) : AppPrefsRepository{

    /**
     * Helper function to convert the [SettingsProto] model stored in the Datastore to domain model.
     * @return [Settings]
     */
    private fun SettingsProto.toDomain(): Settings {
        return Settings(ip = this.ip, port = this.port)
    }

    /**
     * Helper function to convert the domain model [Settings] to model used in Datastore.
     * @return [SettingsProto]
     */
    private fun Settings.toProto(): SettingsProto {
        return SettingsProto.newBuilder()
            .setIp(this.ip)
            .setPort(this.port)
            .build()
    }

    /**
     * Helper function to convert the [RobotActionProto] model stored in the Datastore to domain model.
     * @return [RobotAction]
     */
    private fun RobotActionProto.toDomain(): RobotAction {
        val actionType = RobotActionType.fromId(this.type.toShort())
        return RobotAction(type=actionType,data = this.data)
    }

    /**
     * Helper function to convert the domain [RobotAction] to model stored in Datastore.
     * @return [RobotActionProto]
     */
    private fun RobotAction.toProto(): RobotActionProto {
        return RobotActionProto.newBuilder()
            .setType(this.type.id.toInt())
            .setData(this.data)
            .build()
    }

    /**
     * Flow of [Settings] stored in Datastore. Defaults to ip: 172.20.10.10, port: 8888 if no settings have been saved.
     * @return [Flow]
     */
    override val currentSettings: Flow<Settings> = appPreferencesDataStore.data
        .catch { e -> if (e is IOException) emit(AppPreferences.getDefaultInstance())
        else throw e
        }
        .map { preferences ->
            if (preferences.hasCurrentSettings()) {
                preferences.currentSettings.toDomain()
            } else {
                Settings("192.168.50.195", 6662,)
            }
        }

    /**
     * Loads sequence of [RobotAction] stored in Datastore.
     * @return [List]
     */
    override suspend fun loadSavedSequence(): List<RobotAction> {
        val prefs = appPreferencesDataStore.data
            .catch { e ->
                if (e is IOException) emit(AppPreferences.getDefaultInstance())
                else throw e
            }
            .first()

        return prefs.sequenceList.map { it.toDomain() }
    }

    /**
     * Saves new [Settings] to the Datastore.
     * @param newSettings new [Settings] to be stored.
     */
    override suspend fun updateSettings(newSettings: Settings) {
        appPreferencesDataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .setCurrentSettings(newSettings.toProto())
                .build()
        }
    }

    /**
     * Saves sequence of [RobotAction] to the Datastore.
     * @param newSequence sequence to be stored.
     */
    override suspend fun saveSequence(newSequence: List<RobotAction>) {
        val protoSequence = newSequence.map { it.toProto() }
        appPreferencesDataStore.updateData { currentPrefs ->
            currentPrefs.toBuilder()
                .clearSequence()
                .addAllSequence(protoSequence)
                .build()
        }
    }
}