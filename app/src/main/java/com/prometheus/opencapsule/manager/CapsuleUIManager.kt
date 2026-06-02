package com.prometheus.opencapsule.manager

import android.content.Context
import com.prometheus.opencapsule.dataclass.CapsuleState
import com.prometheus.opencapsule.ui.CapsuleCard
import com.prometheus.opencapsule.util.VibrationManager
import com.prometheus.opencapsule.util.VolumeManager
import com.prometheus.opencapsule.util.isCapsuleServiceRunning
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CapsuleUIManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val volumeManager: VolumeManager,
    private val vibrationManager: VibrationManager
) {
    private val _isServiceEnabled = MutableStateFlow(isCapsuleServiceRunning(context))
    val isServiceEnabled: StateFlow<Boolean> = _isServiceEnabled.asStateFlow()
    
    private val _uiState = MutableStateFlow<CapsuleState>(CapsuleState.Collapsed)
    val uiState: StateFlow<CapsuleState> = _uiState.asStateFlow()

    private val availableCards = mutableListOf<CapsuleCard>()

    init {
        availableCards.add(com.prometheus.opencapsule.ui.cards.VolumeCard(volumeManager))
    }

    fun updateServiceStatus() {
        _isServiceEnabled.value = isCapsuleServiceRunning(context)
    }

    fun toggleExpansion() {
        vibrationManager.vibrateClick()
        if (_uiState.value is CapsuleState.Collapsed) {
            val sortedCards = availableCards.sortedByDescending { it.priority }
            if (sortedCards.isNotEmpty()) {
                _uiState.value = CapsuleState.Expanded(cards = sortedCards)
            }
        } else {
            _uiState.value = CapsuleState.Collapsed
        }
    }
}
