package com.example.rideroffercomparison.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.rideroffercomparison.model.RiderSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "rider_preferences")

class RiderPreferencesRepository(private val context: Context) {

    object PreferencesKeys {
        val MASTER_ENABLED = booleanPreferencesKey("master_enabled")
        val PHONE_NUMBER = stringPreferencesKey("phone_number")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")

        fun platformEnabled(platform: String) = booleanPreferencesKey("platform_enabled_$platform")
        fun fuelCost(platform: String) = doublePreferencesKey("fuel_cost_$platform")
        fun commission(platform: String) = doublePreferencesKey("commission_$platform")
        fun avgSpeed(platform: String) = doublePreferencesKey("avg_speed_$platform")
        fun targetPerHour(platform: String) = doublePreferencesKey("target_per_hour_$platform")
        fun maxPickup(platform: String) = doublePreferencesKey("max_pickup_$platform")
    }

    val masterEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[PreferencesKeys.MASTER_ENABLED] ?: true }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[PreferencesKeys.IS_LOGGED_IN] ?: false }

    fun getSettingsForPlatform(platform: String): Flow<RiderSettings> {
        return context.dataStore.data.map { prefs ->
            RiderSettings(
                fuelCostPerKm = prefs[PreferencesKeys.fuelCost(platform)] ?: 2.5,
                commissionShare = prefs[PreferencesKeys.commission(platform)] ?: 0.0,
                avgSpeedKmh = prefs[PreferencesKeys.avgSpeed(platform)] ?: 20.0,
                targetPerHour = prefs[PreferencesKeys.targetPerHour(platform)] ?: 400.0,
                maxPickupKm = prefs[PreferencesKeys.maxPickup(platform)] ?: 2.0
            )
        }
    }

    fun isPlatformEnabledFlow(platform: String): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[PreferencesKeys.platformEnabled(platform)] ?: true
        }
    }

    suspend fun setMasterEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.MASTER_ENABLED] = enabled
        }
    }

    suspend fun setPlatformEnabled(platform: String, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.platformEnabled(platform)] = enabled
        }
    }

    suspend fun savePlatformSettings(platform: String, settings: RiderSettings) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.fuelCost(platform)] = settings.fuelCostPerKm
            prefs[PreferencesKeys.commission(platform)] = settings.commissionShare
            prefs[PreferencesKeys.avgSpeed(platform)] = settings.avgSpeedKmh
            prefs[PreferencesKeys.targetPerHour(platform)] = settings.targetPerHour
            prefs[PreferencesKeys.maxPickup(platform)] = settings.maxPickupKm
        }
    }

    suspend fun setLoginState(isLoggedIn: Boolean, phoneNumber: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.IS_LOGGED_IN] = isLoggedIn
            prefs[PreferencesKeys.PHONE_NUMBER] = phoneNumber
        }
    }
}
