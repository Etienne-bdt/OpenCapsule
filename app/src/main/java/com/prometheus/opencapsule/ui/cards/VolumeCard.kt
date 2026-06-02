package com.prometheus.opencapsule.ui.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.prometheus.opencapsule.ui.CapsuleCard
import com.prometheus.opencapsule.util.VolumeManager

class VolumeCard(private val volumeManager: VolumeManager) : CapsuleCard {
    override val id: String = "volume"
    override val priority: Int = 10

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val volume by volumeManager.currVol.collectAsState()
        val maxVolume = volumeManager.maxVolume.toFloat()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Slider(
                value = volume.toFloat(),
                onValueChange = { volumeManager.setVolume(it.toInt()) },
                valueRange = 0f..maxVolume,
                modifier = Modifier.fillMaxSize(),
                thumb = { }, // Hide default thumb
                track = { sliderState ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(32.dp))
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f))
                    ) {
                        // Progress Fill (The actual slider bar)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = if (maxVolume > 0) sliderState.value / maxVolume else 0f)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        
                        // Content inside the bar (Android 16 style)
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val isFilled = if (maxVolume > 0) (sliderState.value / maxVolume) > 0.15f else false
                            Icon(
                                imageVector = if (volume == 0) Icons.Default.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = if (isFilled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Text(
                                text = "Media",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isFilled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                            )
                        }
                    }
                }
            )
        }
    }
}
