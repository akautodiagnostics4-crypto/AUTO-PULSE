package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import com.example.ui.components.TopAppBarHeader
import com.example.ui.screens.driver.DriverConsoleScreen
import com.example.ui.screens.passenger.PassengerHomeScreen
import com.example.ui.theme.AutoGoTheme
import com.example.viewmodel.AutoGoViewModel

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
    val currentMode by viewModel.currentMode.collectAsState()
    val walletBalance by viewModel.walletBalance.collectAsState()

    // Handle back button when in Driver Console mode
    BackHandler(enabled = currentMode == AppMode.DRIVER_CONSOLE) {
        viewModel.switchMode(AppMode.PASSENGER)
    }

    Scaffold(
        topBar = {
            TopAppBarHeader(
                currentMode = currentMode,
                walletBalance = walletBalance,
                onModeChange = { viewModel.switchMode(it) },
                onOpenWallet = { viewModel.openWalletSheet() },
                onOpenHistory = { viewModel.openHistorySheet() }
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
            when (currentMode) {
                AppMode.PASSENGER -> {
                    PassengerHomeScreen(viewModel = viewModel)
                }
                AppMode.DRIVER_CONSOLE -> {
                    DriverConsoleScreen(viewModel = viewModel)
                }
            }
        }
    }
}
