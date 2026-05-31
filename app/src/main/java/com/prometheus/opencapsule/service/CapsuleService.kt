package com.prometheus.opencapsule.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.prometheus.opencapsule.ui.CapsuleOverlay
import com.prometheus.opencapsule.util.addOrUpdate
import com.prometheus.opencapsule.util.getCapsuleWindowParams
import com.prometheus.opencapsule.util.safeRemoveView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CapsuleService : AccessibilityService(), LifecycleOwner, SavedStateRegistryOwner {

    private var composeView: ComposeView? = null
    private val windowManager by lazy { getSystemService(Context.WINDOW_SERVICE) as WindowManager }

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        
        showOverlay()
    }

    private fun showOverlay() {
        if (composeView != null) return

        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@CapsuleService)
            setViewTreeSavedStateRegistryOwner(this@CapsuleService)
            
            setContent {
                CapsuleOverlay()
            }
        }

        val params = getCapsuleWindowParams(this)
        addOrUpdate(windowManager, composeView!!, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        composeView?.let { safeRemoveView(windowManager, it) }
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
}
