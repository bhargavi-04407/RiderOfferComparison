package com.example.rideroffercomparison

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.rideroffercomparison.ui.DashboardScreen
import com.example.rideroffercomparison.ui.LoginScreen
import com.example.rideroffercomparison.ui.MainViewModel
import com.example.rideroffercomparison.ui.SettingsScreen
import com.example.rideroffercomparison.ui.theme.RiderOfferComparisonTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Section 6 Strict Compliance: Block screenshots and screen recordings
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        enableEdgeToEdge()
        setContent {
            RiderOfferComparisonTheme {
                val isLoggedIn by viewModel.isLoggedIn.collectAsState()
                val masterEnabled by viewModel.masterEnabled.collectAsState()
                val currentPlatform by viewModel.selectedPlatform.collectAsState()
                val offers by viewModel.liveOffers.collectAsState()
                val riderSettings by viewModel.riderSettings.collectAsState()
                val currentLanguage by viewModel.language.collectAsState()

                var currentScreen by rememberSaveable { mutableStateOf("dashboard") }

                if (!isLoggedIn) {
                    LoginScreen(
                        currentLanguage = currentLanguage,
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onLoginSuccess = { phoneNumber ->
                            viewModel.login(phoneNumber)
                        }
                    )
                } else {
                    when (currentScreen) {
                        "dashboard" -> {
                            DashboardScreen(
                                masterEnabled = masterEnabled,
                                currentPlatform = currentPlatform,
                                offers = offers,
                                onToggleMaster = { viewModel.setMasterEnabled(it) },
                                onSelectPlatform = { viewModel.selectPlatform(it) },
                                onSimulateOffer = { viewModel.simulateTestOffer() },
                                onOpenSettings = { currentScreen = "settings" }
                            )
                        }
                        "settings" -> {
                            SettingsScreen(
                                currentPlatform = currentPlatform,
                                settings = riderSettings,
                                currentLanguage = currentLanguage,
                                onLanguageChange = { viewModel.setLanguage(it) },
                                onSaveSettings = { viewModel.updateSettings(it) },
                                onLogout = {
                                    viewModel.logout()
                                    currentScreen = "dashboard"
                                },
                                onBack = { currentScreen = "dashboard" }
                            )
                        }
                    }
                }
            }
        }
    }
}
