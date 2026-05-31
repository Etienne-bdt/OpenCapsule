package com.prometheus.opencapsule.util

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.WindowInsets

fun getCapsuleWindowParams(context: Context): WindowManager.LayoutParams {
    val params = WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        format = PixelFormat.TRANSLUCENT
        flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        
        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        
        // Initial Y position - will be updated based on cutout if needed
        y = 0 
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
        // Fallback or log error
        try {
            wm.addView(view, params)
        } catch (e2: Exception) {
            // Already added or other error
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
