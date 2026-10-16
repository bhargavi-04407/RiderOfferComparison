package com.example.rideroffercomparison.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rideroffercomparison.data.AppDatabase
import com.example.rideroffercomparison.data.OfferEntity
import com.example.rideroffercomparison.data.RiderPreferencesRepository
import com.example.rideroffercomparison.engine.EarningsEngine
import com.example.rideroffercomparison.model.Offer
import com.example.rideroffercomparison.model.OfferEvaluationResult
import com.example.rideroffercomparison.model.RiderSettings
import com.example.rideroffercomparison.service.OfferAccessibilityService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepository = RiderPreferencesRepository(application)
    private val offerDao = AppDatabase.getDatabase(application).offerDao()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _masterEnabled = MutableStateFlow(true)
    val masterEnabled: StateFlow<Boolean> = _masterEnabled.asStateFlow()

    private val _liveOffers = MutableStateFlow<List<Pair<Offer, OfferEvaluationResult>>>(emptyList())
    val liveOffers: StateFlow<List<Pair<Offer, OfferEvaluationResult>>> = _liveOffers.asStateFlow()

    private val _selectedPlatform = MutableStateFlow("Rapido")
    val selectedPlatform: StateFlow<String> = _selectedPlatform.asStateFlow()

    private val _riderSettings = MutableStateFlow(RiderSettings())
    val riderSettings: StateFlow<RiderSettings> = _riderSettings.asStateFlow()

    private val _language = MutableStateFlow("en")
    val language: StateFlow<String> = _language.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.isLoggedInFlow.collect { _isLoggedIn.value = it }
        }
        viewModelScope.launch {
            preferencesRepository.masterEnabledFlow.collect { _masterEnabled.value = it }
        }
        viewModelScope.launch {
            selectedPlatform.collect { platform ->
                preferencesRepository.getSettingsForPlatform(platform).collect {
                    _riderSettings.value = it
                }
            }
        }

        // Collect live accessibility offers
        viewModelScope.launch {
            OfferAccessibilityService.offerFlow.collect { offer ->
                if (_masterEnabled.value) {
                    processIncomingOffer(offer)
                }
            }
        }
    }

    fun login(phoneNumber: String) {
        viewModelScope.launch {
            preferencesRepository.setLoginState(true, phoneNumber)
            _isLoggedIn.value = true
        }
    }

    fun logout() {
        viewModelScope.launch {
            preferencesRepository.setLoginState(false, "")
            _isLoggedIn.value = false
            _liveOffers.value = emptyList()
        }
    }

    fun setLanguage(lang: String) {
        _language.value = lang
    }

    fun setMasterEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setMasterEnabled(enabled)
            _masterEnabled.value = enabled
        }
    }

    fun selectPlatform(platform: String) {
        _selectedPlatform.value = platform
    }

    fun updateSettings(settings: RiderSettings) {
        viewModelScope.launch {
            preferencesRepository.savePlatformSettings(_selectedPlatform.value, settings)
            _riderSettings.value = settings
        }
    }

    fun processIncomingOffer(offer: Offer) {
        viewModelScope.launch {
            val settings = _riderSettings.value
            val evaluation = EarningsEngine.evaluateOffer(offer, settings)

            // Save to Room history
            if (evaluation.verdict != com.example.rideroffercomparison.model.Verdict.REJECTED) {
                offerDao.insertOffer(
                    OfferEntity(
                        platform = offer.platform,
                        serviceType = offer.serviceType,
                        earningShown = offer.earningShown,
                        netEarning = evaluation.netEarning,
                        netPerHour = evaluation.netPerHour,
                        netPerKm = evaluation.netPerKm,
                        pickupDistanceKm = offer.pickupDistanceKm,
                        tripDistanceKm = offer.tripDistanceKm,
                        verdict = evaluation.verdict.name
                    )
                )
            }

            val currentList = _liveOffers.value.toMutableList()
            currentList.add(0, offer to evaluation)
            // Keep recent on screen
            if (currentList.size > 10) currentList.removeAt(currentList.size - 1)
            _liveOffers.value = currentList
        }
    }

    fun simulateTestOffer() {
        val testOffer = Offer(
            platform = _selectedPlatform.value,
            serviceType = "Bike",
            earningShown = (30..300).random().toDouble(),
            pickupDistanceKm = (2..30).random() / 10.0,
            tripDistanceKm = (10..150).random() / 10.0
        )
        processIncomingOffer(testOffer)
    }
}
