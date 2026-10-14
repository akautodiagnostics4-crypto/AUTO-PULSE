package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.model.AppMode
import com.example.ui.components.RoleSelectionDialog
import com.example.ui.components.TopAppBarHeader
import com.example.ui.screens.auth.SplitAuthScreen
import com.example.ui.screens.driver.DriverConsoleScreen
import com.example.ui.screens.driver.VehicleRegistrationDialog
import com.example.ui.screens.passenger.PassengerHomeScreen
import com.example.ui.theme.AutoGoTheme
import com.example.viewmodel.AutoGoViewModel

/**
 * Single Activity container for the Pollachi AutoGo application.
 * Switches between 'Rider' and 'Driver' screens seamlessly using the
 * shared AutoGoViewModel state without creating new activities.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: AutoGoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutoGoTheme {
                AutoGoApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AutoGoApp(viewModel: AutoGoViewModel) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    val currentMode by viewModel.currentMode.collectAsState()
    val walletBalance by viewModel.walletBalance.collectAsState()
    val showRoleChooser by viewModel.showRoleChooserDialog.collectAsState()
    val showRegistrationDialog by viewModel.showVehicleRegistrationSheet.collectAsState()
    val currentDriverProfile by viewModel.currentDriverProfile.collectAsState()

    Crossfade(targetState = isAuthenticated, label = "auth_crossfade") { authenticated ->
        if (!authenticated) {
            SplitAuthScreen(viewModel = viewModel)
        } else {
            // Handle back button when in Driver Console mode: return to Passenger without reloading activities
            BackHandler(enabled = true) {
                if (currentMode == AppMode.DRIVER_CONSOLE) {
                    viewModel.switchMode(AppMode.PASSENGER)
                } else {
                    viewModel.logout()
                }
            }

            Scaffold(
                topBar = {
                    TopAppBarHeader(
                        currentMode = currentMode,
                        walletBalance = walletBalance,
                        onModeChange = { newMode -> viewModel.switchMode(newMode) },
                        onOpenWallet = { viewModel.openWalletSheet() },
                        onOpenHistory = { viewModel.openHistorySheet() },
                        onOpenRoleChooser = { viewModel.openRoleChooser() },
                        onLogout = { viewModel.logout() }
                    )
                },
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Simple, fluid view switcher driven by shared ViewModel state without creating new activities
                    AnimatedContent(
                        targetState = currentMode,
                        transitionSpec = {
                            if (targetState == AppMode.DRIVER_CONSOLE) {
                                (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                    slideOutHorizontally { width -> -width } + fadeOut()
                                )
                            } else {
                                (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                    slideOutHorizontally { width -> width } + fadeOut()
                                )
                            }.using(
                                SizeTransform(clip = false)
                            )
                        },
                        label = "shared_vm_view_switcher_anim"
                    ) { mode ->
                        when (mode) {
                            AppMode.PASSENGER -> {
                                PassengerHomeScreen(viewModel = viewModel)
                            }
                            AppMode.DRIVER_CONSOLE -> {
                                DriverConsoleScreen(viewModel = viewModel)
                            }
                        }
                    }

                    // Role selection dialog
                    if (showRoleChooser) {
                        RoleSelectionDialog(
                            currentMode = currentMode,
                            onSelectRole = { selectedRole -> viewModel.selectRole(selectedRole) },
                            onDismiss = { viewModel.closeRoleChooser() }
                        )
                    }

                    // Driver vehicle and document registration dialog
                    if (showRegistrationDialog) {
                        VehicleRegistrationDialog(
                            currentProfile = currentDriverProfile,
                            onSaveRegistration = { reg -> viewModel.registerVehicle(reg) },
                            onDismiss = { viewModel.closeVehicleRegistration() }
                        )
                    }
                }
            }
        }
    }
}
