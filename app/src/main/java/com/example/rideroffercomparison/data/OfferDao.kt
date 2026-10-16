package com.example.rideroffercomparison.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OfferDao {

    @Insert
    suspend fun insertOffer(offer: OfferEntity)

    @Query("SELECT * FROM offer_history WHERE platform = :platform ORDER BY timestamp DESC LIMIT 20")
    suspend fun getRecentOffersForPlatform(platform: String): List<OfferEntity>

    @Query("SELECT * FROM offer_history WHERE platform = :platform ORDER BY timestamp DESC LIMIT 20")
    fun observeRecentOffersForPlatform(platform: String): Flow<List<OfferEntity>>

    @Query("DELETE FROM offer_history WHERE timestamp < :cutoffTime")
    suspend fun deleteOlderThan(cutoffTime: Long)

    @Query("DELETE FROM offer_history")
    suspend fun clearAll()
}
