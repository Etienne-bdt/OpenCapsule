package com.prometheus.opencapsule.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CapsuleOverlay() {
    Box(
        modifier = Modifier
            .width(37.dp)
            .height(37.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.Black)
    )
}
