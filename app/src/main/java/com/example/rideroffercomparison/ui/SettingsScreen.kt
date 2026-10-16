package com.example.rideroffercomparison.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rideroffercomparison.model.RiderSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentPlatform: String,
    settings: RiderSettings,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    onSaveSettings: (RiderSettings) -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    var fuelCost by remember(settings) { mutableStateOf(settings.fuelCostPerKm.toString()) }
    var commission by remember(settings) { mutableStateOf((settings.commissionShare * 100).toString()) }
    var avgSpeed by remember(settings) { mutableStateOf(settings.avgSpeedKmh.toString()) }
    var targetHour by remember(settings) { mutableStateOf(settings.targetPerHour.toString()) }
    var maxPickup by remember(settings) { mutableStateOf(settings.maxPickupKm.toString()) }

    val isHindi = currentLanguage == "hi"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isHindi) "$currentPlatform - सेटिंग्स" else "Settings for $currentPlatform") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(if (isHindi) "पीछे" else "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Language selection row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = if (isHindi) "भाषा (Language): हिंदी" else "Language: English", fontWeight = FontWeight.Bold)
                Switch(
                    checked = isHindi,
                    onCheckedChange = { onLanguageChange(if (it) "hi" else "en") }
                )
            }

            Divider()

            Text(
                text = if (isHindi) "कस्टम कैलकुलेशन पैरामीटर" else "Customize Calculation Parameters",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = fuelCost,
                onValueChange = { fuelCost = it },
                label = { Text(if (isHindi) "इंधन लागत प्रति किमी (₹)" else "Fuel & Running Cost per km (₹)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = commission,
                onValueChange = { commission = it },
                label = { Text(if (isHindi) "प्लेटफॉर्म कमीशन (%)" else "Platform Commission (%)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = avgSpeed,
                onValueChange = { avgSpeed = it },
                label = { Text(if (isHindi) "औसत गति (किमी/घंटा)" else "Average Speed (km/h)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = targetHour,
                onValueChange = { targetHour = it },
                label = { Text(if (isHindi) "प्रति घंटे शुद्ध कमाई लक्ष्य (₹)" else "Target Net Earnings per Hour (₹)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = maxPickup,
                onValueChange = { maxPickup = it },
                label = { Text(if (isHindi) "अधिकतम पिकअप दूरी (किमी)" else "Max Pickup Distance (km)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val newSettings = RiderSettings(
                        fuelCostPerKm = fuelCost.toDoubleOrNull() ?: settings.fuelCostPerKm,
                        commissionShare = (commission.toDoubleOrNull() ?: 0.0) / 100.0,
                        avgSpeedKmh = avgSpeed.toDoubleOrNull() ?: settings.avgSpeedKmh,
                        targetPerHour = targetHour.toDoubleOrNull() ?: settings.targetPerHour,
                        maxPickupKm = maxPickup.toDoubleOrNull() ?: settings.maxPickupKm
                    )
                    onSaveSettings(newSettings)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isHindi) "सेटिंग्स सहेजें" else "Save Settings")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout Button (FR-11)
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(if (isHindi) "लॉग आउट (खाता रीसेट)" else "Logout / Reset Account")
            }
        }
    }
}
