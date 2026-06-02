package com.prometheus.opencapsule.dataclass

import com.prometheus.opencapsule.ui.CapsuleCard

sealed class CapsuleState {
     object Collapsed : CapsuleState()
    data class Expanded(
        val cards: List<CapsuleCard>
    ) : CapsuleState()
 }