package com.prometheus.opencapsule.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.prometheus.opencapsule.util.isCapsuleServiceRunning
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CapsuleServiceViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _isServiceEnabled = MutableStateFlow(isCapsuleServiceRunning(context))
    val isServiceEnabled: StateFlow<Boolean> = _isServiceEnabled.asStateFlow()

    fun updateServiceStatus() {
        _isServiceEnabled.value = isCapsuleServiceRunning(context)
    }
}
