package com.prometheus.opencapsule.util

import android.content.Context
import android.media.AudioManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VolumeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val _currVol = MutableStateFlow(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))
    val currVol: StateFlow<Int> = _currVol.asStateFlow()

    val maxVolume: Int = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    fun setVolume(volume: Int) {
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            volume.coerceIn(0, maxVolume),
            0 // 0 means don't show the system volume UI
        )
        _currVol.value = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    }

    /**
     * Updates the internal state. Call this if you detect volume changes 
     * from physical buttons elsewhere in the app/service.
     */
    fun updateVolume() {
        _currVol.value = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    }
}
