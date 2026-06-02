package com.prometheus.opencapsule.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.prometheus.opencapsule.dataclass.CapsuleProfile
import com.prometheus.opencapsule.manager.CapsuleUIManager
import com.prometheus.opencapsule.repository.CapsuleProfileRepository
import com.prometheus.opencapsule.dataclass.CapsuleState
import com.prometheus.opencapsule.ui.CapsuleOverlay
import com.prometheus.opencapsule.util.addOrUpdate
import com.prometheus.opencapsule.util.getCapsuleWindowParams
import com.prometheus.opencapsule.util.safeRemoveView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CapsuleService : AccessibilityService(), LifecycleOwner, SavedStateRegistryOwner, ViewModelStoreOwner {

    @Inject
    lateinit var repository: CapsuleProfileRepository

    @Inject
    lateinit var uiManager: CapsuleUIManager

    private var composeView: ComposeView? = null
    private val windowManager by lazy { getSystemService(Context.WINDOW_SERVICE) as WindowManager }

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val _viewModelStore = ViewModelStore()

    override val lifecycle: Lifecycle = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry = savedStateRegistryController.savedStateRegistry
    override val viewModelStore: ViewModelStore = _viewModelStore

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        
        setupOverlay()
        observeState()
    }

    private fun setupOverlay() {
        if (composeView != null) return

        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@CapsuleService)
            setViewTreeSavedStateRegistryOwner(this@CapsuleService)
            setViewTreeViewModelStoreOwner(this@CapsuleService)
            
            setOnTouchListener { _, event ->
                if (event.action == android.view.MotionEvent.ACTION_OUTSIDE) {
                    if (uiManager.uiState.value is CapsuleState.Expanded) {
                        uiManager.toggleExpansion()
                        return@setOnTouchListener true
                    }
                }
                false
            }

            setContent {
                val profile by repository.profileFlow.collectAsState(initial = CapsuleProfile())
                val uiState by uiManager.uiState.collectAsState()
                
                val context = androidx.compose.ui.platform.LocalContext.current
                val view = androidx.compose.ui.platform.LocalView.current
                
                val cutoutInfo = androidx.compose.runtime.remember(view.rootWindowInsets) {
                    com.prometheus.opencapsule.util.CutoutDetector.getCutoutInfo(
                        context, view.rootWindowInsets?.displayCutout
                    )
                }
                
                CapsuleOverlay(
                    profile = profile,
                    uiState = uiState,
                    onCapsuleClick = { uiManager.toggleExpansion() },
                    cutoutInfo = cutoutInfo
                )
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            combine(repository.profileFlow, uiManager.uiState) { profile, uiState ->
                profile to uiState
            }.collectLatest { (profile, uiState) ->
                composeView?.let { view ->
                    val params = getCapsuleWindowParams(this@CapsuleService, profile, uiState)
                    addOrUpdate(windowManager, view, params)
                }
            }
        }
    }

    private fun observeProfile() {
        // Removed in favor of observeState
    }

    override fun onDestroy() {
        super.onDestroy()
        composeView?.let { safeRemoveView(windowManager, it) }
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
}
