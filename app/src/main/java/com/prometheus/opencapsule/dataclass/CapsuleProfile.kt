package com.prometheus.opencapsule.dataclass

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class CapsuleProfile (
    val widthDp: Float,
    val heightDp: Float,
    val positionXDp: Float,
    val positionYDp: Float,
    val cornerRadiusDp: Float,
    val alpha: Float,
)

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class CapsuleProfileRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val WIDTH_KEY = floatPreferencesKey("capsule_width")
        val HEIGHT_KEY = floatPreferencesKey("capsule_height")
    }

    // Read the width as a stream
    val widthFlow: Flow<Float> = dataStore.data.map { preferences ->
        preferences[WIDTH_KEY] ?: 126f // Default if empty
    }

    val heightFlow: Flow<Float> = dataStore.data.map { preferences ->
        preferences[HEIGHT_KEY] ?: 126f // Default if empty
    }


    // Write a new width
    suspend fun updateWidth(newWidth: Float) {
        dataStore.edit { preferences ->
            preferences[WIDTH_KEY] = newWidth
            preferences[HEIGHT_KEY] = newHeight
        }
    }
}
