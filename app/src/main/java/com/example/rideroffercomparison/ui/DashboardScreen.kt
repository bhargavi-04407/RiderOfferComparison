package com.example.rideroffercomparison.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rideroffercomparison.model.Offer
import com.example.rideroffercomparison.model.OfferEvaluationResult
import com.example.rideroffercomparison.model.Verdict

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    masterEnabled: Boolean,
    currentPlatform: String,
    offers: List<Pair<Offer, OfferEvaluationResult>>,
    onToggleMaster: (Boolean) -> Unit,
    onSelectPlatform: (String) -> Unit,
    onSimulateOffer: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val platforms = listOf("Rapido", "Swiggy", "Zomato", "Ola", "Shadowfax")
    
    // FR-1, FR-3, Section 4: Filter live offers strictly by the currently active platform tab
    val filteredOffers = offers.filter { it.first.platform.equals(currentPlatform, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rider Offer Comparison ($currentPlatform)") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Master Switch Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (masterEnabled) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (masterEnabled) "App Active & Reading" else "App Master OFF",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (masterEnabled) "Ready to evaluate incoming offers" else "Reading paused",
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = masterEnabled,
                        onCheckedChange = onToggleMaster
                    )
                }
            }

            // Platform Selection Tabs
            ScrollableTabRow(
                selectedTabIndex = platforms.indexOf(currentPlatform).coerceAtLeast(0),
                edgePadding = 0.dp
            ) {
                platforms.forEach { platform ->
                    Tab(
                        selected = platform == currentPlatform,
                        onClick = { onSelectPlatform(platform) },
                        text = { Text(platform) }
                    )
                }
            }

            // Simulate Offer Button
            Button(
                onClick = onSimulateOffer,
                modifier = Modifier.fillMaxWidth(),
                enabled = masterEnabled
            ) {
                Text("Simulate Incoming Offer for $currentPlatform")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Live Offers Feed ($currentPlatform)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            // Offers List
            if (filteredOffers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No offers for $currentPlatform yet. Tap simulate or open platform app.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredOffers) { (offer, eval) ->
                        OfferCardItem(offer, eval)
                    }
                }
            }
        }
    }
}

@Composable
fun OfferCardItem(offer: Offer, eval: OfferEvaluationResult) {
    val verdictColor = when (eval.verdict) {
        Verdict.GOOD -> Color(0xFF2E7D32) // Green
        Verdict.OK -> Color(0xFFEF6C00)   // Orange
        Verdict.POOR -> Color(0xFFC62828) // Red
        Verdict.REJECTED -> Color.Gray
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${offer.platform} • ${offer.serviceType}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Surface(
                    color = verdictColor,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = eval.verdict.name,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Divider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Gross Earning: ₹${offer.earningShown}")
                Text("Net Earning: ₹${String.format("%.1f", eval.netEarning)}", fontWeight = FontWeight.Bold)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Pickup: ${offer.pickupDistanceKm} km")
                Text("Trip: ${offer.tripDistanceKm} km")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("₹/hour: ₹${String.format("%.0f", eval.netPerHour)}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text("₹/km: ₹${String.format("%.1f", eval.netPerKm)}")
            }

            if (eval.reason != null) {
                Text(
                    text = "Note: ${eval.reason}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
