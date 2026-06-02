package com.prometheus.opencapsule.util

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.prometheus.opencapsule.dataclass.CapsuleProfile
import com.prometheus.opencapsule.dataclass.CapsuleState

fun getCapsuleWindowParams(
    context: Context,
    profile: CapsuleProfile,
    uiState: CapsuleState = CapsuleState.Collapsed
): WindowManager.LayoutParams {
    val density = context.resources.displayMetrics.density
    val params = WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        format = PixelFormat.TRANSLUCENT
        flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        
        width = if (uiState is CapsuleState.Expanded) {
            flags = flags or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
            (380 * density).toInt()
        } else {
            (profile.widthDp * density).toInt()
        }

        height = WindowManager.LayoutParams.WRAP_CONTENT
        gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        
        x = (profile.positionXDp * density).toInt()
        
        // Match the center of the capsule with the center of the camera (positionYDp).
        // For WindowManager.LayoutParams.y with Gravity.TOP, we set the top edge of the window.
        // So, top = center - (height / 2).
        y = ((profile.positionYDp - (profile.heightDp / 2f)) * density).toInt()
    }
    return params
}

fun addOrUpdate(wm: WindowManager, view: View, params: WindowManager.LayoutParams) {
    try {
        if (view.isAttachedToWindow) {
            wm.updateViewLayout(view, params)
        } else {
            wm.addView(view, params)
        }
    } catch (e: Exception) {
        try {
            wm.addView(view, params)
        } catch (e2: Exception) {
            // Error handling
        }
    }
}

fun safeRemoveView(wm: WindowManager, view: View) {
    try {
        if (view.isAttachedToWindow) {
            wm.removeView(view)
        }
    } catch (e: IllegalArgumentException) {
        // View not attached
    }
}
