package com.prometheus.opencapsule.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prometheus.opencapsule.dataclass.CutoutInfo
import com.prometheus.opencapsule.util.CutoutDetector
import com.prometheus.opencapsule.viewmodel.CapsuleStyleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CapsuleStyleScreen(
    onBack: () -> Unit,
    viewModel: CapsuleStyleViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val view = LocalView.current
    val config = LocalConfiguration.current
    val screenWidthDp = config.screenWidthDp.toFloat()

    // Detect cutout
    val detectedCutout = remember(view.rootWindowInsets) {
        CutoutDetector.getCutoutInfo(context, view.rootWindowInsets?.displayCutout)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Style Capsule") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { 
                        viewModel.saveAndClose()
                        onBack()
                    }) {
                        Text("SAVE", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Auto-detect banner
            if (detectedCutout != null) {
                item {
                    AutoDetectBanner(
                        info = detectedCutout,
                        onApply = { viewModel.resetToAutoDetected(detectedCutout, screenWidthDp) }
                    )
                }
            }

            // Section A: Position & Size
            item {
                SectionHeader("Position & Size")
                
                // Width
                SliderSetting(
                    label = "Width",
                    value = profile.widthDp,
                    range = 20f..300f,
                    onValueChange = viewModel::changeWidth
                )

                // Height
                SliderSetting(
                    label = "Height",
                    value = profile.heightDp,
                    range = 20f..80f,
                    onValueChange = viewModel::changeHeight
                )

                // X Offset
                SliderSetting(
                    label = "X Offset",
                    value = profile.positionXDp,
                    range = -(screenWidthDp / 2).. (screenWidthDp / 2),
                    onValueChange = viewModel::changePositionX,
                    onReset = { viewModel.changePositionX(0f) }
                )

                // Y Offset
                SliderSetting(
                    label = "Y Offset",
                    value = profile.positionYDp,
                    range = 0f..120f,
                    onValueChange = viewModel::changePositionY,
                    onReset = { viewModel.changePositionY(0f) }
                )
            }

            // Section C: Shape
            item {
                SectionHeader("Shape")
                SliderSetting(
                    label = "Corner Radius",
                    value = profile.cornerRadiusDp,
                    range = 0f..50f,
                    onValueChange = viewModel::changeCornerRadius
                )
            }

            // Section D: Appearance
            item {
                SectionHeader("Appearance")
                
                // Opacity
                SliderSetting(
                    label = "Opacity",
                    value = profile.alpha,
                    range = 0.5f..1f,
                    onValueChange = viewModel::changeAlpha
                )

                // Color selector
                Text("Color", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ColorChip(Color.Black, "Black", profile.backgroundColor == 0xFF000000.toInt()) {
                        viewModel.changeColor(0xFF000000.toInt())
                    }
                    ColorChip(Color.DarkGray, "Dark Grey", profile.backgroundColor == 0xFF444444.toInt()) {
                        viewModel.changeColor(0xFF444444.toInt())
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Guide Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text("Show Camera Guide", modifier = Modifier.weight(1f))
                    Switch(
                        checked = profile.isGuideEnabled,
                        onCheckedChange = viewModel::toggleGuide
                    )
                }
            }
            
            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}

@Composable
fun ColorChip(color: Color, label: String, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = {
            Box(modifier = Modifier.size(16.dp).background(color, RoundedCornerShape(4.dp)))
        }
    )
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SliderSetting(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    onReset: (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "$label: ${value.toInt()}dp", style = MaterialTheme.typography.bodyMedium)
            if (onReset != null) {
                IconButton(onClick = onReset, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = MaterialTheme.colorScheme.outline)
                }
            }
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AutoDetectBanner(info: CutoutInfo, onApply: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Camera Found!", style = MaterialTheme.typography.titleSmall)
                Text(
                    "Detected at (${info.centerXDp.toInt()}, ${info.centerYDp.toInt()})",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Button(onClick = onApply) {
                Text("Apply")
            }
        }
    }
}
