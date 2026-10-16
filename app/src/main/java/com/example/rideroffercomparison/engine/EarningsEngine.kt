package com.example.rideroffercomparison.engine

import com.example.rideroffercomparison.model.Offer
import com.example.rideroffercomparison.model.OfferEvaluationResult
import com.example.rideroffercomparison.model.RiderSettings
import com.example.rideroffercomparison.model.Verdict

object EarningsEngine {

    fun evaluateOffer(offer: Offer, settings: RiderSettings): OfferEvaluationResult {
        // Sanity check on earnings (e.g. ₹10 to ₹5,000)
        val grossEarning = offer.earningShown + offer.incentiveShown
        if (grossEarning < 10.0 || grossEarning > 5000.0) {
            return OfferEvaluationResult(
                netEarning = 0.0,
                netPerHour = 0.0,
                netPerKm = 0.0,
                verdict = Verdict.REJECTED,
                reason = "Earning out of sanity range (₹10–₹5,000)"
            )
        }

        // Distance checks
        val totalDistance = offer.pickupDistanceKm + offer.tripDistanceKm
        if (totalDistance < 0.0 || offer.pickupDistanceKm < 0.0 || offer.tripDistanceKm < 0.0) {
            return OfferEvaluationResult(
                netEarning = 0.0,
                netPerHour = 0.0,
                netPerKm = 0.0,
                verdict = Verdict.REJECTED,
                reason = "Invalid negative distance"
            )
        }

        // Net earning calculation:
        // Net = (Earning + Incentive) * (1 - c) - (d_pickup + d_trip) * k
        val netEarning = grossEarning * (1.0 - settings.commissionShare) - totalDistance * settings.fuelCostPerKm

        // Time calculation (in minutes)
        val pickupTimeMin = offer.pickupEtaMinutes ?: if (settings.avgSpeedKmh > 0) {
            (offer.pickupDistanceKm / settings.avgSpeedKmh) * 60.0
        } else {
            0.0
        }

        val tripTimeMin = offer.tripTimeMinutes ?: if (settings.avgSpeedKmh > 0) {
            (offer.tripDistanceKm / settings.avgSpeedKmh) * 60.0
        } else {
            0.0
        }

        val totalTimeMin = pickupTimeMin + tripTimeMin

        val netPerHour = if (totalTimeMin > 0.0) {
            (netEarning / totalTimeMin) * 60.0
        } else {
            0.0
        }

        val netPerKm = if (totalDistance > 0.0) {
            netEarning / totalDistance
        } else {
            0.0
        }

        // Verdict rules:
        // Good: net ₹/hour at or above target, and pickup within max km.
        // Poor: net ₹/hour below 70% of target, or pickup over max km.
        // OK: everything else.
        val verdict = when {
            offer.pickupDistanceKm > settings.maxPickupKm || netPerHour < (settings.targetPerHour * 0.7) -> Verdict.POOR
            netPerHour >= settings.targetPerHour && offer.pickupDistanceKm <= settings.maxPickupKm -> Verdict.GOOD
            else -> Verdict.OK
        }

        return OfferEvaluationResult(
            netEarning = netEarning,
            netPerHour = netPerHour,
            netPerKm = netPerKm,
            verdict = verdict
        )
    }

    fun rankOffers(evaluations: List<Pair<Offer, OfferEvaluationResult>>): List<Triple<Offer, OfferEvaluationResult, Int>> {
        // Sort descending by netPerHour
        val sorted = evaluations.sortedByDescending { it.second.netPerHour }
        var currentRank = 1
        return sorted.mapIndexed { index, pair ->
            if (index > 0) {
                val prev = sorted[index - 1].second.netPerHour
                val curr = pair.second.netPerHour
                // Within 5% counts as "similar", but assign distinct ranks or grouped ranks. Here standard rank 1, 2...
                // If we want exact rank:
                currentRank = index + 1
            }
            Triple(pair.first, pair.second, currentRank)
        }
    }
}
