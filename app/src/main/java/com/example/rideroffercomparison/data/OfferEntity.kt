package com.example.rideroffercomparison.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offer_history")
data class OfferEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val platform: String,
    val serviceType: String,
    val earningShown: Double,
    val netEarning: Double,
    val netPerHour: Double,
    val netPerKm: Double,
    val pickupDistanceKm: Double,
    val tripDistanceKm: Double,
    val verdict: String,
    val timestamp: Long = System.currentTimeMillis()
)
