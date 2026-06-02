package com.prometheus.opencapsule.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.prometheus.opencapsule.ui.theme.OpenCapsuleTheme
import com.prometheus.opencapsule.viewmodel.CapsuleServiceViewModel
import dagger.hilt.android.AndroidEntryPoint

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OpenCapsuleTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(
                            onNavigateToStyle = { navController.navigate("style") }
                        )
                    }
                    composable("style") {
                        CapsuleStyleScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    onNavigateToStyle: () -> Unit,
    viewModel: CapsuleServiceViewModel = hiltViewModel()
) {
    val isEnabled by viewModel.isServiceEnabled.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    // Periodically update status when user returns to app
    LifecycleResumeEffect (Unit) {
        viewModel.updateServiceStatus()
        onPauseOrDispose {  }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StatusIndicator(isEnabled)

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                context.startActivity(intent)
            }) {
                Text(text = "Enable Capsule")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onNavigateToStyle) {
                Text(text = "Style Capsule")
            }
        }
    }
}

@Composable
fun StatusIndicator(isEnabled: Boolean){
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Status: ",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = if (isEnabled) "ACTIVE" else "DISABLED",
            color = if (isEnabled) Color(0xFF4CAF50) else Color.Red,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

