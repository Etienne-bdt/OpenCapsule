package com.prometheus.opencapsule.util

import android.app.ActivityManager
import android.content.Context

fun isCapsuleServiceRunning(context: Context): Boolean {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    @Suppress("DEPRECATION")
    for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
        if ("com.prometheus.opencapsule.service.CapsuleService" == service.service.className) {
            return true
        }
    }
    return false
}
