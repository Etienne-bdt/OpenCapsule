package com.prometheus.opencapsule.util

import android.content.Context
import android.view.DisplayCutout
import com.prometheus.opencapsule.dataclass.CutoutInfo

object CutoutDetector {

    /**
     * Converts a raw Android DisplayCutout into our CutoutInfo data class.
     * Calculates centers and dimensions in DP.
     */
    fun getCutoutInfo(context: Context, displayCutout: DisplayCutout?): CutoutInfo? {
        if (displayCutout == null) return null
        
        val density = context.resources.displayMetrics.density

        val rects = displayCutout.boundingRects
        if (rects.isEmpty()) return null

        // Find the topmost cutout (usually the camera)
        val cameraRect = rects.minByOrNull { it.top } ?: return null

        return CutoutInfo(
            centerXDp = (cameraRect.left + cameraRect.right) / 2f / density,
            centerYDp = (cameraRect.top + cameraRect.bottom) / 2f / density,
            widthDp = (cameraRect.right - cameraRect.left).toFloat() / density,
            heightDp = (cameraRect.bottom - cameraRect.top).toFloat() / density
        )
    }
}
