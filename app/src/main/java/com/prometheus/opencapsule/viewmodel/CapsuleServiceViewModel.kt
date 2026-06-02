package com.prometheus.opencapsule.viewmodel

import androidx.lifecycle.ViewModel
import com.prometheus.opencapsule.manager.CapsuleUIManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CapsuleServiceViewModel @Inject constructor(
    private val uiManager: CapsuleUIManager
) : ViewModel() {

    val isServiceEnabled = uiManager.isServiceEnabled
    val uiState = uiManager.uiState

    fun updateServiceStatus() {
        uiManager.updateServiceStatus()
    }

    fun toggleExpansion() {
        uiManager.toggleExpansion()
    }
}
