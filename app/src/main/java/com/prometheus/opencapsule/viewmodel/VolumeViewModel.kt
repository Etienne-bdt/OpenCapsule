package com.prometheus.opencapsule.viewmodel

import androidx.lifecycle.ViewModel
import com.prometheus.opencapsule.util.VolumeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class VolumeViewModel @Inject constructor(
    private val volumeManager: VolumeManager
) : ViewModel() {

    val currVol: StateFlow<Int> = volumeManager.currVol
    val maxVolume: Int = volumeManager.maxVolume

    fun setVolume(volume: Int) {
        volumeManager.setVolume(volume)
    }

    fun updateVolume() {
        volumeManager.updateVolume()
    }
}
