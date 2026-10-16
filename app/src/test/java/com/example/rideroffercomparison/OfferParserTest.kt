package com.example.rideroffercomparison

import com.example.rideroffercomparison.parser.OfferParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class OfferParserTest {

    @Test
    fun testValidOfferParsing() {
        val json = """
            {
                "platform": "Rapido",
                "serviceType": "Bike",
                "earningShown": 150.0,
                "pickupDistanceKm": 0.4,
                "tripDistanceKm": 5.0,
                "customerName": "John Doe",
                "customerPhone": "+919876543210",
                "pickupAddress": "123 Main St"
            }
        """.trimIndent()

        val offer = OfferParser.parseOffer(json)
        assertNotNull(offer)
        assertEquals("Rapido", offer?.platform)
        assertEquals(150.0, offer?.earningShown ?: 0.0, 0.01)
        assertEquals(0.4, offer?.pickupDistanceKm ?: 0.0, 0.01)
        assertEquals(5.0, offer?.tripDistanceKm ?: 0.0, 0.01)
        // Ensure customer name, phone, address are NOT in Offer data class or parsed
    }

    @Test
    fun testFailClosedOnMissingRequiredField() {
        val json = """
            {
                "platform": "Rapido",
                "serviceType": "Bike",
                "pickupDistanceKm": 0.4,
                "tripDistanceKm": 5.0
            }
        """.trimIndent()

        val offer = OfferParser.parseOffer(json)
        assertNull(offer) // missing earningShown -> fails closed
    }

    @Test
    fun testFailClosedOnSanityRange() {
        val json = """
            {
                "platform": "Rapido",
                "serviceType": "Bike",
                "earningShown": 5.0,
                "pickupDistanceKm": 0.4,
                "tripDistanceKm": 5.0
            }
        """.trimIndent()

        val offer = OfferParser.parseOffer(json)
        assertNull(offer) // earning < ₹10 -> fails closed
    }
}
