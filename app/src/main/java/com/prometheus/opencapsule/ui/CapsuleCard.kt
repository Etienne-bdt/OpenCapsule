package com.prometheus.opencapsule.ui

import androidx.compose.runtime.Composable

interface CapsuleCard {
    val id: String
    val priority: Int

    @Composable
    fun Content()
}