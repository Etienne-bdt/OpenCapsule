package com.prometheus.opencapsule.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.prometheus.opencapsule.dataclass.CapsuleProfile
import com.prometheus.opencapsule.dataclass.CapsuleState
import com.prometheus.opencapsule.dataclass.CutoutInfo

@Composable
fun CapsuleOverlay(
    profile: CapsuleProfile,
    uiState: CapsuleState,
    onCapsuleClick: () -> Unit,
    cutoutInfo: CutoutInfo? = null
) {
    Box(
        modifier = Modifier.wrapContentSize(),
        contentAlignment = Alignment.Center
    ) {
        // Yellow dotted guide (only if enabled AND we have cutout info)
        if (profile.isGuideEnabled && cutoutInfo != null) {
            Box(
                modifier = Modifier
                    .size(width = cutoutInfo.widthDp.dp, height = cutoutInfo.heightDp.dp)
                    .drawBehind {
                        drawRoundRect(
                            color = Color.Yellow,
                            style = Stroke(
                                width = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                        )
                    }
            )
        }

        // The actual capsule
        Box(
            modifier = Modifier
                .width(if (uiState is CapsuleState.Expanded) 380.dp else profile.widthDp.dp)
                .alpha(profile.alpha)
                .clip(RoundedCornerShape(profile.cornerRadiusDp.dp))
                .background(Color(profile.backgroundColor))
                .clickable { onCapsuleClick() }
                .animateContentSize(spring(stiffness = Spring.StiffnessMedium)),
            contentAlignment = if (uiState is CapsuleState.Expanded) Alignment.TopCenter else Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // The "Camera/Cutout" area. 
                // In Collapsed state, this is the entire capsule content, centered.
                // In Expanded state, this is at the top.
                Box(
                    modifier = Modifier
                        .width(profile.widthDp.dp)
                        .height(profile.heightDp.dp)
                )

                if (uiState is CapsuleState.Expanded) {
                    val pagerState = rememberPagerState(pageCount = { uiState.cards.size })
                    VerticalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp) // Fixed height for content area
                            .padding(horizontal = 8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) { page ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            uiState.cards[page].Content()
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp)) // Bottom balance
                }
            }
        }
    }
}
