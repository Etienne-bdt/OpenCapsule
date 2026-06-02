package com.prometheus.opencapsule.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prometheus.opencapsule.dataclass.CapsuleProfile
import com.prometheus.opencapsule.dataclass.CutoutInfo
import com.prometheus.opencapsule.repository.CapsuleProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CapsuleStyleViewModel @Inject constructor(
    private val repository: CapsuleProfileRepository
) : ViewModel() {

    // Observe the Repository's memory state
    val profile: StateFlow<CapsuleProfile> = repository.profileFlow

    private val _cutoutInfo = MutableStateFlow<CutoutInfo?>(null)
    val cutoutInfo: StateFlow<CutoutInfo?> = _cutoutInfo.asStateFlow()

    // These now call memory updates (Instant)
    fun changeWidth(dp: Float) = repository.updateWidth(dp)
    fun changeHeight(dp: Float) = repository.updateHeight(dp)
    fun changePositionX(dp: Float) = repository.updatePosX(dp)
    fun changePositionY(dp: Float) = repository.updatePosY(dp)
    fun changeCornerRadius(dp: Float) = repository.updateCornerRadius(dp)
    fun changeAlpha(value: Float) = repository.updateAlpha(value)
    fun toggleGuide(enabled: Boolean) = repository.updateGuide(enabled)
    fun changeColor(color: Int) = repository.updateColor(color)

    fun resetToAutoDetected(info: CutoutInfo, screenWidthDp: Float) {
        _cutoutInfo.value = info
        // Centering Math: (Camera Center) - (Screen Center) = Relative X Offset
        val offsetX = info.centerXDp - (screenWidthDp / 2)
        
        repository.updateFullProfile(
            profile.value.copy(
                widthDp = info.widthDp,
                heightDp = info.heightDp,
                positionXDp = offsetX,
                positionYDp = info.centerYDp
            )
        )
    }

    // Disk save happens here
    fun saveAndClose() {
        viewModelScope.launch {
            repository.saveToDisk()
        }
    }
}
