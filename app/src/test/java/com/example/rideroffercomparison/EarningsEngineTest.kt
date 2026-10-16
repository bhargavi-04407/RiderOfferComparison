package com.example.rideroffercomparison

import com.example.rideroffercomparison.engine.EarningsEngine
import com.example.rideroffercomparison.model.Offer
import com.example.rideroffercomparison.model.RiderSettings
import com.example.rideroffercomparison.model.Verdict
import org.junit.Assert.assertEquals
import org.junit.Test

class EarningsEngineTest {

    @Test
    fun testWorkedExampleFromSpec() {
        val settings = RiderSettings(
            fuelCostPerKm = 2.5,
            commissionShare = 0.0,
            avgSpeedKmh = 20.0,
            targetPerHour = 400.0,
            maxPickupKm = 2.0
        )

        // Offer B: Earning 150, pickup 0.4 km, trip 5.0 km
        val offerB = Offer(
            platform = "Rapido",
            serviceType = "Bike",
            earningShown = 150.0,
            pickupDistanceKm = 0.4,
            tripDistanceKm = 5.0
        )

        // Offer A: Earning 180, pickup 1.2 km, trip 8.0 km
        val offerA = Offer(
            platform = "Rapido",
            serviceType = "Bike",
            earningShown = 180.0,
            pickupDistanceKm = 1.2,
            tripDistanceKm = 8.0
        )

        val resB = EarningsEngine.evaluateOffer(offerB, settings)
        val resA = EarningsEngine.evaluateOffer(offerA, settings)

        // Verify Offer B net earning = 150 - (0.4 + 5.0) * 2.5 = 150 - 13.5 = 136.5
        assertEquals(136.5, resB.netEarning, 0.01)
        // Verify Offer B verdict is GOOD
        assertEquals(Verdict.GOOD, resB.resVerdict()) // or resB.verdict
        // Verify Offer B net per hour approx 506
        assertEquals(506.0, resB.netPerHour, 1.0)

        // Verify Offer A net earning = 180 - (1.2 + 8.0) * 2.5 = 180 - 23.0 = 157.0
        assertEquals(157.0, resA.netEarning, 0.01)
        // Verify Offer A verdict is OK
        assertEquals(Verdict.OK, resA.verdict)
        // Verify Offer A net per hour approx 341
        assertEquals(341.0, resA.netPerHour, 1.0)

        // Test ranking
        val ranked = EarningsEngine.rankOffers(listOf(offerB to resB, offerA to resA))
        assertEquals(offerB, ranked[0].first)
        assertEquals(1, ranked[0].third)
        assertEquals(offerA, ranked[1].first)
        assertEquals(2, ranked[1].third)
    }

    private fun com.example.rideroffercomparison.model.OfferEvaluationResult.resVerdict(): Verdict = verdict
}
