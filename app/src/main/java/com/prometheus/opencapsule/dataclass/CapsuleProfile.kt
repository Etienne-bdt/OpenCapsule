package com.prometheus.opencapsule.dataclass

data class CapsuleProfile(
    val widthDp: Float = 126f,
    val heightDp: Float = 37f,
    val positionXDp: Float = 0f,
    val positionYDp: Float = 0f,
    val cornerRadiusDp: Float = 50f,
    val alpha: Float = 1f,
    val isGuideEnabled: Boolean = false,
    val backgroundColor: Int = 0xFF000000.toInt(),
)
