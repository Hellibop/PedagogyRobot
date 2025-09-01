package com.example.aida.data.local

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.aida.datastore.generated.AppPreferences
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

/**
 * A [Serializer] for persisting and restoring [AppPreferences] via ProtoBuf.
 *
 * This object provides:
 *  - A default value when no stored data exists.
 *  - Safe parsing of the binary stream into an [AppPreferences] instance.
 *  - Serialization of an [AppPreferences] instance back into binary form.
 */
object AppPreferencesSerializer : Serializer<AppPreferences>{
    /**
     * The default [AppPreferences] to use when no data has been saved yet.
     */
    override val defaultValue: AppPreferences = AppPreferences.getDefaultInstance()

    /**
     * Reads and parses an [AppPreferences] from the given [InputStream].
     *
     * @param input the raw byte stream containing the serialized proto
     * @return the parsed [AppPreferences]
     * @throws CorruptionException if the stream cannot be parsed as a valid proto
     */
    override suspend fun readFrom(input: InputStream): AppPreferences {
        try {
            return AppPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    /**
     * Serializes the given [AppPreferences] instance into the provided [OutputStream].
     *
     * @param t the preferences to write
     * @param output the destination stream for the serialized bytes
     */
    override suspend fun writeTo(t: AppPreferences, output: OutputStream) = t.writeTo(output)
}
