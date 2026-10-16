package com.example.rideroffercomparison.parser

import com.example.rideroffercomparison.model.Offer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object OfferParser {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Parses an incoming raw offer JSON string, strictly filtering to the allowlisted fields
     * and discarding any customer data (name, phone, address, store name, payment mode, etc.).
     * Fails closed (returns null) if required fields are missing or invalid.
     */
    fun parseOffer(rawJson: String, defaultPlatform: String = "Rapido"): Offer? {
        return try {
            val element = json.parseToJsonElement(rawJson)
            val obj = element.jsonObject

            // Detect or take platform
            val platform = obj["platform"]?.jsonPrimitive?.content ?: defaultPlatform
            val serviceType = obj["serviceType"]?.jsonPrimitive?.content ?: "Bike"

            val earningShown = obj["earningShown"]?.jsonPrimitive?.doubleOrNull
                ?: obj["earning"]?.jsonPrimitive?.doubleOrNull
                ?: return null

            val incentiveShown = obj["incentiveShown"]?.jsonPrimitive?.doubleOrNull
                ?: obj["incentive"]?.jsonPrimitive?.doubleOrNull
                ?: 0.0

            val pickupDistanceKm = obj["pickupDistanceKm"]?.jsonPrimitive?.doubleOrNull
                ?: obj["pickupDistance"]?.jsonPrimitive?.doubleOrNull
                ?: 0.0

            val pickupEtaMinutes = obj["pickupEtaMinutes"]?.jsonPrimitive?.doubleOrNull
                ?: obj["pickupEta"]?.jsonPrimitive?.doubleOrNull

            val tripDistanceKm = obj["tripDistanceKm"]?.jsonPrimitive?.doubleOrNull
                ?: obj["tripDistance"]?.jsonPrimitive?.doubleOrNull
                ?: 0.0

            val tripTimeMinutes = obj["tripTimeMinutes"]?.jsonPrimitive?.doubleOrNull
                ?: obj["tripTime"]?.jsonPrimitive?.doubleOrNull

            val orderId = obj["orderId"]?.jsonPrimitive?.content

            // Sanity range check on earning
            val totalEarning = earningShown + incentiveShown
            if (totalEarning < 10.0 || totalEarning > 5000.0) {
                return null
            }

            // Construct strictly allowlisted Offer object (zero customer info can enter)
            Offer(
                platform = platform,
                serviceType = serviceType,
                earningShown = earningShown,
                incentiveShown = incentiveShown,
                pickupDistanceKm = pickupDistanceKm,
                pickupEtaMinutes = pickupEtaMinutes,
                tripDistanceKm = tripDistanceKm,
                tripTimeMinutes = tripTimeMinutes,
                orderId = orderId
            )
        } catch (e: Exception) {
            // Fail closed on any parse exception or garbled data
            null
        }
    }

    /**
     * Parses multiple offers from a JSON array or list payload.
     */
    fun parseOffers(rawJsonArray: String, defaultPlatform: String = "Rapido"): List<Offer> {
        return try {
            val element = json.parseToJsonElement(rawJsonArray)
            if (element is kotlinx.serialization.json.JsonArray) {
                element.mapNotNull { item ->
                    parseOffer(item.toString(), defaultPlatform)
                }
            } else {
                listOfNotNull(parseOffer(rawJsonArray, defaultPlatform))
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
