package com.example.rideroffercomparison.model

import kotlinx.serialization.Serializable

@Serializable
data class Offer(
    val platform: String,
    val serviceType: String,
    val earningShown: Double,
    val incentiveShown: Double = 0.0,
    val pickupDistanceKm: Double = 0.0,
    val pickupEtaMinutes: Double? = null,
    val tripDistanceKm: Double = 0.0,
    val tripTimeMinutes: Double? = null,
    val orderId: String? = null
)

@Serializable
data class RiderSettings(
    val fuelCostPerKm: Double = 2.5,
    val commissionShare: Double = 0.0, // e.g. 0.0 to 1.0 (0% to 100%)
    val avgSpeedKmh: Double = 20.0,
    val targetPerHour: Double = 400.0,
    val maxPickupKm: Double = 2.0
)

enum class Verdict {
    GOOD,
    OK,
    POOR,
    REJECTED
}

data class OfferEvaluationResult(
    val netEarning: Double,
    val netPerHour: Double,
    val netPerKm: Double,
    val verdict: Verdict,
    val reason: String? = null
)
