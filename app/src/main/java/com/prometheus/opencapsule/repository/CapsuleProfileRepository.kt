package com.prometheus.opencapsule.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.prometheus.opencapsule.dataclass.CapsuleProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class CapsuleProfileRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope
) {
    private companion object {
        val WIDTH = floatPreferencesKey("width")
        val HEIGHT = floatPreferencesKey("height")
        val POS_X = floatPreferencesKey("pos_x")
        val POS_Y = floatPreferencesKey("pos_y")
        val CORNER = floatPreferencesKey("corner")
        val ALPHA = floatPreferencesKey("alpha")
        val GUIDE = booleanPreferencesKey("guide")
        val COLOR = intPreferencesKey("color")
    }

    // In-memory state for instant updates (No Lag)
    private val _profile = MutableStateFlow(CapsuleProfile())
    val profileFlow: StateFlow<CapsuleProfile> = _profile.asStateFlow()

    init {
        // Load initial values from disk once
        scope.launch {
            val initial = dataStore.data
                .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
                .map { prefs ->
                    CapsuleProfile(
                        widthDp = prefs[WIDTH] ?: 126f,
                        heightDp = prefs[HEIGHT] ?: 37f,
                        positionXDp = prefs[POS_X] ?: 0f,
                        positionYDp = prefs[POS_Y] ?: 0f,
                        cornerRadiusDp = prefs[CORNER] ?: 50f,
                        alpha = prefs[ALPHA] ?: 1f,
                        isGuideEnabled = prefs[GUIDE] ?: false,
                        backgroundColor = prefs[COLOR] ?: 0xFF000000.toInt()
                    )
                }.first()
            _profile.value = initial
        }
    }

    // Fast Memory Updates
    fun updateWidth(value: Float) { _profile.update { it.copy(widthDp = value) } }
    fun updateHeight(value: Float) { _profile.update { it.copy(heightDp = value) } }
    fun updatePosX(value: Float) { _profile.update { it.copy(positionXDp = value) } }
    fun updatePosY(value: Float) { _profile.update { it.copy(positionYDp = value) } }
    fun updateCornerRadius(value: Float) { _profile.update { it.copy(cornerRadiusDp = value) } }
    fun updateAlpha(value: Float) { _profile.update { it.copy(alpha = value) } }
    fun updateGuide(enabled: Boolean) { _profile.update { it.copy(isGuideEnabled = enabled) } }
    fun updateColor(color: Int) { _profile.update { it.copy(backgroundColor = color) } }

    fun updateFullProfile(profile: CapsuleProfile) {
        _profile.value = profile
    }

    // Save to Disk (Only when called)
    suspend fun saveToDisk() {
        val p = _profile.value
        dataStore.edit { prefs ->
            prefs[WIDTH] = p.widthDp
            prefs[HEIGHT] = p.heightDp
            prefs[POS_X] = p.positionXDp
            prefs[POS_Y] = p.positionYDp
            prefs[CORNER] = p.cornerRadiusDp
            prefs[ALPHA] = p.alpha
            prefs[GUIDE] = p.isGuideEnabled
            prefs[COLOR] = p.backgroundColor
        }
    }
}
